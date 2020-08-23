package com.example.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SuggestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<QueryResult> list=new ArrayList<>();

    public void setData(List<QueryResult> list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.item_suggest,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        MyHolder myHolder= (MyHolder) holder;
        myHolder.suggest_tv.setText(list.get(position).getKeyword());
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onSuggestItemClickListener.onItemClick(list.get(position).getKeyword());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MyHolder extends RecyclerView.ViewHolder{
        TextView suggest_tv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            suggest_tv=itemView.findViewById(R.id.suggest_tv);
        }
    }
    private OnSuggestItemClickListener onSuggestItemClickListener;

    public void setOnSuggestItemClickListener(OnSuggestItemClickListener onSuggestItemClickListener){
        this.onSuggestItemClickListener=onSuggestItemClickListener;
    }
    public interface  OnSuggestItemClickListener{
        void onItemClick(String suggest);
    }
}
