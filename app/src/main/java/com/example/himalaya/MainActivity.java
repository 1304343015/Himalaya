package com.example.himalaya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.himalaya.adapters.MainContentAdapter;
import com.example.himalaya.interfaces.IPlayerViewCallback;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.presenters.RecommendPresenter;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.google.android.material.tabs.TabLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, IPlayerViewCallback {
    private static final String TAG = "MainActivity";
    private Toolbar tb_title;
    private TabLayout tl_main;
    private ViewPager vp_main;
    private ImageView main_play_img;
    private ImageView main_play_control;
    private TextView main_play_title;
    private TextView main_play_author;
    private LinearLayout main_bottom;
    private View iv_search;
    private String[] titleArray;
    private PlayerPresenter playerPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        titleArray=getResources().getStringArray(R.array.indicator_title);
        initView();
        initTalLayout();
        initViewPager();
        playerPresenter=PlayerPresenter.getInstance();
        playerPresenter.registerViewListener(this);
    }
    private boolean isLoadFinished=false;
    private void initView(){
        tb_title=findViewById(R.id.tb_title);
        setSupportActionBar(tb_title);
        main_play_img=findViewById(R.id.main_play_img);
        main_play_control=findViewById(R.id.main_control);
        main_play_title=findViewById(R.id.main_play_title);
        main_play_author=findViewById(R.id.main_play_author);
        iv_search=findViewById(R.id.iv_search);
        main_play_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerPresenter.hasAlbumList()){
                    if(playerPresenter.isPlaying()){
                        playerPresenter.pause();
                    }else{
                        playerPresenter.start();
                    }
                }else{
                    playFirstAlbum();
                }

            }
        });

        main_bottom=findViewById(R.id.main_bottom);
        main_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoadFinished){
                    if(!playerPresenter.hasAlbumList()){
                        playFirstAlbum();
                    }
                    Intent intent=new Intent(MainActivity.this,PlayerActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this,"数据未加载完成",Toast.LENGTH_SHORT).show();
                }

            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });
        }

        private void playFirstAlbum(){
            List<Album> albumList= RecommendPresenter.newInstance().getCurList();
            if (albumList != null&&albumList.size()>0) {
                int id= (int) albumList.get(0).getId();
                playerPresenter.playAlbumById(id);
            }
        }

    private void initTalLayout() {
        tl_main=findViewById(R.id.tl_main);
        for (String s : titleArray) {
            tl_main.addTab(tl_main.newTab().setText(s));
        }
        tl_main.addOnTabSelectedListener(this);
    }
    private void initViewPager() {
        vp_main=findViewById(R.id.vp_main);
        MainContentAdapter adapter=new MainContentAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vp_main.setAdapter(adapter);
        vp_main.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                LogUtil.d(TAG,"onPageSelected");
                tl_main.getTabAt(position).select();
            }
        });
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        vp_main.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPlayStart() {
        updatePlayState(true);
    }

    @Override
    public void onPlayStop() {

    }

    private void updatePlayState(boolean isPlaying){
        main_play_control.setImageResource(isPlaying?R.mipmap.stop_normal:R.mipmap.play_normal);
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
    private LoadFinishedReceive receive=null;

    @Override
    protected void onStart() {
        super.onStart();
        receive=new LoadFinishedReceive();
        IntentFilter filter=new IntentFilter(Constants.LoadFINISHED_EVENT);
        registerReceiver(receive,filter);
    }



    @Override
    public void onTrackUpdate(Track track, int index) {
        if(track!=null){
            main_play_title.setText(track.getTrackTitle());
            main_play_author.setText(track.getAnnouncer().getNickname());
            Glide.with(this).load(track.getCoverUrlSmall()).into(main_play_img);
        }

    }

    @Override
    public void onProgressChange(int currPos, int duration) {

    }

    public class LoadFinishedReceive extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int flag=intent.getIntExtra("flag",0);
            if(flag==0)
                isLoadFinished=true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerPresenter.unRegisterViewListener(this);
        unregisterReceiver(receive);
    }
}
