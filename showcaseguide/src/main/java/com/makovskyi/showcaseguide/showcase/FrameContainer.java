package com.makovskyi.showcaseguide.showcase;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;



/**
 * Created by Denis Makovskyi
 */
public class FrameContainer extends FrameLayout{

    private Activity mActivity;
    private Overlay mOverlay;
    private Bitmap mEraserBitmap;
    private Canvas mEraserCanvas;
    private Paint mEraser;
    private TextPaint mTextPaint;

    public FrameContainer(Activity context, Overlay overlay){
        super(context);
        mActivity = context;
        mOverlay = overlay;
        init(null, 0);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if(mOverlay != null){
            if(mOverlay.getStyle() == Overlay.Style.Rectangle || mOverlay.getStyle() == Overlay.Style.RoundedRectangle){

                //Якщо тач через шар заборонений і точка натискання не попадає в квадрат
                if(!mOverlay.getClickThroughOverlay() && !isTouchOnRectangle(ev)){
                    //Блокувати тач
                    return true;
                    //Інакше якщо тач через шар не заборонений і точка натискання не попадає в квадрат
                }else if(mOverlay.getClickThroughOverlay() && !isTouchOnRectangle(ev)){
                    //Дозволити тач
                    return false;
                }

                if(!mOverlay.getClickThroughHole() && isTouchOnRectangle(ev)){
                    return true;
                }else if(mOverlay.getClickThroughHole() && isTouchOnRectangle(ev)){
                    return false;
                }
            }

            if(mOverlay.getStyle() == Overlay.Style.Circle){

                //Якщо тач через шар заборонений і точка натискання не попадає в коло
                if(!mOverlay.getClickThroughOverlay() && !isTouchOnCircle(ev)){
                    //Блокувати тач
                    return true;
                    //Інакше якщо тач через шар не заборонений і точка натискання не попадає в коло
                }else if(mOverlay.getClickThroughOverlay() && !isTouchOnCircle(ev)) {
                    //Дозволити тач
                    return false;
                }

                if(!mOverlay.getClickThroughHole() && isTouchOnCircle(ev)){
                    return true;
                }else if(mOverlay.getClickThroughHole() && isTouchOnCircle(ev)){
                    return false;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isTouchOnRectangle(MotionEvent event){
        int touchX = (int)event.getRawX();
        int touchY = (int)event.getRawY();

        return touchX >= mOverlay.getStartX() &&
                touchX <= mOverlay.getStartX() + (mOverlay.getEndX() - mOverlay.getStartX()) &&
                touchY >= mOverlay.getStartY() &&
                touchY <= mOverlay.getStartY() + (mOverlay.getEndY() - mOverlay.getStartY());
    }

    private boolean isTouchOnCircle(MotionEvent event){
        int touchX = (int)event.getRawX();
        int touchY = (int)event.getRawY();

        return Math.pow( (touchX - mOverlay.getStartX()), 2) + Math.pow( (touchY - mOverlay.getStartY()), 2) <= Math.pow(mOverlay.getRadius(), 2);
    }

    private void init(AttributeSet attrs, int defStyle){
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        setWillNotDraw(false);
        Point size = new Point();
        size.x = mActivity.getResources().getDisplayMetrics().widthPixels;
        size.y = mActivity.getResources().getDisplayMetrics().heightPixels;
        mEraserBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        mEraserCanvas = new Canvas(mEraserBitmap);
        mEraser = new Paint();
        mEraser.setColor(0xFFFFFFFF);
        mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mEraser.setFlags(Paint.ANTI_ALIAS_FLAG);
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        mEraserBitmap.eraseColor(Color.TRANSPARENT);

        if(mOverlay != null){
            mEraserCanvas.drawColor(mOverlay.getBackGroundColor());

            if(mOverlay.getStyle() == Overlay.Style.Circle){
                mEraserCanvas.drawCircle(mOverlay.getStartX(), mOverlay.getStartY(), mOverlay.getRadius(), mEraser);
            }

            if(mOverlay.getStyle() == Overlay.Style.Rectangle){
                mEraserCanvas.drawRect(mOverlay.getStartX(), mOverlay.getStartY(), mOverlay.getEndX(), mOverlay.getEndY(), mEraser);
            }

            if(mOverlay.getStyle() == Overlay.Style.RoundedRectangle){
                mEraserCanvas.drawRoundRect(new RectF(mOverlay.getStartX(), mOverlay.getStartY(), mOverlay.getEndX(), mOverlay.getEndY()), 25, 25, mEraser);
            }
        }

        canvas.drawBitmap(mEraserBitmap, 0, 0, null);
    }

    public void cleanUp(){
        if(getParent() != null){
            if(mOverlay != null){
                ((ViewGroup)this.getParent()).removeView(this);
            }
        }
    }
}

