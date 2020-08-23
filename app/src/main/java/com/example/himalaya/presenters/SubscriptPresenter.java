package com.example.himalaya.presenters;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.db.ISubDaoCallback;
import com.example.himalaya.db.SubscriptDao;
import com.example.himalaya.interfaces.ISubscriptPresenter;
import com.example.himalaya.interfaces.ISubscriptViewCallback;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;


public class SubscriptPresenter implements ISubscriptPresenter, ISubDaoCallback {
    private static SubscriptPresenter subscriptPresenter;
    private SubscriptDao subscriptDao;

    private List<ISubscriptViewCallback> callbacks=new ArrayList<>();
    private Map<Long,Album> dataMap=new HashMap<>();
    private SubscriptPresenter(){
        subscriptDao=SubscriptDao.getInstance();
        subscriptDao.setCallback(this);
    }

    public static SubscriptPresenter getInstance(){
        if(subscriptPresenter==null){
            synchronized (SubscriptPresenter.class){
                if (subscriptPresenter == null) {
                    subscriptPresenter=new SubscriptPresenter();
                }
            }
        }
        return subscriptPresenter;
    }
    private void listSubscriptions() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe( ObservableEmitter<Object> emitter)  {
                subscriptDao.getAllSubscript();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }
    @Override
    public void addSubscript(final Album album) {

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe( ObservableEmitter<Object> emitter) {
                subscriptDao.addSubscript(album);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delSubscript(final Album album) {

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe( ObservableEmitter<Object> emitter) {
                subscriptDao.delSubscript(album);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getAlbumList() {
        listSubscriptions();
    }

    @Override
    public void registerViewListener(ISubscriptViewCallback iSubscriptViewCallback) {
        if(!callbacks.contains(iSubscriptViewCallback)){
            callbacks.add(iSubscriptViewCallback);
        }
    }

    @Override
    public void unRegisterViewListener(ISubscriptViewCallback iSubscriptViewCallback) {
        callbacks.remove(iSubscriptViewCallback);
    }

    @Override
    public void onAddResult(final boolean isSuccess) {
        listSubscriptions();
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptViewCallback callback : callbacks) {
                    callback.onAddSubscriptFinished(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDelResult(final boolean isSuccess) {
        listSubscriptions();
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptViewCallback callback : callbacks) {
                    callback.onDelSubscriptFinished(isSuccess);
                }
            }
        });
    }

    @Override
    public void onGetSubList(final List<Album> list) {
        dataMap.clear();
        for (Album album : list) {
            dataMap.put(album.getId(),album);
        }
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptViewCallback callback : callbacks) {
                    callback.onGetAlbumListFinished(list);
                }
            }
        });
    }

    public boolean isSub(Album album){
        Album result=dataMap.get(album.getId());
        return result!=null;
    }
}
