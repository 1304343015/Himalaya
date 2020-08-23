package com.example.himalaya.interfaces;

import com.example.himalaya.base.BasePresenter;

public interface IRecommendPresenter extends BasePresenter<IReCommendViewCallback> {
    void getReCommendList();

    void pull2refresh();

    void loadMore();
}
