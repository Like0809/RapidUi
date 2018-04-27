package com.like.frame.rapiddevframe.activity;

import android.os.Bundle;

import com.like.frame.rapiddevframe.ApiUrl;
import com.like.frame.rapiddevframe.R;
import com.like.frame.rapiddevframe.entity.User;
import com.like.rapidui.Request;
import com.like.rapidui.activity.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends BaseActivity<User> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolbar.setTitle("Json实体获取 展示");
        load();
        findViewById(R.id.base_item_text).setOnClickListener((v) -> {
            Map<String, String> param = new HashMap<>();
            param.put("city", "长沙");
            load("https://www.apiopen.top/weatherApi", param, null);
        });
    }

    @Override
    public String getUrl() {
        return ApiUrl.API_LOGIN;
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("phone", "13594347817");
        params.put("passwd", "123456");
        params.put("key", "00d91e8e0cca2b76f515926a36db68f5");
        return params;
    }

    @Override
    public void onError(Request request, int code, String message) {
        super.onError(request, code, message);
        showShort(message);
        //此处可以处理业务上的错误码等等
    }

    @Override
    public void onResponse(Request request, CharSequence json, User data) {
        super.onResponse(request, json, data);
        showShort((String) json);
    }

}
