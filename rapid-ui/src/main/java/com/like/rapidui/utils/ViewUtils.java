package com.like.rapidui.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created By Like on 2016/8/15.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ViewUtils {

    private static final String TAG = "DpiUtils";

    private volatile static ViewUtils mInstance;

    // 当前屏幕的densityDpi
    private static float mDmDensityDpi = 0.0f;
    private static int mWidth = 0;
    private static int mHeight = 0;

    private static float mScale = 1.0f;

    // 设计分辨率
    private static int mDesignWidth = 0;
    private static int mDesignHeight = 0;
    private static float mScaleX = 1.0f;
    private static float mScaleY = 1.0f;

    private ViewUtils(Context context) {
        // 获取当前屏幕
        DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
        // 设置DensityDpi,mWidth,mHeight
        mDmDensityDpi = mDisplayMetrics.densityDpi;
        mWidth = mDisplayMetrics.widthPixels;
        mHeight = mDisplayMetrics.heightPixels;
        // 密度因子
        mScale = getDmDensityDpi() / 160;
        Log.i(TAG, " dmDensityDpi:" + mDmDensityDpi + " mWidth:" + mWidth
                + " mHeight:" + mHeight);
    }

    public static ViewUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ViewUtils.class) {
                if (mInstance == null) {
                    mInstance = new ViewUtils(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 密度转换像素
     */
    public int dipToPx(float dipValue) {

        return (int) (dipValue * mScale + 0.5f);

    }

    /**
     * convert sp to its equivalent px
     * <p>
     * 将sp转换为px
     */
    public int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * 像素转换密度
     */
    public int pxToDip(float pxValue) {
        return (int) (pxValue / mScale + 0.5f);
    }

    /**
     * 当前屏幕的density因子
     */
    public float getDmDensityDpi() {
        return mDmDensityDpi;
    }

    /**
     * 获取设备的屏幕宽度(Pixels)
     */
    public int getWidth() {
        return mWidth;
    }

    public int getWidth(float scale) {
        return (int) (mWidth * scale);
    }

    /**
     * 获取设备的屏幕高度(Pixels)
     *
     * @return mHeight
     */
    public int getHeight() {
        return mHeight;
    }

    public int getHeight(float scale) {
        return (int) (mHeight * scale);
    }

    public String toString(TextView view) {
        return view.getText().toString().trim();
    }

    public String toString(EditText view) {
        return view.getText().toString().trim();
    }

    public boolean isEmpty(TextView view) {
        return TextUtils.isEmpty(view.getText().toString().trim());
    }

    public boolean isEmpty(EditText view) {
        return TextUtils.isEmpty(view.getText().toString().trim());
    }
}
