package com.like.rapidui.ui.icon.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.like.rapidui.R;


/**
 * Created By Like on 2017/5/27.
 */

@SuppressWarnings("unused")
public class IconFontWrapLayout extends LinearLayout {
    private IconTextView mIconView, mTitleView, mRightView, mArrow;

    private String icon;
    private String iconSelected;
    private String iconPressed;
    private ColorStateList iconColor;
    private int iconSize;

    private String title;
    private String titleSelected;
    private String titlePressed;
    private int titleMaxLines;
    private ColorStateList titleColor;
    private int titleSize;

    private int rightTextSize;
    private String rightText;
    private ColorStateList rightTextColor;

    private int spacingHorizontal;
    private int spacingVertical;

    private boolean hasArrow;
    private boolean isInit = false;

    public IconFontWrapLayout(Context context) {
        this(context, null);
    }

    public IconFontWrapLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconFontWrapLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconFontWrapLayout);

        float scale = context.getResources().getDisplayMetrics().densityDpi / 160f;

        icon = ta.getString(R.styleable.IconFontWrapLayout_ifw_icon);
        if (icon == null) icon = "";
        iconSelected = ta.getString(R.styleable.IconFontWrapLayout_ifw_icon_selected);
        iconPressed = ta.getString(R.styleable.IconFontWrapLayout_ifw_icon_pressed);
        iconColor = ta.getColorStateList(R.styleable.IconFontWrapLayout_ifw_icon_color);
        iconSize = ta.getDimensionPixelSize(R.styleable.IconFontWrapLayout_ifw_icon_size, (int) (15 * scale + 0.5f));

        mIconView = new IconTextView(context);
        mIconView.setGravity(Gravity.CENTER);
        mIconView.setTextSize(TypedValue.COMPLEX_UNIT_PX, iconSize);
        mIconView.setMaxLines(1);
        LayoutParams lp1 = new LayoutParams(iconSize + 10, iconSize + 10);
        mIconView.setLayoutParams(lp1);
        addView(mIconView);

        title = ta.getString(R.styleable.IconFontWrapLayout_ifw_title);
        if (title == null) title = "";
        titleSelected = ta.getString(R.styleable.IconFontWrapLayout_ifw_title_selected);
        titlePressed = ta.getString(R.styleable.IconFontWrapLayout_ifw_title_pressed);
        titleColor = ta.getColorStateList(R.styleable.IconFontWrapLayout_ifw_title_color);
        titleSize = ta.getDimensionPixelSize(R.styleable.IconFontWrapLayout_ifw_title_size, (int) (15 * scale + 0.5f));
        titleMaxLines = ta.getInt(R.styleable.IconFontWrapLayout_ifw_title_max_lines, 1);

        mTitleView = new IconTextView(context);
        mTitleView.setGravity(Gravity.CENTER | Gravity.LEFT);
        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        mTitleView.setMaxLines(titleMaxLines);
        mTitleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        addView(mTitleView);

        rightText = ta.getString(R.styleable.IconFontWrapLayout_ifw_right_text);
        rightTextSize = ta.getDimensionPixelSize(R.styleable.IconFontWrapLayout_ifw_right_text_size, (int) (15 * scale + 0.5f));
        rightTextColor = ta.getColorStateList(R.styleable.IconFontWrapLayout_ifw_right_text_color);

        mRightView = new IconTextView(getContext());
        mRightView.setGravity(Gravity.CENTER);
        mRightView.setMaxWidth((int) (200 * scale + 0.5f));
        mRightView.setMaxLines(1);
        mRightView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        addView(mRightView);

        hasArrow = ta.getBoolean(R.styleable.IconFontWrapLayout_ifw_arrow, false);
        mArrow = new IconTextView(getContext());
        mArrow.setNormalText("{fa-chevron-right}").setColor(ColorStateList.valueOf(Color.parseColor("#dddddd")));
        mArrow.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (15 * scale + 0.5f));
        addView(mArrow);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = (int) (4 * scale + 0.5f);
        mArrow.setLayoutParams(lp);

        spacingHorizontal = ta.getDimensionPixelOffset(R.styleable.IconFontWrapLayout_ifw_spacing_horizontal, (int) (16 * scale + 0.5f));
        spacingVertical = ta.getDimensionPixelOffset(R.styleable.IconFontWrapLayout_ifw_spacing_vertical, (int) (3 * scale + 0.5f));
        ta.recycle();

        isInit = true;
        updateView();
        onOrientationChanged();
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
        if (isInit)
            onOrientationChanged();
    }

    private void onOrientationChanged() {
        if (getOrientation() == HORIZONTAL) {
            LayoutParams lp = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = spacingHorizontal;
            lp.weight = 1;
            mTitleView.setLayoutParams(lp);
            if (mRightView != null && !TextUtils.isEmpty(rightText))
                mRightView.setVisibility(VISIBLE);
            if (hasArrow)
                mArrow.setVisibility(VISIBLE);
            else mArrow.setVisibility(GONE);
        } else if (getOrientation() == VERTICAL) {
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = spacingVertical;
            mTitleView.setLayoutParams(lp);
            mRightView.setVisibility(GONE);
            mArrow.setVisibility(GONE);
        }
    }

    private void updateView() {
        mIconView.setNormalText(icon).setSelectedText(iconSelected == null ? icon : iconSelected).setPressedText(iconPressed == null ? icon : iconPressed).setColor(iconColor);
        mTitleView.setNormalText(title).setSelectedText(titleSelected == null ? title : titleSelected).setPressedText(titlePressed == null ? title : titlePressed).setColor(titleColor);
        mRightView.setNormalText(rightText).setColor(rightTextColor);

        mRightView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize);
    }

    public IconTextView getArrow() {
        return mArrow;
    }

    public IconTextView getRightView() {
        return mRightView;
    }

    public IconTextView getIconView() {
        return mIconView;
    }

    public IconFontWrapLayout setRightText(String rightText) {
        this.rightText = rightText;
        return this;
    }

    public IconFontWrapLayout setRightTextColor(ColorStateList color) {
        this.rightTextColor = color;
        return this;
    }

    public IconFontWrapLayout setRightTextSize(int size) {
        this.rightTextSize = size;
        return this;
    }

