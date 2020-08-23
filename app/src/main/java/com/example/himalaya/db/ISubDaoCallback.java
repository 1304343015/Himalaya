package com.example.himalaya.db;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubDaoCallback {
    void onAddResult(boolean isSuccess);

    void onDelResult(boolean isSuccess);

    void onGetSubList(List<Album> list);
}
