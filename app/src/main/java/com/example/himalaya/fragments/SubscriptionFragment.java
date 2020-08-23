package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.AlbumDetailActivity;
import com.example.himalaya.R;
import com.example.himalaya.adapters.RecommendListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.ISubscriptViewCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.SubscriptPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.utils.UIUtil;
import com.example.himalaya.views.ConfirmDialog;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.Collections;
import java.util.List;

public class SubscriptionFragment extends BaseFragment implements ISubscriptViewCallback, RecommendListAdapter.OnItemClickListener, ConfirmDialog.OnDialogClickListener {
    private static final String TAG = "SubscriptionFragment";
    private RecyclerView subscript_list;
    private TwinklingRefreshLayout subscript_refresh;
    private SubscriptPresenter subscriptPresenter;
    private RecommendListAdapter adapter;
    private Album curAlbum;
    private UILoader mUILoader;
    @Override
    public View loadView(final LayoutInflater inflater, ViewGroup container) {
        mUILoader=new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return successView(inflater,container);
            }

            @Override
            protected View getEmptyView() {
                return super.getEmptyView();
            }
        };
        if(mUILoader.getParent() instanceof  ViewGroup)
            ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
        return mUILoader;

    }
    private View successView(LayoutInflater inflater,ViewGroup container){
        View view=inflater.inflate(R.layout.fragment_subscription,container,false);
        subscript_list=view.findViewById(R.id.subscription_list);
        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        subscript_list.setLayoutManager(manager);
        adapter=new RecommendListAdapter(getContext());
        subscript_list.setAdapter(adapter);
        subscript_list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(getContext(),5);
                outRect.bottom= UIUtil.dip2px(getContext(),5);
                outRect.left=UIUtil.dip2px(getContext(),5);
                outRect.right=UIUtil.dip2px(getContext(),5);
            }
        });
        subscript_list.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(this);
        subscript_refresh=view.findViewById(R.id.subscription_refresh);
        subscript_refresh.setEnableRefresh(false);
        subscript_refresh.setEnableLoadmore(false);
        subscriptPresenter=SubscriptPresenter.getInstance();
        subscriptPresenter.registerViewListener(this);
        subscriptPresenter.getAlbumList();
        return view;
    }
    @Override
    public void onAddSubscriptFinished(boolean result) {

    }

    @Override
    public void onDelSubscriptFinished(boolean result) {

    }

    @Override
    public void onGetAlbumListFinished(List<Album> list) {
        Collections.reverse(list);
        adapter.setList(list);
        if(list.size()==0)
            mUILoader.updateState(UILoader.UIState.EMPTY);
        else
        mUILoader.updateState(UILoader.UIState.SUCCESS);
    }

    @Override
    public void onItemClick(Album album) {
        AlbumDetailPresenter albumDetailPresenter=AlbumDetailPresenter.getInstance();
        albumDetailPresenter.setTargetAlbum(album);
        LogUtil.d(TAG,"onItemClick:"+album.toString());
        Intent intent=new Intent(getContext(), AlbumDetailActivity.class);
        startActivity(intent);
    }

    private ConfirmDialog dialog;
    @Override
    public void onItemLongClick(Album album) {
        curAlbum=album;
        if(dialog==null)
        dialog=new ConfirmDialog(getActivity());
        dialog.setOnDialogClickListener(this);
        dialog.show();
    }

    //写反了
    @Override
    public void onCancelClick() {
        subscriptPresenter.delSubscript(curAlbum);
        dialog.dismiss();

    }

    @Override
    public void onEnterClick() {
        dialog.dismiss();
    }
}
