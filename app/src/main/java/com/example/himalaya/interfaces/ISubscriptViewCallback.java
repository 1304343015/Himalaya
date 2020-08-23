package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubscriptViewCallback {

    void onAddSubscriptFinished(boolean result);

    void onDelSubscriptFinished(boolean result);

    void onGetAlbumListFinished(List<Album> list);
}
