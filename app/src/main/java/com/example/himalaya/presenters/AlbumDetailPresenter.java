package com.example.himalaya.presenters;

import android.annotation.TargetApi;

import com.example.himalaya.interfaces.IAlbumDetailPresenter;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {
    private static final String TAG = "AlbumDetailPresenter";
    private List<IAlbumDetailViewCallback> callbacks=new ArrayList<>();
    private Album targetAlbum=null;
    private static AlbumDetailPresenter albumDetailPresenter;

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

    }

    @Override
    public void loadDetailList(int albumId,int pageNum) {
        loading();
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageNum+"");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                List<Track> list=trackList.getTracks();
                if(list!=null){
                    for (Track track : list) {
                        LogUtil.d(TAG,"success:"+track);
                    }
                    handlerSuccess(list);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG,"error code:-->"+i);
                LogUtil.d(TAG,"error message:-->"+s);
                networkError();
            }
        });
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
    public void registerOnDetailViewListener(IAlbumDetailViewCallback onDetailViewListener) {
        if(!callbacks.contains(onDetailViewListener)){
            callbacks.add(onDetailViewListener);
            if(targetAlbum!=null)
            onDetailViewListener.onAlbumInfoLoad(targetAlbum);
        }
    }

    @Override
    public void unRegisterOnDetailViewListener(IAlbumDetailViewCallback onDetailViewListener) {
        callbacks.remove(onDetailViewListener);
    }
}
