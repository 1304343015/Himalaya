package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.AlbumDetailActivity;
import com.example.himalaya.R;
import com.example.himalaya.adapters.RecommendListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IReCommendViewCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.RecommendPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.utils.UIUtil;
import com.example.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public class RecommendFragment extends BaseFragment implements IReCommendViewCallback, UILoader.onReTryListener, RecommendListAdapter.OnItemClickListener {
    private static final String TAG = "RecommendFragment";
    private RecyclerView recommend_list;
    private UILoader mUILoader;
    private RecommendListAdapter adapter;
    private RecommendPresenter presenter;
    @Override
    public View loadView(final LayoutInflater inflater, ViewGroup container) {
        mUILoader=new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return onSuccessView(inflater,container);
            }
        };
        mUILoader.setOnReTryListener(this);
        presenter=RecommendPresenter.newInstance();
        presenter.registerRecommendViewCallback(this);
        presenter.getReCommendList();

        if(mUILoader.getParent() instanceof  ViewGroup){
            ((ViewGroup)mUILoader.getParent()).removeView(mUILoader);
        }
        return mUILoader;
    }

    private View onSuccessView(LayoutInflater inflater,ViewGroup container) {
        LogUtil.d(TAG,"onSuccessView");
        View view=inflater.inflate(R.layout.fragment_recomment,container,false);
        recommend_list=view.findViewById(R.id.recommend_list);
        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        recommend_list.setLayoutManager(manager);
        recommend_list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(getContext(),5);
                outRect.bottom= UIUtil.dip2px(getContext(),5);
                outRect.left=UIUtil.dip2px(getContext(),5);
                outRect.right=UIUtil.dip2px(getContext(),5);
            }
        });
        manager.setOrientation(RecyclerView.VERTICAL);
        adapter=new RecommendListAdapter(getContext());
        adapter.setOnItemClickListener(this);
        recommend_list.setAdapter(adapter);
        return view;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(presenter!=null)
        presenter.unRegisterRecommendViewCallback(this);
    }

    @Override
    public void onReCommendListLoad(List<Album> result) {
        LogUtil.d(TAG,"onReCommendListLoad");
        adapter.setList(result);
        adapter.notifyDataSetChanged();
        mUILoader.updateState(UILoader.UIState.SUCCESS);
    }

    @Override
    public void onLoading() {
        LogUtil.d(TAG,"onLoading");
        mUILoader.updateState(UILoader.UIState.LOADING);
    }

    @Override
    public void onNetworkError() {
        LogUtil.d(TAG,"onNetworkError");
        mUILoader.updateState(UILoader.UIState.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        LogUtil.d(TAG,"onEmpty");
        mUILoader.updateState(UILoader.UIState.EMPTY);
    }

    @Override
    public void onReTry() {
        presenter.getReCommendList();
    }


    @Override
    public void onItemClick(Album album) {
        AlbumDetailPresenter albumDetailPresenter=AlbumDetailPresenter.getInstance();
        albumDetailPresenter.setTargetAlbum(album);
        LogUtil.d(TAG,"onItemClick:"+album.toString());
        Intent intent=new Intent(getContext(), AlbumDetailActivity.class);
        startActivity(intent);
    }
}
