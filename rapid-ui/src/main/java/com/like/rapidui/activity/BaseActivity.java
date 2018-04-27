package com.like.rapidui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.like.rapidui.Request;
import com.like.rapidui.DataParam;
import com.like.rapidui.PagingParam;
import com.like.rapidui.R;
import com.like.rapidui.RapidUi;
import com.like.rapidui.RequestListener;
import com.like.rapidui.callback.DataLoader;
import com.like.rapidui.ui.dialog.NetworkDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created By Like on 2016/8/12.
 */
@SuppressWarnings("ALL")
public class BaseActivity<T> extends AppCompatActivity {
    protected String Tag = getClass().getSimpleName();
    protected Toolbar mToolbar;
    protected NetworkDialog mNetDialog;
    protected Gson mJson;
    protected final int PULL_UP = 1, PULL_DOWN = 2, INIT = 0;
    protected int mPageNum = 1, mPageSize = 10;
    protected Type mEntityType;

    public int getContentView() {
        return R.layout.rapid_activity_base;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mEntityType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        mJson = new Gson();
        setContentView(getContentView());
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setTitle(getClass().getSimpleName());
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
            }
            mToolbar.setNavigationOnClickListener(v -> {
                hideIME();
                finish();
            });
        }
    }

    public void load() {
        load(INIT);
    }

    public void load(int type) {
        load(getUrl(), getParams(), getHeaders(), getDataParam(), getPagingParam(), type, getDataLoader());
    }

    public void load(String url, Map<String, String> params, DataParam dataParam) {
        load(url, params, getHeaders(), dataParam, getPagingParam(), INIT, getDataLoader());
    }

    public void load(String url, Map<String, String> params, Map<String, String> headers, final DataParam dataParam, PagingParam pagingParam, int loadType, DataLoader dataLoader) {
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
        runOnUiThread(() -> {
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
            BaseActivity.this.onResponse(request, json, entity);
            BaseActivity.this.onComplete(request, 0);
        });
    }

    protected void failed(Request request, int code, String message) {
        runOnUiThread(() -> {
            BaseActivity.this.onError(request, code, message);
            BaseActivity.this.onComplete(request, -1);
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


    @Override
    protected void onDestroy() {
        hideIME();
        super.onDestroy();
    }

    public void hideIME() {
        if (getCurrentFocus() != null)
            hideKeyboard(getCurrentFocus().getApplicationWindowToken());
    }

    protected void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert im != null;
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
    };

    public void verifyStoragePermissions(Activity activity, int requestCode) {
        //  int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // if (permission != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                requestCode
        );
        //  }
    }

    protected void jumpWithAnimation(Class<?> clazz) {
        startActivity(new Intent(this, clazz));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    protected void jumpTo(Class<?> clazz) {
        startActivity(new Intent(this, clazz));
    }

    protected void jumpForResult(Class<?> clazz, int code) {
        startActivityForResult(new Intent(this, clazz), code);
    }

    protected void jumpTo(Class<?> clazz, Map<String, Object> data) {
        Intent intent = new Intent(this, clazz);
        putExtras(data, intent);
        startActivity(intent);
    }

    protected void jumpForResult(Class<?> clazz, Map<String, Object> data, int code) {
        Intent intent = new Intent(this, clazz);
        putExtras(data, intent);
        startActivityForResult(intent, code);
    }

    private void putExtras(Map<String, Object> extras, Intent intent) {
        if (extras != null) {
            for (String name : extras.keySet()) {
                Object obj = extras.get(name);
                if (obj instanceof Boolean) {
                    intent.putExtra(name, (Boolean) obj);
                } else if (obj instanceof Byte) {
                    intent.putExtra(name, (Byte) obj);
                } else if (obj instanceof Character) {
                    intent.putExtra(name, (Character) obj);
                } else if (obj instanceof Short) {
                    intent.putExtra(name, (Short) obj);
                } else if (obj instanceof Integer) {
                    intent.putExtra(name, (Integer) obj);
                } else if (obj instanceof Long) {
                    intent.putExtra(name, (Long) obj);
                } else if (obj instanceof Float) {
                    intent.putExtra(name, (Float) obj);
                } else if (obj instanceof Double) {
                    intent.putExtra(name, (Double) obj);
                } else if (obj instanceof String) {
                    intent.putExtra(name, (String) obj);
                } else if (obj instanceof CharSequence) {
                    intent.putExtra(name, (CharSequence) obj);
                } else if (obj instanceof Parcelable) {
                    intent.putExtra(name, (Parcelable) obj);
                } else if (obj instanceof Parcelable[]) {
                    intent.putExtra(name, (Parcelable[]) obj);
                } else if (obj instanceof Boolean[]) {
                    intent.putExtra(name, (Boolean[]) obj);
                } else if (obj instanceof Byte[]) {
                    intent.putExtra(name, (Byte[]) obj);
                } else if (obj instanceof Short[]) {
                    intent.putExtra(name, (Short[]) obj);
                } else if (obj instanceof ArrayList) {
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
                } else if (obj instanceof Serializable) {
                    intent.putExtra(name, (Serializable) obj);
                } else if (obj instanceof Character[]) {
                    intent.putExtra(name, (Character[]) obj);
                } else if (obj instanceof Integer[]) {
                    intent.putExtra(name, (Integer[]) obj);
                } else if (obj instanceof Long[]) {
                    intent.putExtra(name, (Long[]) obj);
                } else if (obj instanceof Float[]) {
                    intent.putExtra(name, (Float[]) obj);
                } else if (obj instanceof Double[]) {
                    intent.putExtra(name, (Double[]) obj);
                } else if (obj instanceof String[]) {
                    intent.putExtra(name, (String[]) obj);
                } else if (obj instanceof CharSequence[]) {
                    intent.putExtra(name, (CharSequence[]) obj);
                } else if (obj instanceof Bundle) {
                    intent.putExtra(name, (Bundle) obj);
                }

            }
        }
    }

    protected void showDialog() {
        showDialog("加载中");
    }

    protected void showDialog(String message) {
        showDialog(message, false);
    }

    protected void showDialog(String message, boolean cancelable) {
        hideDialog();
        mNetDialog = new NetworkDialog(this, message);
        mNetDialog.setCancelable(cancelable);
        mNetDialog.show();
    }


    protected void hideDialog() {
        if (mNetDialog != null && mNetDialog.isShowing())
            mNetDialog.dismiss();
    }

    protected void showShort(String message) {
        Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected void showLong(String message) {
        Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
