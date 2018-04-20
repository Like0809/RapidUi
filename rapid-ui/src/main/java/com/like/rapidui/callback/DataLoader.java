package com.like.rapidui.callback;

import com.like.rapidui.Request;
import com.like.rapidui.RequestListener;

/**
 * Created By Like on 2018/4/18.
 */

public abstract class DataLoader {

    public abstract void load(Request request, RequestListener requestListener);

}
