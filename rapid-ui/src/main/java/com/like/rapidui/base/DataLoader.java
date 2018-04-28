package com.like.rapidui.base;

import com.like.rapidui.base.Request;
import com.like.rapidui.base.RequestListener;

/**
 * Created By Like on 2018/4/18.
 */

public abstract class DataLoader {

    public abstract void load(Request request, RequestListener requestListener);

}
