package com.example.himalaya.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlbumDetailAdapter extends RecyclerView.Adapter {
    private List<Track> list=new ArrayList<>();
    private Context context;
    private SimpleDateFormat durationFormat=new SimpleDateFormat("mm:ss");
    private SimpleDateFormat dateFormat=new SimpleDateFormat("yy-MM-dd");
    public AlbumDetailAdapter(Context context){
        this.context=context;
    }

    public void setList(List<Track> list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_album_detail,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder= (MyHolder) holder;
        Track track=list.get(position);
        myHolder.detail_num.setText((position+1)+"");
        myHolder.detail_name.setText(track.getTrackTitle());
        myHolder.detail_count.setText(String.format("%.1f万",track.getPlayCount()*1.0f/10000));
        String duration=durationFormat.format(new Date(track.getDuration()*1000));
        myHolder.detail_time.setText(duration);
        String date=dateFormat.format(new Date(track.getUpdatedAt()));
        myHolder.detail_date.setText(date);
        myHolder.itemView.setTag(position);
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i= (int) v.getTag();
                onDetailItemClickListener.onDetailItemClick(list.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MyHolder extends RecyclerView.ViewHolder {
        TextView detail_num;
        TextView detail_name;
        TextView detail_count;
        TextView detail_time;
        TextView detail_date;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            detail_num=itemView.findViewById(R.id.detail_num);
            detail_name=itemView.findViewById(R.id.detail_name);
            detail_count=itemView.findViewById(R.id.detail_count);
            detail_time=itemView.findViewById(R.id.detail_time);
            detail_date=itemView.findViewById(R.id.detail_date);
        }
    }
    private OnDetailItemClickListener onDetailItemClickListener;
    public  void setOnDetailItemClickListener(OnDetailItemClickListener onDetailItemClickListener){
        this.onDetailItemClickListener=onDetailItemClickListener;
    }
    public interface OnDetailItemClickListener{
        void onDetailItemClick(Track track);
    }
}
