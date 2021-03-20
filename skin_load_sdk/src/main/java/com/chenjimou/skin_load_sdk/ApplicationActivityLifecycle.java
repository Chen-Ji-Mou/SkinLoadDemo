package com.chenjimou.skin_load_sdk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.LayoutInflaterCompat;

import com.chenjimou.skin_load_sdk.utils.SkinTheme;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * 监听整个程序（application）中所有activity的生命周期
 */
public class ApplicationActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    // 被观察者，SkinManager
    private final Observable mSkinManager;
    // 记录不同的activity对应的Factory
    private final Map<Activity, SkinLayoutInflaterFactory> mLayoutInflaterFactories = new HashMap<>();

    public ApplicationActivityLifecycle(Observable observable) {
        mSkinManager = observable;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        // 更新状态栏
        SkinTheme.updateStatusBarColor(activity);
        // 获得Activity的布局加载器
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        try {
            // 通过反射设置字段"mFactorySet"为false
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible(true);
            field.setBoolean(layoutInflater, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 设置我们自定义的Factory
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = new SkinLayoutInflaterFactory(activity);
        LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutInflaterFactory);
        mLayoutInflaterFactories.put(activity, skinLayoutInflaterFactory);
        // 给SkinManager添加观察者
        mSkinManager.addObserver(skinLayoutInflaterFactory);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        // 当activity销毁的时候，移除观察者
        SkinLayoutInflaterFactory observer = mLayoutInflaterFactories.remove(activity);
        SkinManager.getInstance().deleteObserver(observer);
    }
}
