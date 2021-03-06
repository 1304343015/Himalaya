package com.example.himalaya.presenters;

import com.example.himalaya.api.XmlyApi;
import com.example.himalaya.interfaces.IReCommendViewCallback;
import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPresenter implements IRecommendPresenter {
    private static final String TAG = "ReCommendPresenter";
    private List<IReCommendViewCallback> callbacks=new ArrayList<>();
    private static RecommendPresenter recommendPresenter=null;

    private List<Album> curList=null;
    private RecommendPresenter(){};

    public static RecommendPresenter newInstance(){
        if(recommendPresenter==null){
            synchronized (RecommendPresenter.class){
                if(recommendPresenter==null){
                    recommendPresenter=new RecommendPresenter();
                }
            }
        }
        return recommendPresenter;
    }
    @Override
    public void getReCommendList() {
        loading();
        XmlyApi xmlyApi=XmlyApi.getXmlyApi();
        xmlyApi.getAlbumList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                List<Album> list=gussLikeAlbumList.getAlbumList();
                if (list != null) {
                    int size=list.size();
                    LogUtil.d(TAG,"albumList size---->"+size);
                    for (Album album : list) {
                        LogUtil.d(TAG,album.toString());
                    }
                    handlerRecommendResult(list);
                }
            }
            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG,"get album error ");
                handlerError();
            }
        });
    }

    private void handlerError() {
        for (IReCommendViewCallback callback : callbacks) {
            callback.onNetworkError();
        }
    }

    private void handlerRecommendResult(List<Album> result) {
        if(result!=null){
            if(result.size()!=0){
                for (IReCommendViewCallback callback : callbacks) {
                    callback.onReCommendListLoad(result);
                }
                curList=result;
            }else{
                for (IReCommendViewCallback callback : callbacks) {
                    callback.onEmpty();
                }
            }
        }

    }

    public List<Album> getCurList(){
        return curList;
    }
    private void loading(){
        for (IReCommendViewCallback callback : callbacks) {
            callback.onLoading();
        }
    }

    @Override
    public void pull2refresh() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewListener(IReCommendViewCallback viewCallback) {
        if(!callbacks.contains(viewCallback)){
            callbacks.add(viewCallback);
        }
    }

    @Override
    public void unRegisterViewListener(IReCommendViewCallback viewCallback) {
        if(callbacks.contains(viewCallback)){
            callbacks.remove(viewCallback);
        }
    }
}
