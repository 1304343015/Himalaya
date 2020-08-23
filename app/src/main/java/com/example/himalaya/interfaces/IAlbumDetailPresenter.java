package com.example.himalaya.interfaces;

import com.example.himalaya.base.BasePresenter;

public interface IAlbumDetailPresenter extends BasePresenter<IAlbumDetailViewCallback> {

    void pull2refresh();

    void loadMore();

    void loadDetailList(int albumId,int pageNum);

    void loading();

    void networkError();



}
