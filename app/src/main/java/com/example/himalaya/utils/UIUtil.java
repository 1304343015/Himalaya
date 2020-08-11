package com.example.himalaya.utils;

import android.content.Context;
import android.hardware.display.DisplayManager;

public class UIUtil {
    public static int dip2px(Context context,int dip){
        float density=context.getResources().getDisplayMetrics().density;
        return (int) ((dip*density)+0.5f);
    }

    public static int px2dip(Context context,int px){
        float density=context.getResources().getDisplayMetrics().density;
        return (int) ((px/density)+0.5f);
    }
}
