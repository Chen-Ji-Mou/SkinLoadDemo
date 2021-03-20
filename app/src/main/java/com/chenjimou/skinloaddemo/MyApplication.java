package com.chenjimou.skinloaddemo;

import android.app.Application;

import com.chenjimou.skin_load_sdk.SkinManager;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.init(this);
    }
}
