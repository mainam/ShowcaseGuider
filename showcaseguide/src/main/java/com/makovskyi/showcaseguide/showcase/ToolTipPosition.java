package com.makovskyi.showcaseguide.showcase;

import android.app.Activity;
import android.view.Gravity;


/**
 * Created by Denis Makovskyi
 */
public class ToolTipPosition {

    private Activity mActivity;
    private int mScreenWidth;
    private int mScreenHeight;

    private int mMarginTop;
    private int mMarginLeft;
    private int mMarginRight;
    private int mMarginBottom;
    private int mGravity;
    private int mWidth;
    private int mHeight;
    private boolean mMarginsAdjustment = false;

    public ToolTipPosition(Activity activity) {
        this.mActivity = activity;
        this.mMarginTop = 0;
        this.mMarginLeft = 0;
        this.mMarginRight = 0;
        this.mMarginBottom = 0;
        this.mGravity = Gravity.NO_GRAVITY;
        this.mWidth = 0;
        this.mHeight = 0;
        mScreenWidth = mActivity.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = mActivity.getResources().getDisplayMetrics().heightPixels;
    }

    public ToolTipPosition setMarginsPx(int left, int top, int right, int bottom) {
        this.mMarginLeft = left;
        this.mMarginTop = top;
        this.mMarginRight = right;
        this.mMarginBottom = bottom;
        return this;
    }

    public ToolTipPosition setMarginsPercent(int left, int top, int right, int bottom) {
        this.mMarginLeft = (mScreenWidth * left) / 100;
        this.mMarginTop = (mScreenHeight * top) / 100;
        this.mMarginRight = (mScreenWidth * right) / 100;
        this.mMarginBottom = (mScreenHeight * bottom) / 100;
        return this;
    }

    public ToolTipPosition setGravity(int mGravity) {
        this.mGravity = mGravity;
        return this;
    }

    public ToolTipPosition setWidthPx(int width) {
        this.mWidth = width;
        return this;
    }

    public ToolTipPosition setWidthPercent(int width) {
        if(mMarginsAdjustment){
            this.mWidth = ( (mScreenWidth - mMarginLeft) * width) / 100;
        }else{
            this.mWidth = (mScreenWidth * width) / 100;
        }
        return this;
    }

    public ToolTipPosition setHeightPx(int height) {
        this.mHeight = height;
        return this;
    }

    public ToolTipPosition setHeightPercent(int height) {
        if(mMarginsAdjustment){
            this.mHeight = ( (mScreenHeight - mMarginTop) * height) / 100;
        }else{
            this.mHeight = (mScreenHeight * height) / 100;
        }
        return this;
    }

    /**
     * Якщо прапорець встановлений в Істина - при виклику сеттерів, setWidthPercent(int width) та setHeightPercent(int height),<br/>
     * ширина та висота TollTip буде вираховуватись з урахуванням відступу з гори та зліва.<br/>
     * Призначено дляя того, щоб ToolTip не вийшов за межі екрану.<br/>
     * <b>Тому перш ніж встановлювати висоту та ширину, спочатку необхідно встановити відступи.</b><br/>
     * <i>Не працює з сеттерами setWidthPx(int width) та setHeightPx(int height)</i>
     *
     * @param adjustment - прапорець урахування відступів для ширини та висоти ToolTip
     */
    public ToolTipPosition setMarginsAdjustment(boolean adjustment) {
        this.mMarginsAdjustment = adjustment;
        return this;
    }

    public int getMarginTop() {
        return mMarginTop;
    }

    public int getMarginLeft() {
        return mMarginLeft;
    }

    public int getMarginRight() {
        return mMarginRight;
    }

    public int getMarginBottom() {
        return mMarginBottom;
    }

    public int getGravity() {
        return mGravity;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }
}
