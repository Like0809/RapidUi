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
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.like.rapidui.R;
import com.like.rapidui.ui.dialog.NetworkDialog;

import java.io.Serializable;
import java.util.Map;


/**
 * Created By Like on 2016/8/12.
 */
@SuppressWarnings("unused,unchecked")
public class BaseActivity extends AppCompatActivity {
    protected String Tag = getClass().getSimpleName();
    protected Toolbar mToolbar;
    protected NetworkDialog mNetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideIME();
                    finish();
                }
            });
        }
    }

    public int getContentView() {
        return R.layout.rapid_activity_base;
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

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    // 后为工具方法部分
    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
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

    protected boolean isEmpty(String string) {
        return TextUtils.isEmpty(string);
    }

    protected <T extends TextView> boolean isEmpty(T tv) {
        return isEmpty(tv.getText().toString().trim());
    }

    protected void showShort(String message) {
        Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected void showLong(String message) {
        Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
