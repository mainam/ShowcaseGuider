package com.makovskyi.showcaseguide.showcase;

import android.view.View;

/**
 * Created by Denis Makovskyi
 */
public class ToolTip {

    private String mTitle;
    private String mDescription;
    private int mTextColor;
    private int mBackgroundColor;
    private int mGravity;
    private ToolTipPosition mToolTipPosition;

    private View.OnClickListener mOnClickListener;

    public ToolTip(){
        mBackgroundColor = 0xffffff;
        mTextColor = 0x000000;
    }

    public String getTitle() {
        return mTitle;
    }

    public ToolTip setTitle(String mTitle) {
        this.mTitle = mTitle;
        return this;
    }

    public String getDescription() {
        return mDescription;
    }

    public ToolTip setDescription(String mDescription) {
        this.mDescription = mDescription;
        return this;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public ToolTip setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        return this;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public ToolTip setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
        return this;
    }

    public int getGravity() {
        return mGravity;
    }

    public ToolTip setGravity(int gravity) {
        this.mGravity = gravity;
        return this;
    }

    public ToolTipPosition getToolTipPosition() {
        return mToolTipPosition;
    }

    public ToolTip setToolTipPosition(ToolTipPosition mToolTipPosition) {
        this.mToolTipPosition = mToolTipPosition;
        return this;
    }

    public View.OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    public ToolTip setOnClickListener(View.OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        return this;
    }
}
