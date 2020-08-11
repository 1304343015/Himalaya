package com.example.himalaya.interfaces;

public interface IAlbumDetailPresenter {

    void pull2refresh();

    void loadMore();

    void loadDetailList(int albumId,int pageNum);

    void loading();

    void networkError();
    void registerOnDetailViewListener(IAlbumDetailViewCallback onDetailViewListener);

    void unRegisterOnDetailViewListener(IAlbumDetailViewCallback onDetailViewListener);

}
