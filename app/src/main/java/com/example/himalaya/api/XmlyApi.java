package com.example.himalaya.api;

import android.util.Xml;

import com.example.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

public class XmlyApi {
    private static XmlyApi xmlyApi;

    public static XmlyApi getXmlyApi(){
        if(xmlyApi==null){
            synchronized (XmlyApi.class){
                if(xmlyApi==null){
                    xmlyApi=new XmlyApi();
                }
            }
        }
        return xmlyApi;
    }

    public void getAlbumList(IDataCallBack<GussLikeAlbumList> callBack){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.sRECOMMEND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map,callBack);
    }


    public void getTracks(IDataCallBack<TrackList> callBack,int albumId,int page){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page+"");
        CommonRequest.getTracks(map,callBack);
    }


    public void getSearchAlbumList(String keyword, int page, IDataCallBack<SearchAlbumList> callBack){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY,keyword);
        map.put(DTransferConstants.PAGE, page+"");
        map.put(DTransferConstants.PAGE_SIZE,20+"");
        CommonRequest.getSearchedAlbums(map,callBack);
    }


    public void getHotWord( IDataCallBack<HotWordList> callBack){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP,Constants.HOT_WORD_NUM+"");
        CommonRequest.getHotWords(map,callBack);
    }

    public void getSuggestWord(String keyword, IDataCallBack<SuggestWords> callBack){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY,keyword);
        CommonRequest.getSuggestWord(map, callBack);
    }
}
