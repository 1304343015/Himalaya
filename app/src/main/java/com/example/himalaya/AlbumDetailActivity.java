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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.himalaya.adapters.AlbumDetailAdapter;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IAlbumDetailPresenter;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.example.himalaya.interfaces.IPlayerViewCallback;
import com.example.himalaya.interfaces.ISubscriptViewCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.presenters.SubscriptPresenter;
import com.example.himalaya.utils.ImageBlur;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.utils.UIUtil;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public class AlbumDetailActivity extends AppCompatActivity implements IAlbumDetailViewCallback, UILoader.onReTryListener, AlbumDetailAdapter.OnDetailItemClickListener, IPlayerViewCallback, ISubscriptViewCallback {

    private static final String TAG = "AlbumDetailActivity";
    private int currentPage=1;
    private ImageView album_large;
    private ImageView album_small;
    private TextView album_name;
    private TextView album_author;
    private ImageView play_btn;
    private TextView play_tv;
    private RecyclerView detail_list;
    private TextView subscript_button;
    private FrameLayout content_layout;
    private TwinklingRefreshLayout refreshLayout;
    private AlbumDetailAdapter albumDetailAdapter;
    private AlbumDetailPresenter albumDetailPresenter;
    private UILoader mUILoader;
    private int currentAlbumId=-1;
    private List<Track> curTrackList=null;
    private PlayerPresenter playerPresenter;
    private SubscriptPresenter subscriptPresenter;
    private Track curTrack;
    private Album curAlbum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
        initPresenter();
        updateSubscriptState();
        updatePlayState(playerPresenter.isPlaying());
    }

    private void updateSubscriptState() {
        boolean isSub=subscriptPresenter.isSub(curAlbum);
        LogUtil.d(TAG,"isSub:"+isSub);
        subscript_button.setText(isSub?R.string.subscript_text:R.string.unSubscript_text);
    }

    private void initPresenter(){
        albumDetailPresenter=AlbumDetailPresenter.getInstance();
        albumDetailPresenter.registerViewListener(this);
        playerPresenter=PlayerPresenter.getInstance();
        playerPresenter.registerViewListener(this);
        subscriptPresenter=SubscriptPresenter.getInstance();
        subscriptPresenter.getAlbumList();
        subscriptPresenter.registerViewListener(this);
    }
    private void initView(){
        album_large=findViewById(R.id.album_large);
        album_small=findViewById(R.id.album_small);
        album_name=findViewById(R.id.album_name);
        album_author=findViewById(R.id.album_author);
        content_layout=findViewById(R.id.content_layout);
        play_btn=findViewById(R.id.play_btn);
        play_tv=findViewById(R.id.play_tv);
        subscript_button=findViewById(R.id.subscript_button);
        mUILoader=new UILoader(this) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return successView();
            }
        };
        mUILoader.setOnReTryListener(this);
        content_layout.addView(mUILoader);



        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerPresenter.hasAlbumList()){
                    if(playerPresenter.isPlaying()){
                        playerPresenter.pause();
                    }else{
                        playerPresenter.start();
                    }
                }else{
                    playerPresenter.setTrackListAndIndex(curTrackList,0);
                }

            }
        });

        subscript_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSub=subscriptPresenter.isSub(curAlbum);
                if(isSub){
                    subscriptPresenter.delSubscript(curAlbum);
                }else{
                    subscriptPresenter.addSubscript(curAlbum);
                }
            }
        });
    }
    private boolean isRefresh=false;
    private View successView() {
        View view= LayoutInflater.from(this).inflate(R.layout.detail_view,content_layout,false);
        refreshLayout=view.findViewById(R.id.refresh);
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
        albumDetailAdapter.setOnDetailItemClickListener(this);
        detail_list.setAdapter(albumDetailAdapter);

        //refreshLayout.setHeaderHeight(140);
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AlbumDetailActivity.this,"刷新完成",Toast.LENGTH_SHORT).show();
                        refreshLayout.finishRefreshing();
                    }
                },2000);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                isRefresh=true;
               albumDetailPresenter.loadMore();

            }
        });
        return view;
    }

    private void updatePlayState(boolean isPlay){
        play_btn.setImageResource(isPlay?R.mipmap.pause_black_normal:R.mipmap.play_black_normal);
        if(!isPlay){
            play_tv.setText("点击播放");
        }else{
            play_tv.setText(curTrack.getTrackTitle());
        }
    }
    @Override
    public void onAlbumInfoLoad(final Album album) {
        curAlbum=album;
        currentAlbumId= (int) album.getId();
        LogUtil.d(TAG,album.toString());
        albumDetailPresenter.loading();
        albumDetailPresenter.loadDetailList(currentAlbumId,1);

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
        if(isRefresh){
            isRefresh=false;
            refreshLayout.finishLoadmore();
        }
        curTrackList=list;
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
    public void onRefreshFinished(int size) {

    }

    @Override
    public void onLoadMoreFinished(int size) {
        if(size>0){
            Toast.makeText(this,"成功加载"+size+"条数据",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"无更多数据",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        albumDetailPresenter.unRegisterViewListener(this);
    }

    @Override
    public void onReTry() {
        albumDetailPresenter.loadDetailList(currentAlbumId,currentPage);
    }

    @Override
    public void onDetailItemClick(List<Track> trackList,int index) {
        PlayerPresenter playerPresenter=PlayerPresenter.getInstance();
        playerPresenter.setTrackListAndIndex(trackList,index);
        Intent intent=new Intent(AlbumDetailActivity.this,PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPlayStart() {
        updatePlayState(true);
    }

    @Override
    public void onPlayStop() {

    }

    @Override
    public void onPlayPause() {
        updatePlayState(false);
    }

    @Override
    public void onChangePre() {

    }

    @Override
    public void onChangeNext() {

    }

    @Override
    public void onSwitchPlayMode(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void onListLoaded(List<Track> trackList) {

    }

    @Override
    public void onTrackUpdate(Track track, int index) {
        if(track!=null){
            this.curTrack=track;
            play_tv.setText(track.getTrackTitle());
        }
    }

    @Override
    public void onProgressChange(int currPos, int duration) {

    }

    @Override
    public void onAddSubscriptFinished(boolean result) {
        if(result){
            subscript_button.setText(R.string.subscript_text);
            Toast.makeText(this,"订阅成功",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"订阅失败",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDelSubscriptFinished(boolean result) {
        if(result){
            subscript_button.setText(R.string.unSubscript_text);
            Toast.makeText(this,"取消订阅成功",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"取消订阅失败",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetAlbumListFinished(List<Album> list) {
        for (Album album : list) {
            LogUtil.d(TAG,"subscription:"+album.toString());
        }
        updateSubscriptState();
    }
}