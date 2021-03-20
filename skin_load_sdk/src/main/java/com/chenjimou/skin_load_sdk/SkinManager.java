package com.chenjimou.skin_load_sdk;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.chenjimou.skin_load_sdk.utils.SkinResources;

import java.lang.reflect.Method;
import java.util.Observable;

/**
 * 提供给使用者进行操作的类
 */
public class SkinManager extends Observable {

    private volatile static SkinManager instance;
    private final Application mContext;

    public static void init(Application application) {
        if (instance == null) {
            synchronized (SkinManager.class) {
                if (instance == null) {
                    instance = new SkinManager(application);
                }
            }
        }
    }

    private SkinManager(Application application) {
        mContext = application;
        SkinPreference.init(application);
        SkinResources.init(application);
        // 注册Activity生命周期监听，并设置被观察者
        ApplicationActivityLifecycle skinActivityLifecycle = new ApplicationActivityLifecycle(this);
        application.registerActivityLifecycleCallbacks(skinActivityLifecycle);
        // 初始化SkinPreference和SkinResources
        loadSkin(SkinPreference.getInstance().getSkin());
    }

    public static SkinManager getInstance() {
        return instance;
    }

    /**
     * 加载皮肤并应用
     * @param skinPath 皮肤包apk的路径
     */
    public void loadSkin(String skinPath) {
        if (TextUtils.isEmpty(skinPath)) {
            // 还原默认皮肤
            SkinPreference.getInstance().reset();
            SkinResources.getInstance().reset();
        } else {
            try {
                // 原始app的resource
                Resources appResource = mContext.getResources();

                // 反射创建AssetManager
                AssetManager assetManager = AssetManager.class.newInstance();

                // 设置皮肤包apk的路径
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                addAssetPath.invoke(assetManager, skinPath);

                // 创建皮肤包apk的resource
                Resources skinResource = new Resources(assetManager, appResource.getDisplayMetrics(),
                        appResource.getConfiguration());

                // 获取皮肤包apk的包名
                PackageManager mPm = mContext.getPackageManager();
                PackageInfo info = mPm.getPackageArchiveInfo(skinPath, PackageManager
                        .GET_ACTIVITIES);
                String packageName = info.packageName;
                SkinResources.getInstance().setSkinApk(skinResource, packageName);

                // 记录皮肤包apk的路径和包名
                SkinPreference.getInstance().setSkin(skinPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 通知所有观察者
        setChanged();
        notifyObservers();
    }
}
