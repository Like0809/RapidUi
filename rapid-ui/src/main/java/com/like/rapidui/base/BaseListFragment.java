package com.like.rapidui.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.like.rapidui.R;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * Created By Like on 2018/3/23.
 */

@SuppressWarnings("ALL")
public abstract class BaseListFragment<T> extends BaseFragment<T> {

    protected SwipeRefreshLayout mRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected BaseQuickAdapter<T, BaseViewHolder> mAdapter;
    protected EmptyView mEmptyView;

    public int getContentView() {
        return R.layout.rapid_fragment_base_list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView != null)
            return mRootView;
        mRootView = super.onCreateView(inflater, container, savedInstanceState);
        assert mRootView != null;
        mRecyclerView = mRootView.findViewById(R.id.recyclerView);
        if (getEnableDrag()) {
            mAdapter = new DragAdapter(getItemView());
        } else {
            mAdapter = new InnerAdapter(getItemView());
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        if (getEnableLoadMore()) {
            mAdapter.setOnLoadMoreListener(() -> {
                mPageNum++;
                load(LoadType.PULL_UP);
            }, mRecyclerView);
        }
        mAdapter.setEnableLoadMore(getEnableLoadMore());
        mRefreshLayout = mRootView.findViewById(R.id.swipeRefresh);
        if (mRefreshLayout != null)
            mRefreshLayout.setOnRefreshListener(() -> {
                mPageNum = 1;
                load(LoadType.PULL_DOWN);
            });
        return mRootView;
    }

    protected final void success(final Request request, final String json) {
        Activity activity = getActivity();
        if (!activity.isFinishing() && !activity.isDestroyed()) {
            activity.runOnUiThread(() -> {
                String url = getUrl();
                if ((url != null && url.equals(request.getUrl())) || url == null) {
                    LoadType type = request.getLoadType();
                    if (type != LoadType.PULL_UP) mAdapter.replaceData(new ArrayList<>());
                    ArrayList<T> list = parseArray(json);
                    if (list.size() < mPageSize) {
                        mAdapter.loadMoreEnd();
                        //定制EmptyView内容;
                    } else {
                        if (type == LoadType.PULL_UP) {
                            mAdapter.loadMoreComplete();
                        }
                    }
                    if (type == LoadType.PULL_DOWN) {
                        if (getEnableLoadMore())
                            mAdapter.setNewData(list);
                        else mAdapter.addData(list);
                    } else {
                        mAdapter.addData(list);
                    }
                    if (mRefreshLayout != null) {
                        mRefreshLayout.setRefreshing(false);
                    }
                }
                BaseListFragment.this.onResponse(request, json, null);
                BaseListFragment.this.onComplete(request, 0);
            });
        }
    }

    protected final void failed(final Request request, int code, final String message) {
        Activity activity = getActivity();
        if (!activity.isFinishing() && !activity.isDestroyed()) {
            activity.runOnUiThread(() -> {
                String url = getUrl();
                if (url != null && url.equals(request.getUrl())) {
                    mEmptyView.setMessage(message);
                    if (request.getLoadType() == LoadType.PULL_UP) {
                        mPageNum--;
                        mAdapter.loadMoreFail();
                    }
                    if (mRefreshLayout != null) {
                        mRefreshLayout.setRefreshing(false);
                    }
                }
                BaseListFragment.this.onError(request, code, message);
                BaseListFragment.this.onComplete(request, -1);
            });
        }
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
                        list.add(new Gson().fromJson(array.getString(i), mEntityType));
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
        return json -> json;
    }

    public View getHeadView() {
        return null;
    }

    @SuppressLint("InflateParams")
    public View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.rapid_layout_base_empty_view, null);
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
            BaseListFragment.this.convert(helper, item);
        }
    }

    protected class DragAdapter extends BaseItemDraggableAdapter<T, BaseViewHolder> {

        DragAdapter(int layoutResId) {
            super(layoutResId, null);
        }

        @Override
        protected void convert(BaseViewHolder helper, T item) {
            BaseListFragment.this.convert(helper, item);
        }
    }
}
