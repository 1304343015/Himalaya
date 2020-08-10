package com.example.himalaya.fragments;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.CategoryRecommendAlbumsList;
import com.ximalaya.ting.android.opensdk.model.album.DiscoveryRecommendAlbums;
import com.ximalaya.ting.android.opensdk.model.album.DiscoveryRecommendAlbumsList;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.category.Category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment {
    private static final String TAG = "RecommendFragment";
    @Override
    public View loadView(LayoutInflater inflater, ViewGroup container) {
        View view=inflater.inflate(R.layout.fragment_recomment,container,false);
        getRecommendList();
        return view;
    }

    private void getRecommendList(){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT,Constants.sRECOMMEND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                List<Album> list=gussLikeAlbumList.getAlbumList();
                if (list != null) {
                    int size=list.size();
                    LogUtil.e(TAG,"albumList size---->"+size);
                    for (Album album : list) {
                        LogUtil.e(TAG,album.toString());
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG,"get album error ");
            }
        });
    }
}
