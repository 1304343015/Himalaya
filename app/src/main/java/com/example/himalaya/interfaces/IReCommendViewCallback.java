package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface IReCommendViewCallback {
    void onReCommendListLoad(List<Album> result);

    void onLoading();

    void onNetworkError();

    void onEmpty();


}
