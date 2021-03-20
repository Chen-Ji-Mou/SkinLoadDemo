package com.chenjimou.skin_load_sdk;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.chenjimou.skin_load_sdk.utils.SkinResources;
import com.chenjimou.skin_load_sdk.utils.SkinTheme;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装类，将一个需要解析的xml文件中的每一个View进行了封装
 */
public class SkinAttribute {

    // 我们需要进行换肤的属性名称
    private static final List<String> mAttributes = new ArrayList<>();
    // 记录换肤需要操作的View和需要换肤的属性信息
    private final List<SkinView> mSkinViews = new ArrayList<>();

    static {
        mAttributes.add("background");
        mAttributes.add("src");
        mAttributes.add("drawableLeft");
        mAttributes.add("drawableTop");
        mAttributes.add("drawableRight");
        mAttributes.add("drawableBottom");
    }

    /**
     * 筛选View中哪些是需要并且可以执行换肤的属性，进行记录并执行换肤
     * @param view 所要筛选的View
     * @param attrs View的属性集
     */
    public void screen(View view, AttributeSet attrs) {
        List<SkinPair> mSkinPars = new ArrayList<>();
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String attributeName = attrs.getAttributeName(i);
            if (mAttributes.contains(attributeName)) {
                String attributeValue = attrs.getAttributeValue(i);
                // 如果属性值是写死的颜色代码，是无法执行换肤的
                if (attributeValue.startsWith("#")) {
                    continue;
                }
                int resId;
                // 如果属性值是使用系统的资源
                if (attributeValue.startsWith("?")) {
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    resId = SkinTheme.getResId(view.getContext(), new int[]{attrId})[0];
                } else {
                    resId = Integer.parseInt(attributeValue.substring(1));
                }
                // 记录属性
                SkinPair skinPair = new SkinPair(attributeName, resId);
                mSkinPars.add(skinPair);
            }
        }
        // 执行换肤
        if (!mSkinPars.isEmpty() || view instanceof SkinViewSupport) {
            SkinView skinView = new SkinView(view, mSkinPars);
            skinView.applySkin();
            // 记录View
            mSkinViews.add(skinView);
        }
    }

    /**
     * 对记录的view中记录的属性进行换肤
     */
    public void applySkin() {
        for (SkinView mSkinView : mSkinViews) {
            mSkinView.applySkin();
        }
    }

    /**
     * 封装需要进行换肤的View
     */
    static class SkinView {

        // 需要换肤的View
        View view;
        // 这个View中需要换肤的属性
        List<SkinPair> skinPairs;

        public SkinView(View view, List<SkinPair> skinPairs) {
            this.view = view;
            this.skinPairs = skinPairs;
        }

        /**
         * 对View中记录的属性值进行修改，执行换肤
         */
        public void applySkin() {
            // 如果是自定义View，抛给使用者自己解决
            if (view instanceof SkinViewSupport) {
                ((SkinViewSupport) view).applySkin();
            }
            for (SkinPair skinPair : skinPairs) {
                Drawable left = null, top = null, right = null, bottom = null;
                switch (skinPair.attributeName) {
                    case "background":
                        Object background = SkinResources.getInstance().getSkinBackground(skinPair
                                .resId);
                        //背景可能是纯颜色也可能是图片
                        if (background instanceof Integer) {
                            view.setBackgroundColor((int) background);
                        } else {
                            ViewCompat.setBackground(view, (Drawable) background);
                        }
                        break;
                    case "src":
                        background = SkinResources.getInstance().getSkinBackground(skinPair
                                .resId);
                        if (background instanceof Integer) {
                            ((ImageView) view).setImageDrawable(new ColorDrawable((Integer)
                                    background));
                        } else {
                            ((ImageView) view).setImageDrawable((Drawable) background);
                        }
                        break;
                    case "drawableLeft":
                        left = SkinResources.getInstance().getSkinDrawable(skinPair.resId);
                        break;
                    case "drawableTop":
                        top = SkinResources.getInstance().getSkinDrawable(skinPair.resId);
                        break;
                    case "drawableRight":
                        right = SkinResources.getInstance().getSkinDrawable(skinPair.resId);
                        break;
                    case "drawableBottom":
                        bottom = SkinResources.getInstance().getSkinDrawable(skinPair.resId);
                        break;
                    default:
                        break;
                }
                if (null != left || null != right || null != top || null != bottom) {
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
                }
            }
        }
    }

    /**
     * 封装View中需要进行换肤的属性
     */
    static class SkinPair {

        //属性名称
        String attributeName;
        //设置给属性的资源id
        int resId;

        public SkinPair(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }
}
