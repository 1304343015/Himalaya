package com.example.himalaya.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.fragments.HistoryFragment;
import com.example.himalaya.fragments.RecommendFragment;
import com.example.himalaya.fragments.SubscriptionFragment;

import java.util.HashMap;
import java.util.Map;

public class MainContentAdapter extends FragmentPagerAdapter {
    private Map<Integer,BaseFragment> sCache=new HashMap<>();
    public MainContentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        BaseFragment baseFragment=sCache.get(position);
        if(baseFragment!=null){
            return sCache.get(position);
        }else{
            switch (position){
                case 0:
                    baseFragment=new RecommendFragment();
                    break;
                case 1:
                    baseFragment=new SubscriptionFragment();
                    break;
                case 2:
                    baseFragment=new HistoryFragment();
            }
            sCache.put(position,baseFragment);
            return baseFragment;
        }



    }

    @Override
    public int getCount() {
        return 3;
    }
}
