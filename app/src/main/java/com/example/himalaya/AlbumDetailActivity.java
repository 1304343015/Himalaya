package com.example.himalaya;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.himalaya.adapters.AlbumDetailAdapter;
import com.example.himalaya.interfaces.IAlbumDetailPresenter;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.utils.ImageBlur;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.utils.UIUtil;
import com.example.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public class AlbumDetailActivity extends AppCompatActivity implements IAlbumDetailViewCallback,UILoader.onReTryListener, AlbumDetailAdapter.OnDetailItemClickListener {
    private static final String TAG = "AlbumDetailActivity";
    private int currentPage=1;
    private ImageView album_large;
    private ImageView album_small;
    private TextView album_name;
    private TextView album_author;
    private RecyclerView detail_list;
    private FrameLayout content_layout;
    private AlbumDetailAdapter albumDetailAdapter;
    private AlbumDetailPresenter albumDetailPresenter;
    private UILoader mUILoader;
    private int currentAlbumId=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
    }

    private void initView(){
        album_large=findViewById(R.id.album_large);
        album_small=findViewById(R.id.album_small);
        album_name=findViewById(R.id.album_name);
        album_author=findViewById(R.id.album_author);
        content_layout=findViewById(R.id.content_layout);
        mUILoader=new UILoader(this) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return successView();
            }
        };
        mUILoader.setOnReTryListener(this);
        content_layout.addView(mUILoader);

        albumDetailPresenter=AlbumDetailPresenter.getInstance();
        albumDetailPresenter.registerOnDetailViewListener(this);

    }

    private View successView() {
        View view= LayoutInflater.from(this).inflate(R.layout.detail_view,content_layout,false);
        detail_list=view.findViewById(R.id.detail_list);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        detail_list.setLayoutManager(manager);
        detail_list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(getApplicationContext(),2);
                outRect.bottom= UIUtil.dip2px(getApplicationContext(),2);
                outRect.left=UIUtil.dip2px(getApplicationContext(),2);
                outRect.right=UIUtil.dip2px(getApplicationContext(),2);
            }
        });
        albumDetailAdapter=new AlbumDetailAdapter(this);
        detail_list.setAdapter(albumDetailAdapter);
        return view;
    }

    @Override
    public void onAlbumInfoLoad(final Album album) {
        currentAlbumId= (int) album.getId();
        LogUtil.d(TAG,album.toString());
        albumDetailPresenter.loadDetailList((int)album.getId(),1);

        Glide.with(this).load(album.getCoverUrlLarge()).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if(resource!=null){
                    album_large.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    album_large.setImageDrawable(resource);
                    ImageBlur.makeBlur(album_large,getApplicationContext());
                }
            }
        });
        Glide.with(this).load(album.getCoverUrlSmall()).into(album_small);
        LogUtil.d(TAG,album.getAlbumTitle());
        album_name.setText(album.getAlbumTitle());
        album_author.setText(album.getAnnouncer().getNickname());
    }

    @Override
    public void onDetailListLoad(List<Track> list) {
        albumDetailAdapter.setList(list);
        mUILoader.updateState(UILoader.UIState.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        mUILoader.updateState(UILoader.UIState.NETWORK_ERROR);
    }

    @Override
    public void onLoading() {
        mUILoader.updateState(UILoader.UIState.LOADING);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        albumDetailPresenter.unRegisterOnDetailViewListener(this);
    }

    @Override
    public void onReTry() {
        albumDetailPresenter.loadDetailList(currentAlbumId,currentPage);
    }

    @Override
    public void onDetailItemClick(Track track) {
        Intent intent=new Intent(AlbumDetailActivity.this,PlayerActivity.class);
        startActivity(intent);
    }
}