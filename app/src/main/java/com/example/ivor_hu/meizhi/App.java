package com.example.ivor_hu.meizhi;

import android.app.Application;

import com.example.ivor_hu.meizhi.db.DatabaseHelper;

/**
 * Created by Ivor on 2016/2/12.
 */
public class App extends Application {
    private static App sInstance;

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        DatabaseHelper.getInstance(this);
    }
}
