package com.example.himalaya.base;

import android.app.Application;

import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CommonRequest mXimalaya = CommonRequest.getInstanse();
        String mAppSecret = "afe063d2e6df361bc9f1fb8bb8210d67";
        mXimalaya.setAppkey("af1d317b871e0e7e2ce45872caa34d9a");
        mXimalaya.setPackid("com.humaxdigital.automotive.ximalaya");
        mXimalaya.init(this ,mAppSecret);
    }
}
