package com.example.himalaya.interfaces;

import com.example.himalaya.base.BasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface ISubscriptPresenter extends BasePresenter<ISubscriptViewCallback> {

    void addSubscript(Album album);

    void delSubscript(Album album);

    void getAlbumList();
}
