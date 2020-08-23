package com.example.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.adapters.PopListAdapter;
import com.example.himalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public class ListPopWindow extends PopupWindow {
    private View view;
    private RecyclerView pop_list;
    private TextView pop_close;
    private LinearLayout pop_mode_content;
    private ImageView pop_mode_iv;
    private TextView pop_mode_tv;
    private LinearLayout pop_order_content;
    private ImageView pop_order_iv;
    private TextView pop_order_tv;
    public ListPopWindow(){
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view= LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.popup_view,null);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        setAnimationStyle(R.style.PopStyle);
        setContentView(view);
        initView();
        initEvent();
    }

    private PopListAdapter adapter;
    private void initView(){
        pop_close=view.findViewById(R.id.pop_close);
        pop_list=view.findViewById(R.id.pop_list);
        LinearLayoutManager manager=new LinearLayoutManager(BaseApplication.getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        pop_list.setLayoutManager(manager);
        adapter=new PopListAdapter(BaseApplication.getContext());
        pop_list.setAdapter(adapter);

        pop_mode_content=view.findViewById(R.id.pop_mode_content);
        pop_mode_iv=view.findViewById(R.id.pop_play_mode_iv);
        pop_mode_tv=view.findViewById(R.id.pop_play_mode_tv);

        pop_order_content=view.findViewById(R.id.pop_order_content);
        pop_order_tv=view.findViewById(R.id.pop_order_tv);
        pop_order_iv=view.findViewById(R.id.pop_order_iv);
    }

    private void initEvent() {
        pop_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        pop_mode_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPopListActionListener.onPopModeChange();
            }
        });

        pop_order_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void onSwitchPlayMode(XmPlayListControl.PlayMode mode) {
        int imgId=R.mipmap.player_icon_list_normal;
        int textId=R.string.play_mode_order_text;
        switch (mode){
            case PLAY_MODEL_LIST:
                imgId=R.mipmap.player_icon_list_normal;
                textId=R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_LIST_LOOP:
                imgId=R.mipmap.play_mode_loop_normal;
                textId=R.string.play_mode_list_play_text;
                break;
            case PLAY_MODEL_RANDOM:
                imgId=R.mipmap.play_mode_random_normal;
                textId=R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                imgId=R.mipmap.play_mode_loop_one_normal;
                textId=R.string.play_mode_single_play_text;
                break;
        }
        pop_mode_iv.setImageResource(imgId);
        pop_mode_tv.setText(textId);
    }

    public void setData(List<Track> list){
        adapter.setData(list);
    }

    public void setCurrentPosition(int position){
        adapter.setCurrentPosition(position);
        pop_list.scrollToPosition(position);
    }

    public void setOnPopListItemClickListener(OnPopListItemClickListener onPopListItemClickListener){
        adapter.setOnPopListItemClickListener(onPopListItemClickListener);
    }
    public interface OnPopListItemClickListener{
        void onItemClick(int position);
    }

    private OnPopListActionListener onPopListActionListener;

    public void setOnPopListActionListener(OnPopListActionListener onPopListActionListener){
        this.onPopListActionListener=onPopListActionListener;
    }
    public interface OnPopListActionListener{
        void onPopModeChange();
    }
}
