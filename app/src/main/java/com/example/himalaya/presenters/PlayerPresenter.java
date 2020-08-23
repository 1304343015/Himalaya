package com.example.himalaya.presenters;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.himalaya.api.XmlyApi;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IPlayerPresenter;
import com.example.himalaya.interfaces.IPlayerViewCallback;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {
    private static PlayerPresenter sPlayerPresenter;
    private boolean isSetTrackList=false;
    private XmPlayerManager playerManager;
    private static final String TAG = "PlayerPresenter";
    private Track curTrack;
    private int playIndex;
    private List<IPlayerViewCallback> callbacks=new ArrayList<>();

    private final static int PLAY_MODEL_LIST_INT=0;
    private final static int PLAY_MODEL_LIST_LOOP_INT=1;
    private final static int PLAY_MODEL_RANDOM_INT=2;
    private final static int PLAY_MODEL_SINGLE_LOOP_INT=3;

    private int currentMode=PLAY_MODEL_LIST_INT;

    private SharedPreferences sp;

    private final static String SP_NAME="PlayMode";
    private final static String SP_KEY="currentPlayMode";
    private int currentPostion;
    private int duration;

    /**
     * 初始化播放器
     */
    private PlayerPresenter(){
        playerManager=XmPlayerManager.getInstance(BaseApplication.getContext());
        playerManager.addAdsStatusListener(this);
        playerManager.addPlayerStatusListener(this);
        sp=BaseApplication.getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    };

    /**
     * 单例模式
     * @return
     */
    public static PlayerPresenter getInstance(){
        if(sPlayerPresenter==null){
            synchronized (PlayerPresenter.class){
                if(sPlayerPresenter==null)
                    sPlayerPresenter=new PlayerPresenter();
            }
        }
        return sPlayerPresenter;
    }

    /**
     * 设置播放列表，和当前播放序号
     * @param list
     * @param index
     */
    public void setTrackListAndIndex(List<Track> list,int index){
        if(playerManager!=null){
            playIndex=index;
            curTrack=list.get(index);
            playerManager.setPlayList(list,index);
            isSetTrackList=true;
        }else{
            LogUtil.d(TAG,"set error");
        }

    }

    public boolean hasAlbumList(){
        return isSetTrackList;
    }

    /**
     * 开始播放音乐
     */
    @Override
    public void start() {
        LogUtil.d(TAG,"start");
        if(isSetTrackList){
            playerManager.play();
            LogUtil.d(TAG,"list size:"+callbacks.size());
        }
    }

    /**
     * 设置播放列表
     * @param list  声音列表
     * @param startIndex 播放声音序列
     */
    private void PlayList(List<Track> list, int startIndex){
        if(isSetTrackList){
            playerManager.playList(list,startIndex);

        }
    }

    public void playAlbumById(int id){
        XmlyApi xmlyApi = XmlyApi.getXmlyApi();
        xmlyApi.getTracks(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                List<Track> list=trackList.getTracks();
                if (list != null) {
                    setTrackListAndIndex(list,0);
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(BaseApplication.getContext(),"获取数据错误",Toast.LENGTH_LONG);
            }
        },id,1);
    }

    /**
     * 播放器是否播放
     * @return 是否正在播放
     */
    public boolean isPlaying(){
        if(playerManager!=null)
            return playerManager.isPlaying();
        return false;
    }

    /**
     * 播放暂停
     */
    @Override
    public void pause() {
        LogUtil.d(TAG,"pause");
        if(isSetTrackList){
            playerManager.pause();
        }
    }

    @Override
    public void stop() {
        if(isSetTrackList){
            playerManager.stop();
        }
    }

    @Override
    public void playPre() {
        if (playerManager != null) {
            playerManager.playPre();
            for (IPlayerViewCallback callback : callbacks) {
                callback.onChangePre();
            }
        }

    }

    @Override
    public void playNext() {
        if (sPlayerPresenter != null) {
            playerManager.playNext();
            for (IPlayerViewCallback callback : callbacks) {
                callback.onChangeNext();
            }
        }

    }

    /**
     * 转换播放模式
     * @param mode  播放模式
     */
    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        playerManager.setPlayMode(mode);
        for (IPlayerViewCallback callback : callbacks) {
            callback.onSwitchPlayMode(mode);
        }
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt(SP_KEY,getIntByPlayMode(mode));
        editor.commit();
    }

    private int getIntByPlayMode(XmPlayListControl.PlayMode mode){
        switch (mode){
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
        }
        return PLAY_MODEL_LIST_INT;
    }

    public XmPlayListControl.PlayMode getPlayModeByInt(int int_mode){
        switch (int_mode) {
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
        }
        return PLAY_MODEL_LIST;
    }

    /**
     * 滑动SeekBar
     * @param position 位置
     */
    @Override
    public void seekTo(int position) {
        playerManager.seekTo(position);
    }

    @Override
    public void getList() {
        LogUtil.d(TAG,"getList");
        if(playerManager!=null){
            List<Track> trackList=playerManager.getPlayList();
            for (IPlayerViewCallback callback : callbacks) {
                callback.onListLoaded(trackList);
            }
        }
    }

    @Override
    public void playByIndex(int index) {
        if(playerManager!=null){
            playerManager.play(index);
        }
    }

    @Override
    public void registerViewListener(IPlayerViewCallback iPlayerViewCallback) {
        if(!callbacks.contains(iPlayerViewCallback)){
            callbacks.add(iPlayerViewCallback);
        }
        switchPlayMode(getPlayModeByInt(sp.getInt(SP_KEY,PLAY_MODEL_LIST_INT)));
        getList();
            iPlayerViewCallback.onTrackUpdate(curTrack,playIndex);
        if(playerManager.isPlaying()){
            for (IPlayerViewCallback callback : callbacks) {
                callback.onPlayStart();
            }
        }
        iPlayerViewCallback.onProgressChange(currentPostion,duration);
    }

    @Override
    public void unRegisterViewListener(IPlayerViewCallback iPlayerViewCallback) {
        callbacks.remove(iPlayerViewCallback);
    }


    //============================广告监听器开始==============================
    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG,"onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG,"onGetAdsInfo");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG,"onAdsStartBuffering");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG,"onAdsStopBuffering");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG,"onStartPlayAds");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG,"onStartPlayAds");
    }

    @Override
    public void onError(int i, int i1) {
        LogUtil.d(TAG,"onError:error code:"+i+"  errExa:"+i1);
    }

    //============================广告监听器结束==============================

    //============================内容监听器开始==============================

    @Override
    public void onPlayStart() {
        LogUtil.d(TAG,"onPlayStart");
        for (IPlayerViewCallback callback : callbacks) {
            callback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG,"onPlayPause");
        for (IPlayerViewCallback callback : callbacks) {
            callback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG,"onPlayStop");
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG,"onSoundPlayComplete");
    }

    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG,"onSoundPrepared");
        playerManager.play();
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel ) {
        LogUtil.d(TAG,"onSoundSwitch");
        int curIndex=playerManager.getCurrentIndex();
        if(curModel instanceof  Track){
            Track curTrack=(Track)curModel;
            for (IPlayerViewCallback callback : callbacks) {
                callback.onTrackUpdate(curTrack,curIndex);
            }
        }
    }

    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG,"onBufferingStart");
    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG,"onBufferingStop");
    }

    @Override
    public void onBufferProgress(int percent) {
        LogUtil.d(TAG,"onBufferProgress percent:"+percent);
    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
        this.currentPostion=currPos;
        this.duration=duration;
        LogUtil.d(TAG,"onPlayProgress: currPos:"+currPos+"   duration:"+duration);
        for (IPlayerViewCallback callback : callbacks) {
            callback.onProgressChange(currPos,duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG,e.getMessage());
        return false;
    }

    //============================内容监听器结束==============================
}
