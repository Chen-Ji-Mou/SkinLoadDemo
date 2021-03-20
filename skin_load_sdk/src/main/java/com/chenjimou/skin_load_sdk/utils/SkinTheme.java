package com.chenjimou.skin_load_sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * 工具类，用于获得Theme中的属性值
 */
public class SkinTheme {

    private static final int[] APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS = {
            android.R.attr.colorPrimaryDark
    };

    private static final int[] STATUSBAR_COLOR_ATTRS = {
            android.R.attr.statusBarColor,
            android.R.attr.navigationBarColor
    };

    /**
     * 获得原始app中设置给Theme属性的资源id
     * @param context 原始app的context
     * @param attrs 原始app的属性集
     * @return 原始app的资源id
     */
    public static int[] getResId(Context context, int[] attrs) {
        int[] resIds = new int[attrs.length];
        TypedArray a = context.obtainStyledAttributes(attrs);
        for (int i = 0; i < attrs.length; i++) {
            resIds[i] = a.getResourceId(i, 0);
        }
        a.recycle();
        return resIds;
    }

    /**
     * 修改系统栏和状态栏的颜色
     */
    public static void updateStatusBarColor(Activity activity) {

        // Android 5.0以上才能修改
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        // 获得设置给系统栏和状态栏的资源id
        int[] resIds = getResId(activity, STATUSBAR_COLOR_ATTRS);
        int statusBarColorResId = resIds[0];
        int navigationBarColorResId = resIds[1];

        // 如果是直接在style中写死固定的颜色值，则statusBarColorResId将为0
        if (statusBarColorResId != 0) {
            int color = SkinResources.getInstance().getSkinColor(statusBarColorResId);
            activity.getWindow().setStatusBarColor(color);
        } else {
            // 如果是直接在style中写死固定的颜色值，我们就去获取colorPrimaryDark属性的资源id
            int colorPrimaryDarkResId = getResId(activity, APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS)[0];
            if (colorPrimaryDarkResId != 0) {
                int color = SkinResources.getInstance().getSkinColor(colorPrimaryDarkResId);
                activity.getWindow().setStatusBarColor(color);
            }
        }
        if (navigationBarColorResId != 0) {
            int color = SkinResources.getInstance().getSkinColor(navigationBarColorResId);
            activity.getWindow().setNavigationBarColor(color);
        }
    }
}
