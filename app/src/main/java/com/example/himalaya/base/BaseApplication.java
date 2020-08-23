package com.example.himalaya.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;


public class BaseApplication extends Application {
    private static Context context;
    private static Handler handler;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        handler=new Handler();
        CommonRequest mXimalaya = CommonRequest.getInstanse();
        String mAppSecret = "afe063d2e6df361bc9f1fb8bb8210d67";
        mXimalaya.setAppkey("af1d317b871e0e7e2ce45872caa34d9a");
        mXimalaya.setPackid("com.humaxdigital.automotive.ximalaya");
        mXimalaya.init(this ,mAppSecret);
        LogUtil.init(getPackageName(),false);

        XmPlayerManager.getInstance(this).init();
    }

    public static Context getContext(){
        return context;
    }

    public static Handler getHandler(){
        return handler;
    }
}
