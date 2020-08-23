package com.example.himalaya.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.ListPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PopListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "PopListAdapter";
    private List<Track> list=new ArrayList<>();
    private Context context;
    private int currentPosition=0;
    public PopListAdapter(Context context){
        this.context=context;
    }

    public void setData(List<Track> list){
        LogUtil.d(TAG,"notifyDataSetChanged");
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();

    }

    public void setCurrentPosition(int position){
        currentPosition=position;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.item_pop_list,parent,false);
        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder= (MyHolder) holder;
        myHolder.pop_list_title.setText(list.get(position).getTrackTitle());
        myHolder.pop_list_image.setVisibility(position==currentPosition?View.VISIBLE:View.GONE);
        myHolder.pop_list_title.setTextColor(context.getResources().getColor(position==currentPosition?R.color.subscript_bg:R.color.pop_text));
        myHolder.itemView.setTag(position);
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i= (int) v.getTag();
                onPopListItemClickListener.onItemClick(i);
            }
        });
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    private class MyHolder extends RecyclerView.ViewHolder{
        ImageView pop_list_image;
        TextView pop_list_title;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            pop_list_image=itemView.findViewById(R.id.pop_list_image);
            pop_list_title=itemView.findViewById(R.id.pop_list_title);
        }
    }
    private ListPopWindow.OnPopListItemClickListener onPopListItemClickListener;

    public void setOnPopListItemClickListener(ListPopWindow.OnPopListItemClickListener onPopListItemClickListener){
        this.onPopListItemClickListener=onPopListItemClickListener;
    }

}
