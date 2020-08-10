package com.example.himalaya.utils;

import android.location.Location;
import android.util.Log;

public class LogUtil {
    private static boolean isRelease;
    private static String mTAG;

    public static void init(String mTag,boolean isRelease){
        LogUtil.isRelease =isRelease;
        LogUtil.mTAG=mTag;
    }

    public static void d(String TAG,String content){
        if(!isRelease){
            Log.d(mTAG+":"+TAG, content);
        }
    }
    public static void w(String TAG,String content){
        if(!isRelease){
            Log.w(mTAG+":"+TAG, content);
        }
    }

    public static void e(String TAG,String content){
        if(!isRelease){
            Log.e(mTAG+":"+TAG, content);
        }
    }

}
