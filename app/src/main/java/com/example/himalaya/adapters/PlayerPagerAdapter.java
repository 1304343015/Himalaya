package com.example.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.himalaya.R;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayerPagerAdapter extends PagerAdapter {
    private static final String TAG = "PlayerPagerAdapter";
    private List<Track> trackList=new ArrayList<>();
    @Override
    public int getCount() {
        return trackList.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view= LayoutInflater.from(container.getContext()).inflate(R.layout.item_player_page,container,false);
        container.addView(view);
        ImageView play_image=view.findViewById(R.id.play_image);
        Glide.with(container.getContext()).load(trackList.get(position).getCoverUrlLarge()).into(play_image);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public void setList(List<Track> list){
        LogUtil.d(TAG,"setList");
        trackList.clear();
        trackList.addAll(list);
        notifyDataSetChanged();
    }
}
