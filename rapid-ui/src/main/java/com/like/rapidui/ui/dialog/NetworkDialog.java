package com.like.rapidui.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.like.rapidui.R;
import com.like.rapidui.utils.ViewUtils;

/**
 * Created By Like on 2016/8/16.
 */
public class NetworkDialog extends Dialog {
    private TextView mTitle;

    public void setText(Object mText) {
        if (mText instanceof String) {
            mTitle.setText((String) mText);
        }
        if (mText instanceof Integer) {
            if ((int) mText > 0)
                mTitle.setText((Integer) mText);
        }
    }

    public NetworkDialog(Context context, Object text) {
        this(context, R.style.RapidBaseDialog, text);
    }

    private NetworkDialog(Context context, int themeResId, Object text) {
        super(context, themeResId);
        setCancelable(false);
        setContentView(R.layout.rapid_layout_dialog);
        mTitle = (TextView) findViewById(R.id.dialog_tv);
        setText(text);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = ViewUtils.getInstance(context).getWidth(0.8f);
            window.setGravity(Gravity.CENTER_VERTICAL);
        }
    }
}
