package com.like.rapidui.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.like.rapidui.RapidUi;
import com.like.rapidui.ui.dialog.NetworkDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
    protected Toast mToast;

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
        load(LoadType.INIT);
    }

    public void load(String text) {
        if (!TextUtils.isEmpty(text)) {
            showDialog(text);
        }
        load(LoadType.INIT);
    }

    public void load(LoadType loadType) {
        load(getUrl(), getParams(), getHeaders(), getDataParam(), getPagingParam(), loadType, getDataLoader());
    }

    public void load(String url, Map<String, String> params, DataParam dataParam) {
        load(url, params, dataParam, null);
    }

    public void load(String url, Map<String, String> params, DataParam dataParam, String text) {
        if (!TextUtils.isEmpty(text)) {
            showDialog(text);
        }
        load(url, params, getHeaders(), dataParam, getPagingParam(), LoadType.INIT, getDataLoader());
    }


    public void load(String url, Map<String, String> params, Map<String, String> headers, final DataParam dataParam, PagingParam pagingParam, LoadType loadType, DataLoader dataLoader) {
        if (loadType == LoadType.INIT || loadType == LoadType.PULL_DOWN) {
            mPageNum = 1;
        }
        if (params == null) {
            params = new HashMap<>();
        }
        Request request = new Request(url, params, headers, loadType);
        if (dataLoader == null) {
            dataLoader = RapidUi.getInstance().getDataLoader();
        }
        if (pagingParam == null) {
            pagingParam = RapidUi.getInstance().getPagingParam();
        }
        params.put(pagingParam.getPageSizeParam(), "" + mPageSize);
        params.put(pagingParam.getPageNumParam(), "" + mPageNum);
        dataLoader.load(request, new RequestListener() {
            @Override
            public void onResponse(final Request request, final String json) {
                DataParam mDataParam;
                if (dataParam != null) {
                    mDataParam = dataParam;
                } else mDataParam = RapidUi.getInstance().getDataParam();
                try {
                    final Object jsonObj = new JSONTokener(json).nextValue();
                    if (jsonObj instanceof JSONArray) {
                        success(request, jsonObj.toString());
                    } else if (jsonObj instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) jsonObj;
                        if (jsonObject.has(mDataParam.getDataParam()) && jsonObject.get(mDataParam.getDataParam()) != null && !jsonObject.get(mDataParam.getDataParam()).equals(JSONObject.NULL)) {
                            success(request, jsonObject.get(mDataParam.getDataParam()).toString());
                        } else {
                            if (jsonObject.has(mDataParam.getCodeParam())) {
                                final int code = jsonObject.getInt(mDataParam.getCodeParam());
                                try {
                                    final String message = jsonObject.getString(mDataParam.getMessageParam());
                                    if (code == mDataParam.getSuccessCode()) {
                                        success(request, message);
                                    } else {
                                        failed(request, code, message);
                                    }
                                } catch (JSONException e) {
                                    failed(request, -1, "message字段解析失败，请检查DataParam设置与数据源是否一致");
                                }
                            } else {
                                success(request, json);
                            }
                        }
                    } else {
                        failed(request, -1, "非Json格式数据请勿使用本框架内的网络请求");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    failed(request, -1, "数据解析失败");
                }
            }

            @Override
            public void onError(Request request, int code, String message) {
                failed(request, code, message);
            }

        });
    }

    protected void success(final Request request, final String json) {
        getActivity().runOnUiThread(() -> {
            String url = request.getUrl();
            T entity = null;
            try {
                if (mEntityType == String.class) {
                    entity = (T) json;
                } else {
                    entity = (T) mJson.fromJson((String) json, mEntityType);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            BaseFragment.this.onResponse(request, json, entity);
            BaseFragment.this.onComplete(request, 0);
        });
    }

    protected void failed(Request request, int code, String message) {
        getActivity().runOnUiThread(() -> {
            BaseFragment.this.onError(request, code, message);
            BaseFragment.this.onComplete(request, -1);
        });
    }

    public void onError(Request request, int code, String message) {
        showShort(message);
    }

    /**
     * @param request 请求体
     * @param json    返回值
     * @param data    BaseActivity对应的第一泛型对象，尽量不要使用
     */
    public void onResponse(Request request, CharSequence json, T data) {
    }

    /**
     * @param status 状态，-1为失败，0为成功
     */
    public void onComplete(Request request, int status) {
        hideDialog();
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
                if (obj instanceof Boolean) {
                    intent.putExtra(name, (Boolean) obj);
                }
                if (obj instanceof Byte) {
                    intent.putExtra(name, (Byte) obj);
                }
                if (obj instanceof Character) {
                    intent.putExtra(name, (Character) obj);
                }
                if (obj instanceof Short) {
                    intent.putExtra(name, (Short) obj);
                }
                if (obj instanceof Integer) {
                    intent.putExtra(name, (Integer) obj);
                }
                if (obj instanceof Long) {
                    intent.putExtra(name, (Long) obj);
                }
                if (obj instanceof Float) {
                    intent.putExtra(name, (Float) obj);
                }
                if (obj instanceof Double) {
                    intent.putExtra(name, (Double) obj);
                }
                if (obj instanceof String) {
                    intent.putExtra(name, (String) obj);
                }
                if (obj instanceof CharSequence) {
                    intent.putExtra(name, (CharSequence) obj);
                }
                if (obj instanceof Parcelable) {
                    intent.putExtra(name, (Parcelable) obj);
                }
                if (obj instanceof Parcelable[]) {
                    intent.putExtra(name, (Parcelable[]) obj);
                }
                if (obj instanceof Boolean[]) {
                    intent.putExtra(name, (Boolean[]) obj);
                }
                if (obj instanceof Byte[]) {
                    intent.putExtra(name, (Byte[]) obj);
                }
                if (obj instanceof Short[]) {
                    intent.putExtra(name, (Short[]) obj);
                }
                if (obj instanceof ArrayList) {
                    ArrayList list = (ArrayList) obj;
                    if (list.size() > 0) {
                        Object o = list.get(0);
                        if (o instanceof Parcelable) {
                            intent.putParcelableArrayListExtra(name, (ArrayList<Parcelable>) obj);
                        } else if (o instanceof Integer) {
                            intent.putIntegerArrayListExtra(name, (ArrayList<Integer>) obj);
                        } else if (o instanceof String) {
                            intent.putStringArrayListExtra(name, (ArrayList<String>) obj);
                        } else if (o instanceof CharSequence) {
                            intent.putCharSequenceArrayListExtra(name, (ArrayList<CharSequence>) obj);
                        }
                    }
                }
                if (obj instanceof Serializable) {
                    intent.putExtra(name, (Serializable) obj);
                }
                if (obj instanceof Character[]) {
                    intent.putExtra(name, (Character[]) obj);
                }
                if (obj instanceof Integer[]) {
                    intent.putExtra(name, (Integer[]) obj);
                }
                if (obj instanceof Long[]) {
                    intent.putExtra(name, (Long[]) obj);
                }
                if (obj instanceof Float[]) {
                    intent.putExtra(name, (Float[]) obj);
                }
                if (obj instanceof Double[]) {
                    intent.putExtra(name, (Double[]) obj);
                }
                if (obj instanceof String[]) {
                    intent.putExtra(name, (String[]) obj);
                }
                if (obj instanceof CharSequence[]) {
                    intent.putExtra(name, (CharSequence[]) obj);
                }
                if (obj instanceof Bundle) {
                    intent.putExtra(name, (Bundle) obj);
                }
            }
        }
    }

    protected void showShort(String message) {
        if (mToast == null) {
            mToast = Toast.makeText(getContext().getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setText(message);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
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
