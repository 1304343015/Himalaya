package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchViewCallback {

    void onSearchResultLoad(List<Album> result);

    void onHotWordLoad(List<HotWord> result);

    void onLoading();

    void onLoadMoreResult(List<Album> result,boolean isOkay);

    void onRecommendResultLoad(List<QueryResult> result);

    void onDataEmpty();

    void onNetworkError();
}
