package com.like.frame.rapiddevframe.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.like.frame.rapiddevframe.R;
import com.like.rapidui.base.PagingParam;
import com.like.rapidui.base.Request;
import com.like.rapidui.base.RequestListener;
import com.like.rapidui.base.DataLoader;
import com.like.rapidui.base.BaseListFragment;

import java.util.ArrayList;

/**
 * Created By Like on 2017/9/29.
 */
@SuppressLint("ALL")
public class ListFragment extends BaseListFragment<String> {

    private Toolbar mToolbar;

    @Override
    public int getContentView() {
        return R.layout.fragment_normal_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolbar = view.findViewById(R.id.toolbar);
        mToolbar.setTitle("RapidUi-普通列表");
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public DataLoader getDataLoader() {
        return new DataLoader() {
            @Override
            public void load(Request request, RequestListener requestListener) {
                if (TextUtils.isEmpty(request.getUrl())) {
                    int pageNum = Integer.valueOf(request.getParams().get("b"));
                    if (pageNum <= 5) {
                        ArrayList<String> list = new ArrayList<>();
                        for (int i = (pageNum - 1) * 10; i < (pageNum - 1) * 10 + 10; i++) {
                            list.add(String.format("item No. %d", i));
                        }
                        requestListener.onResponse(request, new Gson().toJson(list));
                    } else {
                        requestListener.onResponse(request, "[]");
                    }
                }
            }
        };
    }

    @Override
    public PagingParam getPagingParam() {
        return new PagingParam("a", "b");
    }

    @Override
    public int getItemView() {
        return R.layout.item_normal_list;
    }

    @Override
    public void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_name, item);
    }

    @Override
    public void onResume() {
        super.onResume();
        fixToolbar(mToolbar);
    }

}
