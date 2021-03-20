package com.chenjimou.skin_load_sdk.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

/**
 * 工具类，用于获取皮肤包apk的资源
 */
public class SkinResources {

    // 皮肤包apk的名称
    private String mSkinPkgName;
    // 是否使用默认的皮肤
    private boolean isDefaultSkin = true;
    // app原始的resource
    private final Resources mAppResources;
    // 皮肤包apk的resource
    private Resources mSkinResources;
    private volatile static SkinResources instance;

    private SkinResources(Context context) {
        mAppResources = context.getResources();
    }

    public static void init(Context context) {
        if (instance == null) {
            synchronized (SkinResources.class) {
                if (instance == null) {
                    instance = new SkinResources(context);
                }
            }
        }
    }

    public static SkinResources getInstance() {
        return instance;
    }

    /**
     * 重置已经记录了的皮肤包apk
     */
    public void reset() {
        mSkinResources = null;
        mSkinPkgName = "";
        isDefaultSkin = true;
    }

    /**
     * 设置换肤所使用的apk
     * @param resources 皮肤包apk的resource
     * @param pkgName 皮肤包apk的名称
     */
    public void setSkinApk(Resources resources, String pkgName) {
        mSkinResources = resources;
        mSkinPkgName = pkgName;
        isDefaultSkin = TextUtils.isEmpty(pkgName) || resources == null;
    }

    /**
     * 通过原始app中资源的名称和类型，获取到皮肤包apk中对应的资源id
     * @param resId 原始app中的资源id
     * @return 皮肤包apk中对应的资源id
     */
    public int getSkinIdentifier(int resId){
        if(isDefaultSkin){
            return resId;
        }
        String resName = mAppResources.getResourceEntryName(resId);
        String resType = mAppResources.getResourceTypeName(resId);
        return mSkinResources.getIdentifier(resName, resType, mSkinPkgName);
    }

    /**
     * 通过原始app中的资源id，获取到皮肤包apk中对应资源所表示的颜色值
     * @param resId 原始app中的资源id
     * @return 皮肤包apk中对应资源所表示的颜色值
     */
    public int getSkinColor(int resId){
        if(isDefaultSkin){
            return mAppResources.getColor(resId);
        }
        int skinId = getSkinIdentifier(resId);
        if(skinId == 0){
            return mAppResources.getColor(resId);
        }
        return mSkinResources.getColor(skinId);
    }

    /**
     * 通过原始app中的资源id，获取到皮肤包apk中对应资源所表示的Drawable对象
     * @param resId 原始app中的资源id
     * @return 皮肤包apk中对应资源所表示的Drawable对象
     */
    public Drawable getSkinDrawable(int resId) {
        if (isDefaultSkin) {
            return mAppResources.getDrawable(resId);
        }
        int skinId = getSkinIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getDrawable(resId);
        }
        return mSkinResources.getDrawable(skinId);
    }

    /**
     * 当控件设置了背景时，这个背景有可能是纯颜色或者是图片，需要判断
     * @param resId 原始app中的资源id
     * @return 根据判断后从皮肤包apk中获取到的值
     */
    public Object getSkinBackground(int resId) {
        String resourceTypeName = mAppResources.getResourceTypeName(resId);
        if ("color".equals(resourceTypeName)) {
            return getSkinColor(resId);
        } else {
            return getSkinDrawable(resId);
        }
    }
}
