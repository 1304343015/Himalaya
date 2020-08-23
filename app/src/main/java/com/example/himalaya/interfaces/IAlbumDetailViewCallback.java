package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailViewCallback {

    void onAlbumInfoLoad(Album album);

    void onDetailListLoad(List<Track> list);

    void onNetworkError();

    void onLoading();

    void onRefreshFinished(int size);

    void onLoadMoreFinished(int size);
}
