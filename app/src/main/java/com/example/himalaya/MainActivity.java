package com.example.himalaya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.example.himalaya.adapters.MainContentAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    private static final String TAG = "MainActivity";
    private Toolbar tb_title;
    private TabLayout tl_main;
    private ViewPager vp_main;
    private String[] titleArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        titleArray=getResources().getStringArray(R.array.indicator_title);
        initView();
        initTalLayout();
        initViewPager();
    }

    private void initView(){
        tb_title=findViewById(R.id.tb_title);
        setSupportActionBar(tb_title);
        }

    private void initTalLayout() {
        tl_main=findViewById(R.id.tl_main);
        for (String s : titleArray) {
            tl_main.addTab(tl_main.newTab().setText(s));
        }
        tl_main.addOnTabSelectedListener(this);
    }
    private void initViewPager() {
        vp_main=findViewById(R.id.vp_main);
        MainContentAdapter adapter=new MainContentAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vp_main.setAdapter(adapter);
        vp_main.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                tl_main.getTabAt(position).select();
            }
        });
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        vp_main.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
