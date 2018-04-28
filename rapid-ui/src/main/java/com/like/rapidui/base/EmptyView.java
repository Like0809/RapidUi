package com.like.rapidui.base;

import android.view.View;

/**
 * Created By Like on 2018/4/5.
 */

public abstract class EmptyView {

    private View mView;

    public EmptyView(View view) {
        mView = view;
    }

    public View getView() {
        return mView;
    }

    public abstract void setMessage(String message);

    public abstract void setIcon(int resId);

}