//    public IconFontWrapLayout setIconView(IconFontView mIconView) {
//        this.mIconView = mIconView;
//        return this;
//    }

    public String getIcon() {
        return icon;
    }

    public IconFontWrapLayout setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public String getIconSelected() {
        return iconSelected;
    }

    public IconFontWrapLayout setIconSelected(String iconSelected) {
        this.iconSelected = iconSelected;
        return this;
    }

    public ColorStateList getIconColor() {
        return iconColor;
    }

    public IconFontWrapLayout setIconColor(ColorStateList iconColor) {
        this.iconColor = iconColor;
        return this;
    }

    public int getIconSize() {
        return iconSize;
    }

    public IconFontWrapLayout setIconSize(int iconSize) {
        this.iconSize = iconSize;
        LayoutParams lp1 = new LayoutParams(iconSize + 10, iconSize + 10);
        mIconView.setLayoutParams(lp1);
        return this;
    }

    public String getTitle() {
        return title;
    }

    public IconFontWrapLayout setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTitleSelected() {
        return titleSelected;
    }

    public IconFontWrapLayout setTitleSelected(String titleSelected) {
        this.titleSelected = titleSelected;
        return this;
    }

    public ColorStateList getTitleColor() {
        return titleColor;
    }

    public IconFontWrapLayout setTitleColor(ColorStateList titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public int getTitleSize() {
        return titleSize;
    }

    public IconFontWrapLayout setTitleSize(int titleSize) {
        this.titleSize = titleSize;
        return this;
    }

    public int getSpacingHorizontal() {
        return spacingHorizontal;
    }

    public IconFontWrapLayout setSpacingHorizontal(int spacingHorizontal) {
        this.spacingHorizontal = spacingHorizontal;
        return this;
    }

    public int getSpacingVertical() {
        return spacingVertical;
    }

    public IconFontWrapLayout setSpacingVertical(int spacingVertical) {
        this.spacingVertical = spacingVertical;
        return this;
    }

    public void commit() {
        updateView();
    }
}
