package com.like.rapidui.ui.icon.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.Checkable;

import com.like.rapidui.R;
import com.like.rapidui.ui.icon.Iconify;
import com.like.rapidui.ui.icon.internal.HasOnViewAttachListener;

public class IconTextView extends AppCompatTextView implements HasOnViewAttachListener, Checkable {

    private HasOnViewAttachListenerDelegate delegate;
    protected boolean mChecked;
    private int states[];
    private CharSequence mNormalText, mSelectedText, mPressedText;
    private ColorStateList mColor;

    public IconTextView(Context context) {
        this(context, null);
    }

    public IconTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTransformationMethod(null);
        mChecked = false;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconTextView);
        mNormalText = ta.getString(R.styleable.IconTextView_itv_icon);
        if (mNormalText == null)
            mNormalText = "";
        mSelectedText = ta.getString(R.styleable.IconTextView_itv_icon_selected);
        mPressedText = ta.getString(R.styleable.IconTextView_itv_icon_pressed);
        mColor = ta.getColorStateList(R.styleable.IconTextView_itv_color);
        if (mColor == null)
            mColor = ColorStateList.valueOf(Color.GRAY);
        setText(mNormalText);
        ta.recycle();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        states = getDrawableState();
        if (mColor != null)
            setTextColor(mColor.getColorForState(states, Color.GRAY));
        if (containsState(android.R.attr.state_pressed)) {
            setText(mPressedText == null ? mNormalText : mPressedText);
            return;
        }
        if (containsState(android.R.attr.state_selected)) {
            setText(mSelectedText == null ? mNormalText : mSelectedText);
            return;
        }
        setText(mNormalText);
    }

    private boolean containsState(int state) {
        for (int s : states) {
            if (s == state) return true;
        }
        return false;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(Iconify.compute(getContext(), text, this), type);
    }

    @Override
    public void setOnViewAttachListener(OnViewAttachListener listener) {
        if (delegate == null) delegate = new HasOnViewAttachListenerDelegate(this);
        delegate.setOnViewAttachListener(listener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        delegate.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        delegate.onDetachedFromWindow();
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    public CharSequence getNormalText() {
        return mNormalText;
    }

    public IconTextView setNormalText(CharSequence mNormalText) {
        this.mNormalText = mNormalText;
        drawableStateChanged();
        return this;
    }

    public CharSequence getSelectedText() {
        return mSelectedText;
    }

    public IconTextView setSelectedText(CharSequence mSelectedText) {
        this.mSelectedText = mSelectedText;
        drawableStateChanged();
        return this;
    }

    public CharSequence getPressedText() {
        return mPressedText;
    }

    public IconTextView setPressedText(CharSequence mPressedText) {
        this.mPressedText = mPressedText;
        drawableStateChanged();
        return this;
    }

    public ColorStateList getColor() {
        return mColor;
    }

    public IconTextView setColor(ColorStateList mColor) {
        this.mColor = mColor;
        drawableStateChanged();
        return this;
    }
}
