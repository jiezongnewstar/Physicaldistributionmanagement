package com.xibei.physicaldistributionmanagement;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MyApplication extends Application {

    public static MyApplication myApp;

    public static synchronized Application getInstance() {
        return myApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        Fresco.initialize(this);
    }
}
