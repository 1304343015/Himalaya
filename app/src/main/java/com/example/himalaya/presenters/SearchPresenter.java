package com.example.himalaya.presenters;

import android.util.Log;

import com.example.himalaya.api.XmlyApi;
import com.example.himalaya.interfaces.ISearchPresenter;
import com.example.himalaya.interfaces.ISearchViewCallback;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {
    private static final String TAG = "SearchPresenter";
    private List<ISearchViewCallback> callbacks=new ArrayList<>();
    private XmlyApi xmlyApi;
    private int curPage= Constants.DEFAULT_PAGE;
    private List<Album> curAlbumList=new ArrayList<>();
    private String curKeyWord=null;
    private boolean isLoadMore=false;

    private static SearchPresenter searchPresenter=null;
    private SearchPresenter(){
        xmlyApi=XmlyApi.getXmlyApi();
    }
    public static SearchPresenter getInstance(){
        if(searchPresenter==null){
            synchronized (SearchPresenter.class){
                if(searchPresenter==null){
                    searchPresenter=new SearchPresenter();
                }
            }
        }
        return searchPresenter;
    }
    @Override
    public void doSearch(String keyword) {
        isLoadMore=false;
        curPage=Constants.DEFAULT_PAGE;
        curAlbumList.clear();
        loading();
        curKeyWord=keyword;
        search();

    }
    private void search(){
        xmlyApi.getSearchAlbumList(curKeyWord, curPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {

                List<Album> albums = searchAlbumList.getAlbums();
                if(albums!=null){
                    if ((albums.size()==0)){
                        if(isLoadMore){
                            for (ISearchViewCallback callback : callbacks) {
                                callback.onLoadMoreResult(curAlbumList,false);
                            }
                        }else{
                            for (ISearchViewCallback callback : callbacks) {
                                callback.onDataEmpty();
                            }
                        }

                    }else{
                        for (Album album : albums) {
                            LogUtil.d(TAG,album.toString());
                        }
                        curAlbumList.addAll(albums);
                        if(isLoadMore){
                            for (ISearchViewCallback callback : callbacks) {
                                callback.onLoadMoreResult(curAlbumList,true);
                            }
                        }else{
                            for (ISearchViewCallback callback : callbacks) {
                                callback.onSearchResultLoad(curAlbumList);
                            }
                        }

                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG,"doSearch,code:"+i+",msg:"+s);
                if(isLoadMore){
                    curPage--;
                }
                for (ISearchViewCallback callback : callbacks) {
                    callback.onNetworkError();
                }
            }
        });
    }

    @Override
    public void loading() {
        for (ISearchViewCallback callback : callbacks) {
            callback.onLoading();
        }
    }

    @Override
    public void getHotWord() {
        xmlyApi.getHotWord(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                List<HotWord> list = hotWordList.getHotWordList();
                if(list!=null){
                    for (HotWord hotWord : list) {
                        LogUtil.d(TAG,hotWord.getSearchword());
                    }
                    if(list.size()==0){
                        for (ISearchViewCallback callback : callbacks) {
                            //callback.onDataEmpty();
                        }
                    }else{
                        for (ISearchViewCallback callback : callbacks) {
                            callback.onHotWordLoad(list);
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                for (ISearchViewCallback callback : callbacks) {
                    callback.onNetworkError();
                }
            }
        });
    }

    @Override
    public void research() {
        search();
    }

    @Override
    public void loadMore() {
        isLoadMore=true;
        curPage++;
        search();
    }

    @Override
    public void getRecommendByWord( String keyword) {
        xmlyApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                LogUtil.d(TAG,"suggestWords:"+suggestWords);
                List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                if(keyWordList!=null){
                    for (ISearchViewCallback callback : callbacks) {
                        callback.onRecommendResultLoad(keyWordList);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    @Override
    public void registerViewListener(ISearchViewCallback iSearchViewCallback) {
        if(!callbacks.contains(iSearchViewCallback)){
            callbacks.add(iSearchViewCallback);
            getHotWord();
        }

    }

    @Override
    public void unRegisterViewListener(ISearchViewCallback iSearchViewCallback) {
        callbacks.remove(iSearchViewCallback);
    }
}
