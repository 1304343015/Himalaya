package com.example.himalaya.interfaces;

import com.example.himalaya.base.BasePresenter;

public interface ISearchPresenter extends BasePresenter<ISearchViewCallback> {

    void doSearch(String keyword);

    void getHotWord();

    void loading();

    void research();

    void loadMore();

    void getRecommendByWord(String keyword);

}
