package com.example.himalaya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.himalaya.adapters.PlayerPagerAdapter;
import com.example.himalaya.adapters.PopListAdapter;
import com.example.himalaya.interfaces.IPlayerViewCallback;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.ListPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, IPlayerViewCallback {
    private ImageView play_control;
    private TextView current_time;
    private TextView count_time;
    private SeekBar seek_control;
    private ImageView play_previous;
    private ImageView play_next;
    private TextView player_title;
    private ImageView play_mode;
    private PlayerPresenter playerPresenter;
    private ViewPager vp_play;
    private ImageView play_open_list;
    private ListPopWindow popWindow;
    private static final String TAG = "PlayerActivity";
    private PlayerPagerAdapter adapter;
    private Map<XmPlayListControl.PlayMode,XmPlayListControl.PlayMode> modeMap=new HashMap<>();

    private PlayMode currentMode=PlayMode.PLAY_MODEL_LIST;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initMap();
        initView();
        playerPresenter=PlayerPresenter.getInstance();
        playerPresenter.registerViewListener(this);
        initEvent();
        initBgAnimation();
    }
    private void initMap(){
        modeMap.put(PlayMode.PLAY_MODEL_LIST,PlayMode.PLAY_MODEL_LIST_LOOP);
        modeMap.put(PlayMode.PLAY_MODEL_LIST_LOOP,PlayMode.PLAY_MODEL_RANDOM);
        modeMap.put(PlayMode.PLAY_MODEL_RANDOM,PlayMode.PLAY_MODEL_SINGLE_LOOP);
        modeMap.put(PlayMode.PLAY_MODEL_SINGLE_LOOP,PlayMode.PLAY_MODEL_LIST);
    }
    private void initView(){
        play_control=findViewById(R.id.play_control);
        current_time=findViewById(R.id.current_time);
        count_time=findViewById(R.id.count_time);
        seek_control=findViewById(R.id.seek_control);
        play_previous=findViewById(R.id.play_previous);
        play_next=findViewById(R.id.play_next);
        player_title=findViewById(R.id.player_title);

        vp_play=findViewById(R.id.vp_play);
        adapter=new PlayerPagerAdapter();
        vp_play.setAdapter(adapter);

        play_mode=findViewById(R.id.play_mode);
        play_open_list=findViewById(R.id.play_open_list);
        popWindow=new ListPopWindow();
    }
    private int touchPosition=0;
    private boolean isTouching=false;
    private void initEvent(){
        play_control.setOnClickListener(this);
        play_previous.setOnClickListener(this);
        play_next.setOnClickListener(this);
        play_mode.setOnClickListener(this);
        play_open_list.setOnClickListener(this);
        seek_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    touchPosition=progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouching=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playerPresenter.seekTo(touchPosition);
                isTouching=false;
            }
        });
        vp_play.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                playerPresenter.playByIndex(position);

            }
        });

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popOutAnimator.start();
            }
        });

        popWindow.setOnPopListItemClickListener(new ListPopWindow.OnPopListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                playerPresenter.playByIndex(position);
            }
        });

        popWindow.setOnPopListActionListener(new ListPopWindow.OnPopListActionListener() {
            @Override
            public void onPopModeChange() {
                playerPresenter.switchPlayMode(modeMap.get(currentMode));
            }
        });


    }
    private ValueAnimator popInAnimator;
    private ValueAnimator popOutAnimator;
    private void initBgAnimation(){
        popInAnimator=ValueAnimator.ofFloat(1.0f,0.7f);
        popInAnimator.setDuration(300);
        popInAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha= (float) animation.getAnimatedValue();
                updateBgAlpha(alpha);
            }
        });

        popOutAnimator=ValueAnimator.ofFloat(0.7f,1.0f);
        popOutAnimator.setDuration(300);
        popOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha= (float) animation.getAnimatedValue();
                updateBgAlpha(alpha);
            }
        });


    }


    private void updateBgAlpha(float alpha){
        Window window=getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha=alpha;
        window.setAttributes(attributes);

    }


    /**
     * 点击事件监听
     * @param v view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_control:
                playControl();
                break;
            case R.id.play_previous:
                playerPresenter.playPre();
                break;
            case R.id.play_next:
                playerPresenter.playNext();
                break;
            case R.id.play_mode:
                playerPresenter.switchPlayMode(modeMap.get(currentMode));
                break;
            case R.id.play_open_list:
                LogUtil.d(TAG,"open list");
                popWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                popInAnimator.start();
                break;

        }
    }
    private void playControl(){
        if(playerPresenter!=null){

                if(playerPresenter.isPlaying()){
                    playerPresenter.pause();
                }else {
                    playerPresenter.start();
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerPresenter.unRegisterViewListener(this);
    }

    //视图层
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG,"onPlayStart");
        play_control.setImageDrawable(getResources().getDrawable(R.mipmap.stop_normal));
    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG,"onPlayStop");
        play_control.setImageDrawable(getResources().getDrawable(R.mipmap.play_normal));
    }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG,"onPlayPause");
        play_control.setImageDrawable(getResources().getDrawable(R.mipmap.play_normal));
    }

    @Override
    public void onChangePre() {

    }

    @Override
    public void onChangeNext() {

    }

    @Override
    public void onSwitchPlayMode(XmPlayListControl.PlayMode mode) {
        currentMode=mode;
        switch (mode){
            case PLAY_MODEL_LIST:
                play_mode.setImageResource(R.mipmap.player_icon_list_normal);
                break;
            case PLAY_MODEL_LIST_LOOP:
                play_mode.setImageResource(R.mipmap.play_mode_loop_normal);
                break;
            case PLAY_MODEL_RANDOM:
                play_mode.setImageResource(R.mipmap.play_mode_random_normal);
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                play_mode.setImageResource(R.mipmap.play_mode_loop_one_normal);
                break;

        }
        popWindow.onSwitchPlayMode(mode);
    }

    @Override
    public void onListLoaded(List<Track> trackList) {
        LogUtil.d(TAG,"onListLoaded: list size:"+trackList.size()+"    "+trackList.toString());
        adapter.setList(trackList);
        popWindow.setData(trackList);
    }


    @Override
    public void onTrackUpdate(Track track, int index) {
        if(track==null)
            return ;
        LogUtil.d(TAG,"index:"+index);
        player_title.setText(track.getTrackTitle());
        vp_play.setCurrentItem(index);
        popWindow.setCurrentPosition(index);
    }

    private SimpleDateFormat hFormat=new SimpleDateFormat("hh:mm:ss");
    private SimpleDateFormat mFormat=new SimpleDateFormat("mm:ss");
    @Override
    public void onProgressChange(int currPos, int duration) {
        seek_control.setMax(duration);
        if(duration>=1000*60*60){
            current_time.setText(hFormat.format(currPos));
            count_time.setText(hFormat.format(duration));
        }else{
            current_time.setText(mFormat.format(currPos));
            count_time.setText(mFormat.format(duration));
        }
        if(!isTouching){
            seek_control.setProgress(currPos);
        }

    }

    @Override
    public void onBackPressed() {
        if(popWindow.isShowing()){
            popWindow.dismiss();
        }else{
            finish();
        }
    }
}