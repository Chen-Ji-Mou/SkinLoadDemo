package com.chenjimou.skin_load_sdk;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.chenjimou.skin_load_sdk.utils.SkinTheme;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * 自定义的View创建过程
 */
public class SkinLayoutInflaterFactory implements LayoutInflater.Factory2, Observer {

    // 我们需要反射的节点名称前缀
    private static final String[] mClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app.",
            "android.view."
    };
    private static final Class<?>[] mConstructorSignature = new Class[] {
            Context.class, AttributeSet.class
    };
    // 记录对应View的构造函数
    private static final HashMap<String, Constructor<? extends View>> mConstructorMap = new HashMap<>();
    private final SkinAttribute skinAttribute;
    private final Activity activity;

    public SkinLayoutInflaterFactory(Activity activity) {
        this.activity = activity;
        skinAttribute = new SkinAttribute();
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = createSDKView(name, context, attrs);
        if (null == view) {
            view = createView(name, context, attrs);
        }
        if (null != view) {
            // 对创建出来的View进行筛选属性
            skinAttribute.screen(view, attrs);
        }
        return view;
    }

    /**
     * 判断是否是系统的View
     */
    private View createSDKView(String name, Context context, AttributeSet attrs) {
        // 如果包含"."则不是系统的View，可能是使用者自定义的View或者是拓展包中的View，交给系统处理
        if (-1 != name.indexOf('.')) {
            return null;
        }
        // 如果不包含就要在解析的节点名称前，拼上我们定义的前缀尝试去反射
        for (int i = 0; i < mClassPrefixList.length; i++) {
            View view = createView(mClassPrefixList[i] + name, context, attrs);
            if(view != null){
                return view;
            }
        }
        return null;
    }

    private View createView(String name, Context context, AttributeSet
            attrs) {
        Constructor<? extends View> constructor = findConstructor(context, name);
        try {
            return constructor.newInstance(context, attrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过反射去获取View的构造方法
     */
    private Constructor<? extends View> findConstructor(Context context, String name) {
        Constructor<? extends View> constructor = mConstructorMap.get(name);
        if (constructor == null) {
            try {
                Class<? extends View> clazz = context.getClassLoader().loadClass
                        (name).asSubclass(View.class);
                constructor = clazz.getConstructor(mConstructorSignature);
                mConstructorMap.put(name, constructor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return constructor;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        // 执行换肤
        SkinTheme.updateStatusBarColor(activity);
        skinAttribute.applySkin();
    }
}
