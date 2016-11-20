package com.wuxiaosu.litepal_demo;

import android.app.Application;

import org.litepal.LitePal;

/**
 * Created by Su on 2016/11/18.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
