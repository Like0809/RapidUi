package com.like.rapidui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Map;


/**
 * Created By Like on 2016/8/12.
 */
@SuppressWarnings("All")
@SuppressLint("All")
public class BaseFragment extends Fragment {
    protected String Tag;

    @Override
    public void onAttach(Context activity) {
        Tag = getClass().getSimpleName();
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
    };


    protected void verifyStoragePermissions(int requestCode) {
        requestPermissions(PERMISSIONS_STORAGE, requestCode);
    }

    protected void jumpTo(Class<?> clazz) {
        startActivity(new Intent(getActivity(), clazz));
    }

    protected void jumpTo(Class<?> clazz, Map<String, Object> data) {
        Intent intent = new Intent(getActivity(), clazz);
        putExtras(data, intent);
        startActivity(intent);
    }

    private void putExtras(Map<String, Object> extras, Intent intent) {
        if (extras != null) {
            for (String name : extras.keySet()) {
                Object obj = extras.get(name);
                if (obj instanceof String) {
                    intent.putExtra(name, (String) obj);
                }
                if (obj instanceof Integer) {
                    intent.putExtra(name, (Integer) obj);
                }
                if (obj instanceof String[]) {
                    intent.putExtra(name, (String[]) obj);
                }
                if (obj instanceof Long) {
                    intent.putExtra(name, (Long) obj);
                }
                if (obj instanceof Boolean) {
                    intent.putExtra(name, (Boolean) obj);
                }
            }
        }
    }

    protected void showShort(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 修正 Toolbar 的位置
     * 在 Android 4.4 版本下无法显示内容在 StatusBar 下，所以无需修正 Toolbar 的位置
     */
    protected void fixToolbar(Toolbar toolbar) {
        int statusHeight = getStatusBarHeight(getActivity());
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
        layoutParams.setMargins(0, statusHeight, 0, 0);
    }

    /**
     * 获取系统状态栏高度
     */
    public int getStatusBarHeight(Context context) {
        Class<?> c;
        Object obj;
        Field field;
        int x, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
}
