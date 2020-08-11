package com.example.himalaya.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.himalaya.R;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class RecommendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "RecommendListAdapter";
    private List<Album> list=new ArrayList<>();
    private Context context;
    public RecommendListAdapter(Context context){
        this.context=context;
    }
    public void setList(List<Album> list){
        this.list.clear();
        this.list.addAll(list);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_recommend,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder= (MyHolder) holder;
        myHolder.itemView.setTag(position);
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i= (int) v.getTag();
                LogUtil.d(TAG,"onclick:-->"+i);
                onItemClickListener.onItemClick(list.get(i));
            }
        });
        Album album=list.get(position);
        Glide.with(context).load(album.getCoverUrlSmall()).into(myHolder.album_icon);
        myHolder.album_title.setText(album.getAlbumTitle());
        myHolder.album_content.setText(album.getAlbumIntro());
        myHolder.play_count.setText(String.format("%.1f",album.getPlayCount()*1.0f/100000)+"万");
        myHolder.play_ji.setText(album.getIncludeTrackCount()+"集");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MyHolder extends RecyclerView.ViewHolder{
        ImageView album_icon;
        TextView album_title;
        TextView album_content;
        TextView play_count;
        TextView play_ji;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_icon=itemView.findViewById(R.id.album_icon);
            album_title=itemView.findViewById(R.id.album_title);
            album_content=itemView.findViewById(R.id.album_content);
            play_count=itemView.findViewById(R.id.play_count);
            play_ji=itemView.findViewById(R.id.play_ji);
        }
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }
    public interface  OnItemClickListener{
        void onItemClick(Album album);
    }

}
