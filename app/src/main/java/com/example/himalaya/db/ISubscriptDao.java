package com.example.himalaya.db;

import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface ISubscriptDao {
    void addSubscript(Album album);

    void delSubscript(Album album);

    void getAllSubscript();
}
