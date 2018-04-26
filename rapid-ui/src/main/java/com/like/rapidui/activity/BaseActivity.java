package com.like.rapidui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
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
        load(getUrl(), getParams(), getHeaders(), type);
    }

    public void load(String url, Map<String, String> params, Map<String, String> headers, int loadType) {
        Request request = new Request(url, params, headers, loadType);
        DataLoader dataLoader = getDataLoader();
        if (dataLoader == null) {
            dataLoader = RapidUi.getInstance(this).getDataLoader();
        }
        final DataParam dataParam;
        if (getDataParam() == null) {
            dataParam = RapidUi.getInstance(this).getDataParam();
        } else {
            dataParam = getDataParam();
        }
        PagingParam pagingParam = getPagingParam();
        if (pagingParam == null) {
            pagingParam = RapidUi.getInstance(this).getPagingParam();
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
                        failed(request, -1, "数据解析失败");
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

    private void success(final Request request, final String json) {
        runOnUiThread(() -> {
            BaseActivity.this.onResponse(request, json);
            BaseActivity.this.onComplete(request, 0);
        });
    }

    private void failed(Request request, int code, String message) {
        runOnUiThread(() -> {
            BaseActivity.this.onError(request, -1, message);
            BaseActivity.this.onComplete(request, -1);
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
                if (obj instanceof String) {
                    intent.putExtra(name, (String) obj);
                } else if (obj instanceof Integer) {
                    intent.putExtra(name, (Integer) obj);
                } else if (obj instanceof String[]) {
                    intent.putExtra(name, (String[]) obj);
                } else if (obj instanceof Boolean) {
                    intent.putExtra(name, (Boolean) obj);
                } else {
                    intent.putExtra(name, (Serializable) obj);
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
