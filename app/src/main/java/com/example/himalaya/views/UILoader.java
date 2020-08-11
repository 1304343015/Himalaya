package com.example.himalaya.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.himalaya.R;

public abstract class UILoader extends FrameLayout {
    private View emptyView;
    private View networkErrorView;
    private View loadingView;
    private View successView;

    public enum UIState{
        EMPTY,NETWORK_ERROR,LOADING,SUCCESS,NONE
    }
    private UIState currentState=UIState.NONE;
    public UILoader(@NonNull Context context) {
        this(context,null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void updateState(UIState state){
        currentState=state;
        switchViewByCurrentState();
    }
    private void init(){
        switchViewByCurrentState();
    }
    private  void switchViewByCurrentState(){
        if(successView==null){
            successView=getSuccessView(this);
            addView(successView);
        }
        successView.setVisibility(currentState==UIState.SUCCESS?View.VISIBLE:View.GONE);

        if(emptyView==null){
            emptyView=getEmptyView();
            addView(emptyView);
        }
        emptyView.setVisibility(currentState==UIState.EMPTY?View.VISIBLE:View.GONE);


        if(networkErrorView ==null){
            networkErrorView = getNetworkErrorView();
            addView(networkErrorView);
        }
        networkErrorView.setVisibility(currentState==UIState.NETWORK_ERROR?View.VISIBLE:View.GONE);

        if(loadingView==null){
            loadingView= getLoadingView();
            addView(loadingView);
        }
        loadingView.setVisibility(currentState==UIState.LOADING?View.VISIBLE:View.GONE);
    }

    protected abstract View getSuccessView(ViewGroup container);

    private View getLoadingView() {
        View view= LayoutInflater.from(getContext()).inflate(R.layout.loading_view,this,false);
        return view;
    }

    private View getNetworkErrorView() {
        View view= LayoutInflater.from(getContext()).inflate(R.layout.error_view,this,false);
        RelativeLayout error_view=view.findViewById(R.id.error_view);
        error_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onReTryListener!=null)
                    onReTryListener.onReTry();
            }
        });
        return view;
    }

    private View getEmptyView() {
        View view= LayoutInflater.from(getContext()).inflate(R.layout.empty_view,this,false);
        return view;
    }
    private onReTryListener onReTryListener;
    public void setOnReTryListener(onReTryListener onReTryListener){
            this.onReTryListener=onReTryListener;
    }
    public interface onReTryListener{
        void onReTry();
    }


}
