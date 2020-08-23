package com.example.himalaya.presenters;



import com.example.himalaya.api.XmlyApi;
import com.example.himalaya.interfaces.IAlbumDetailPresenter;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import java.util.ArrayList;
import java.util.List;


public class AlbumDetailPresenter implements IAlbumDetailPresenter {
    private static final String TAG = "AlbumDetailPresenter";
    private List<IAlbumDetailViewCallback> callbacks=new ArrayList<>();
    private Album targetAlbum=null;
    private static AlbumDetailPresenter albumDetailPresenter;
    private List<Track> curTrackList=new ArrayList<>();
    private int loadPage=1;
    private AlbumDetailPresenter(){};

    public static AlbumDetailPresenter getInstance(){
        if(albumDetailPresenter==null){
            synchronized (AlbumDetailPresenter.class){
                if(albumDetailPresenter==null)
                    albumDetailPresenter=new AlbumDetailPresenter();
            }
        }
        return albumDetailPresenter;
    }
    @Override
    public void pull2refresh() {

    }

    @Override
    public void loadMore() {
        loadPage++;
        doLoad(true);
    }

    public void doLoad(final boolean isLoadMore){
        XmlyApi xmlyApi= XmlyApi.getXmlyApi();
        xmlyApi.getTracks(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                List<Track> list=trackList.getTracks();
                if(list!=null){
                    for (Track track : list) {
                        LogUtil.d(TAG,"success:"+track);
                    }
                    if(isLoadMore){
                        curTrackList.addAll(list);
                        for (IAlbumDetailViewCallback callback : callbacks) {
                            callback.onLoadMoreFinished(list.size());
                        }
                    }else{
                        curTrackList.addAll(0,list);
                    }
                    handlerSuccess(curTrackList);
                }
            }

            @Override
            public void onError(int i, String s) {
                loadPage--;
                LogUtil.d(TAG,"error code:-->"+i);
                LogUtil.d(TAG,"error message:-->"+s);
                networkError();
            }
        }, (int) targetAlbum.getId(),loadPage);
    }
    @Override
    public void loadDetailList(int albumId,int pageNum) {
        curTrackList.clear();
        loadPage=pageNum;
        doLoad(false);

    }

    @Override
    public void loading() {
        for (IAlbumDetailViewCallback callback : callbacks) {
            callback.onLoading();
        }
    }

    @Override
    public void networkError() {
        for (IAlbumDetailViewCallback callback : callbacks) {
            callback.onNetworkError();
        }
    }

    private void handlerSuccess(List<Track> trackList) {
        for (IAlbumDetailViewCallback callback : callbacks) {
            callback.onDetailListLoad(trackList);
        }
    }

    public void setTargetAlbum(Album album){
        targetAlbum=album;
    }


    @Override
    public void registerViewListener(IAlbumDetailViewCallback callback) {
        if(!callbacks.contains(callback)){
            callbacks.add(callback);
            if(targetAlbum!=null){
                callback.onAlbumInfoLoad(targetAlbum);
            }


        }
    }

    @Override
    public void unRegisterViewListener(IAlbumDetailViewCallback callback) {
        callbacks.remove(callback);
    }
}
