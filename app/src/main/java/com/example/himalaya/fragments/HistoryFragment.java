package com.example.himalaya.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseFragment;

public class HistoryFragment extends BaseFragment {
    @Override
    public View loadView(LayoutInflater inflater, ViewGroup container) {
        View view=inflater.inflate(R.layout.fragment_history,container,false);
        return view;
    }
}
