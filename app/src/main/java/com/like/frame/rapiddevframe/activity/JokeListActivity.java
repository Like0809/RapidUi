package com.like.frame.rapiddevframe.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.like.frame.rapiddevframe.ApiUrl;
import com.like.frame.rapiddevframe.R;
import com.like.frame.rapiddevframe.entity.Joke;
import com.like.rapidui.base.DataParam;
import com.like.rapidui.base.PagingParam;
import com.like.rapidui.base.BaseListActivity;
import com.like.rapidui.ui.icon.widget.IconTextView;
import com.like.rapidui.ui.picasso.transformation.CircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class JokeListActivity extends BaseListActivity<Joke> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load();
    }

    @Override
    public String getUrl() {
        return ApiUrl.API_JOKE;
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "2");
        return params;
    }

    @Override
    public PagingParam getPagingParam() {
        return new PagingParam("", "page");
    }

    @Override
    public DataParam getDataParam() {
        return new DataParam(200, "code", "msg", "data");
    }

    @Override
    public int getItemView() {
        return R.layout.item_joke_text;
    }

    @Override
    public void convert(BaseViewHolder helper, final Joke item) {
        helper.setText(R.id.name, item.getName());
        helper.setText(R.id.text, item.getText());
        if (!TextUtils.isEmpty(item.getProfile_image()))
            Picasso.get().load(item.getProfile_image()).placeholder(R.mipmap.no_pic).error(R.mipmap.no_pic).transform(new CircleTransformation()).into((ImageView) helper.getView(R.id.avatar));
        else
            Picasso.get().load(R.mipmap.no_pic).transform(new CircleTransformation()).into((ImageView) helper.getView(R.id.avatar));
        ((IconTextView) helper.getView(R.id.comment)).setNormalText(String.format("{fa-comments-o} %s", item.getComment()));
        ((IconTextView) helper.getView(R.id.hate)).setNormalText(String.format("{fa-thumbs-o-down} %s", item.getHate()));
        ((IconTextView) helper.getView(R.id.love)).setNormalText(String.format("{fa-thumbs-o-up} %s", item.getLove()));
    }
}