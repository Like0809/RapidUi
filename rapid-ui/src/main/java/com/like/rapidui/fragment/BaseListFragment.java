package com.like.rapidui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.like.rapidui.PagingParam;
import com.like.rapidui.R;
import com.like.rapidui.RapidUi;
import com.like.rapidui.Request;
import com.like.rapidui.RequestListener;
import com.like.rapidui.RequestStatus;
import com.like.rapidui.activity.EmptyView;
import com.like.rapidui.callback.DataLoader;
import com.like.rapidui.network.ApiMessage;
import com.like.rapidui.network.OkHttpUtils;
import com.like.rapidui.network.callback.Callback;
import com.like.rapidui.network.callback.StringCallback;


import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.like.rapidui.RequestListener.FAILED;
import static com.like.rapidui.RequestListener.SUCCESS;


/**
 * Created By Like on 2018/3/23.
 */

@SuppressWarnings("unchecked")
public abstract class BaseListFragment<T> extends BaseFragment {

    protected SwipeRefreshLayout mRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected InnerAdapter mAdapter;
    protected final int PULL_UP = 1, PULL_DOWN = 2, INIT = 0;
    protected int mPageNum = 1, mPageSize = 10;
    protected EmptyView mEmptyView;
    protected View mRootView;
    protected Type mEntityType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView != null)
            return mRootView;
        mRootView = inflater.inflate(getContentView(), null, false);
        mEntityType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        mRecyclerView = mRootView.findViewById(R.id.recyclerView);
        mAdapter = new InnerAdapter(getItemView());
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
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPageNum++;
                refresh(PULL_UP);
            }
        }, mRecyclerView);
        mAdapter.setEnableLoadMore(getEnableLoadMore());
        mRefreshLayout = mRootView.findViewById(R.id.swipeRefresh);
        if (mRefreshLayout != null)
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mPageNum = 1;
                    refresh(PULL_DOWN);
                }
            });
        return mRootView;
    }

    public void refresh() {
        refresh(INIT);
    }

    public void refresh(int type) {
        loadData(getUrl(), getParams(), getHeaders(), type);
    }

    class Cbk implements RequestListener {
        @Override
        public void onResponse(Request request, String json) {
            int type = request.getLoadType();
            if (type != PULL_UP) mAdapter.replaceData(new ArrayList<T>());
            ArrayList<T> list = parseArray(json);
            if (list.size() < mPageSize) {
                if (type == PULL_DOWN && mRefreshLayout != null)
                    mRefreshLayout.setRefreshing(false);
                mAdapter.loadMoreEnd();
                //定制EmptyView内容;
            } else {
                if (type == PULL_DOWN && mRefreshLayout != null)
                    mRefreshLayout.setRefreshing(false);
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
        }

        @Override
        public void onError(RequestStatus status, Request request, int code, String message) {
            showShort(message);
            mEmptyView.setMessage(message);
            if (request.getLoadType() == PULL_UP)
                mAdapter.loadMoreFail();
            if (status == RequestStatus.API_ERR) {
                onApiError(code, message);
            }
        }

        @Override
        public void onComplete(Request request, int status) {
            if (mRefreshLayout != null)
                mRefreshLayout.setRefreshing(false);
        }
    }

    private ArrayList<T> parseArray(String string) {
        ArrayList<T> list = new ArrayList<>();
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
                throw new IllegalArgumentException("返回的数据格式错误");
            }
        }
        return list;
    }

    private void loadData(String url, Map<String, String> params, Map<String, String> headers, int loadType) {
        PagingParam pagingParam = getPagingParam();
        if (pagingParam == null) {
            pagingParam = RapidUi.getInstance().getPagingParam();
        }
        params.put(pagingParam.getPageSizeParam(), "" + mPageSize);
        params.put(pagingParam.getPageNumParam(), "" + mPageNum);
        loadData(url, params, headers, new Cbk(), loadType);
    }

    protected void loadData(String url, Map<String, String> params, Map<String, String> headers, RequestListener requestListener, final int loadType) {
        Request request = new Request(url, params, headers, loadType);
        DataLoader dataLoader = getDataLoader();
        if (dataLoader == null)
            dataLoader = RapidUi.getInstance().getDataLoader();
        dataLoader.load(request, requestListener);
    }

    public int getContentView() {
        return R.layout.rapid_fragment_base_list;
    }


    public DataLoader getDataLoader() {
        return null;
    }

    public abstract String getUrl();

    public void onApiError(int code, String message) {
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

}
