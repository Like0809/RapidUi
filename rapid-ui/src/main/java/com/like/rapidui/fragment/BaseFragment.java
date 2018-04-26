package com.like.rapidui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.like.rapidui.DataParam;
import com.like.rapidui.PagingParam;
import com.like.rapidui.RapidUi;
import com.like.rapidui.Request;
import com.like.rapidui.RequestListener;
import com.like.rapidui.callback.DataLoader;
import com.like.rapidui.ui.dialog.NetworkDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * Created By Like on 2016/8/12.
 */
@SuppressWarnings("All")
@SuppressLint("All")
public class BaseFragment<T> extends Fragment {

    protected View mRootView;
    protected final int PULL_UP = 1, PULL_DOWN = 2, INIT = 0;
    protected int mPageNum = 1, mPageSize = 10;
    protected Type mEntityType;
    protected NetworkDialog mNetDialog;
    protected Gson mJson;

    public int getContentView() {
        return 0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mEntityType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        mJson = new Gson();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView != null)
            return mRootView;
        mRootView = inflater.inflate(getContentView(), null, false);
        return mRootView;
    }

    public void load() {
        load(INIT);
    }

    public void load(int type) {
        load(getUrl(), getParams(), getHeaders(), type);
    }

    public void load(String url, Map<String, String> params, Map<String, String> headers, int loadType) {
        Request request = new Request(url, params, headers, loadType);
        DataLoader dataLoader = getDataLoader();
        if (dataLoader == null) {
            dataLoader = RapidUi.getInstance(getActivity()).getDataLoader();
        }
        final DataParam dataParam;
        if (getDataParam() == null) {
            dataParam = RapidUi.getInstance(getActivity()).getDataParam();
        } else {
            dataParam = getDataParam();
        }
        PagingParam pagingParam = getPagingParam();
        if (pagingParam == null) {
            pagingParam = RapidUi.getInstance(getActivity()).getPagingParam();
        }
        params.put(pagingParam.getPageSizeParam(), "" + mPageSize);
        params.put(pagingParam.getPageNumParam(), "" + mPageNum);
        dataLoader.load(request, new RequestListener() {
            @Override
            public void onResponse(final Request request, final String json) {
                try {
                    final Object jsonObj = new JSONTokener(json).nextValue();
                    if (jsonObj instanceof JSONArray) {
                        success(request, jsonObj.toString());
                    } else if (jsonObj instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) jsonObj;
                        if (jsonObject.has(dataParam.getDataParam())) {
                            success(request, jsonObject.get(dataParam.getDataParam()).toString());
                        } else {
                            if (jsonObject.has(dataParam.getCodeParam())) {
                                final int code = jsonObject.getInt(dataParam.getCodeParam());
                                if (code == dataParam.getSuccessCode()) {
                                    success(request, jsonObject.getString(dataParam.getMessageParam()));
                                } else {
                                    failed(request, code, jsonObject.getString(dataParam.getMessageParam()));
                                }
                            } else {
                                success(request, json);
                            }
                        }
                    } else {
                        failed(request, -1, "数据解析出错");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    failed(request, -1, "数据解析出错");
                }
            }

            @Override
            public void onError(Request request, int code, String message) {
                failed(request, code, message);
            }

        });
    }

    private void success(final Request request, final String json) {
        getActivity().runOnUiThread(() -> {
            BaseFragment.this.onResponse(request, json);
            BaseFragment.this.onComplete(request, 0);
        });
    }

    private void failed(Request request, int code, String message) {
        getActivity().runOnUiThread(() -> {
            BaseFragment.this.onError(request, -1, message);
            BaseFragment.this.onComplete(request, -1);
        });
    }


    public void onError(Request request, int code, String message) {
    }

    public void onResponse(Request request, CharSequence json) {
        if (mEntityType == String.class) {
            onResponse(request, (T) json);
        }
        onResponse(request, (T) mJson.fromJson((String) json, mEntityType));
    }

    public void onResponse(Request request, T data) {
    }

    /**
     * @param status 状态，-1为失败，0为成功
     */
    public void onComplete(Request request, int status) {
    }

    public String getUrl() {
        return null;
    }

    public DataLoader getDataLoader() {
        return null;
    }

    public DataParam getDataParam() {
        return null;
    }

    public PagingParam getPagingParam() {
        return null;
    }

    public Map<String, String> getParams() {
        return new HashMap<>();
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>();
    }


    protected void showDialog() {
        showDialog("加载中");
    }

    protected void showDialog(String message) {
        showDialog(message, false);
    }

    protected void showDialog(String message, boolean cancelable) {
        hideDialog();
        mNetDialog = new NetworkDialog(getActivity(), message);
        mNetDialog.setCancelable(cancelable);
        mNetDialog.show();
    }

    protected void hideDialog() {
        if (mNetDialog != null && mNetDialog.isShowing())
            mNetDialog.dismiss();
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
