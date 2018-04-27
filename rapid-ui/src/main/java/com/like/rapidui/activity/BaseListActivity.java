package com.like.rapidui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.like.rapidui.DataParser;
import com.like.rapidui.Request;
import com.like.rapidui.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * Created By Like on 2018/3/23.
 */

@SuppressWarnings("ALL")
public abstract class BaseListActivity<T> extends BaseActivity<T> {

    protected SwipeRefreshLayout mRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected BaseQuickAdapter mAdapter;
    protected EmptyView mEmptyView;

    @Override
    public int getContentView() {
        return R.layout.rapid_activity_base_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView = findViewById(R.id.recyclerView);
        if (getEnableDrag()) {
            mAdapter = new BaseListActivity.DragAdapter(getItemView());
        } else {
            mAdapter = new BaseListActivity.InnerAdapter(getItemView());
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mEmptyView = new EmptyView(getEmptyView()) {
            @Override
            public void setMessage(String message) {
                if (getView() != null) {
                    ((TextView) (getView().findViewById(R.id.tv_message))).setText(message);
                }
            }

            @Override
            public void setIcon(int resId) {
            }
        };
        mAdapter.setEmptyView(mEmptyView.getView());
        View view = getHeadView();
        if (view != null)
            mAdapter.addHeaderView(view);
        mAdapter.setHeaderAndEmpty(true);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.disableLoadMoreIfNotFullPage();
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPageNum++;
                refresh(PULL_UP);
            }
        }, mRecyclerView);
        mAdapter.setEnableLoadMore(getEnableLoadMore());
        mRefreshLayout = findViewById(R.id.swipeRefresh);
        if (mRefreshLayout != null)
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mPageNum = 1;
                    refresh(PULL_DOWN);
                }
            });
    }

    public void refresh() {
        refresh(INIT);
    }

    public void refresh(int type) {
        load(getUrl(), getParams(), getHeaders(), getDataParam(), getPagingParam(), type, getDataLoader());
    }

    protected final void success(final Request request, final String json) {
        runOnUiThread(() -> {
            String url = getUrl();
            if ((url != null && url.equals(request.getUrl())) || url == null) {

                int type = request.getLoadType();
                if (type != PULL_UP) mAdapter.replaceData(new ArrayList<T>());
                ArrayList<T> list = parseArray((String) json);
                if (list.size() < mPageSize) {
                    mAdapter.loadMoreEnd();
                    //定制EmptyView内容;
                } else {
                    if (type == PULL_UP) {
                        mAdapter.loadMoreComplete();
                    }
                }
                if (type == PULL_DOWN) {
                    if (getEnableLoadMore())
                        mAdapter.setNewData(list);
                    else mAdapter.addData(list);
                } else {
                    mAdapter.addData(list);
                }
                if (mRefreshLayout != null) {
                    mRefreshLayout.setRefreshing(false);
                }
            } else {
                BaseListActivity.this.onResponse(request, json, null);
                BaseListActivity.this.onComplete(request, 0);
            }
        });
    }

    protected final void failed(final Request request, int code, final String message) {
        runOnUiThread(() -> {
            String url = getUrl();
            if (url != null && url.equals(request.getUrl())) {
                if (mRefreshLayout != null) {
                    mRefreshLayout.setRefreshing(false);
                }
                mEmptyView.setMessage(message);
                if (request.getLoadType() == PULL_UP) {
                    mPageNum--;
                    mAdapter.loadMoreFail();
                }
            }
            BaseListActivity.this.onError(request, code, message);
            BaseListActivity.this.onComplete(request, -1);
        });
    }

    @Override
    public final void onResponse(Request request, CharSequence json, T data) {
        onResponse(request, json);
    }

    public void onResponse(Request request, CharSequence json) {
    }

    @Override
    public void onError(Request request, int code, String message) {
        showShort(message);
    }

    @Override
    public void onComplete(Request request, int status) {
        hideDialog();
    }

    private ArrayList<T> parseArray(String string) {
        ArrayList<T> list = new ArrayList<>();
        string = getDataParser().findList(string);
        if (!TextUtils.isEmpty(string)) {
            JSONArray array;
            try {
                array = new JSONArray(string);
                for (int i = 0; i < array.length(); i++) {
                    if (mEntityType == String.class) {
                        list.add((T) array.getString(i));
                    } else {
                        list.add((T) new Gson().fromJson(array.getString(i), mEntityType));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public abstract String getUrl();

    public DataParser getDataParser() {
        return new DataParser() {
            @Override
            public String findList(String json) {
                return json;
            }
        };
    }

    public View getHeadView() {
        return null;
    }

    @SuppressLint("InflateParams")
    public View getEmptyView() {
        return LayoutInflater.from(this).inflate(R.layout.rapid_layout_base_empty_view, null);
    }

    public int getItemView() {
        return R.layout.rapid_item_base_list;
    }

    public boolean getEnableLoadMore() {
        return true;
    }

    public boolean getEnableDrag() {
        return false;
    }

    public abstract void convert(BaseViewHolder helper, T item);

    protected class InnerAdapter extends BaseQuickAdapter<T, BaseViewHolder> {

        InnerAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, T item) {
            BaseListActivity.this.convert(helper, item);
        }

    }

    protected class DragAdapter extends BaseItemDraggableAdapter<T, BaseViewHolder> {

        DragAdapter(int layoutResId) {
            super(layoutResId, null);
        }

        @Override
        protected void convert(BaseViewHolder helper, T item) {
            BaseListActivity.this.convert(helper, item);
        }
    }

    protected interface ListFinder {
        String find(String json);
    }

}
