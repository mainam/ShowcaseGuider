package com.makovskyi.showcaseguide.showcase;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.makovskyi.showcaseguide.R;

/**
 * Created by Denis Makovskyi
 */
public class ShowcaseView {

    private Activity mActivity;
    private FrameContainer mFrameContainer;
    private Overlay mOverlay;
    private ToolTip mToolTip;
    private View mToolTipView;

    public static ShowcaseView initialize(Activity activity) {
        return new ShowcaseView(activity);
    }

    public ShowcaseView(Activity activity) {
        mActivity = activity;
    }

    public ShowcaseView setOverlay(Overlay overlay) {
        mOverlay = overlay;
        return this;
    }

    public ShowcaseView setToolTip(ToolTip toolTip) {
        mToolTip = toolTip;
        return this;
    }

    public void cleanShowcase() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFrameContainer.cleanUp();
                if (mToolTipView != null) {
                    ((ViewGroup) mActivity.getWindow().getDecorView()).removeView(mToolTipView);
                }
            }
        });
    }

    public ShowcaseView attachView() {

        if (mOverlay == null) {
            throw new IllegalArgumentException("Can not invoke setup method if overlay is null");
        }

        if (mOverlay.getTarget() != null) {
            if (ViewCompat.isAttachedToWindow(mOverlay.getTarget())) {
                setupView();
            } else {
                final ViewTreeObserver viewTreeObserver = mOverlay.getTarget().getViewTreeObserver();
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mOverlay.getTarget().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mOverlay.getTarget().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        setupView();
                    }
                });
            }
        } else {
            setupView();
        }

        return this;
    }

    private void setupView() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mOverlay.calculateCoordinates();
                mFrameContainer = new FrameContainer(mActivity, mOverlay);
                setupFrameContainer();
                setupToolTip();
            }
        });
    }

    private void setupFrameContainer() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        ViewGroup contentArea = (ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        int[] pos = new int[2];
        contentArea.getLocationOnScreen(pos);
        layoutParams.setMargins(0, -pos[1], 0, 0);
        contentArea.addView(mFrameContainer, layoutParams);
    }

    private void setupToolTip() {
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        if (mToolTip != null) {
            final ViewGroup parent = (ViewGroup) mActivity.getWindow().getDecorView();
            int displayWidth = mActivity.getResources().getDisplayMetrics().widthPixels;
            final LayoutInflater layoutInflater = mActivity.getLayoutInflater();

            mToolTipView = layoutInflater.inflate(R.layout.showcase_tooltip, null);
            View tooltipRootContainer = mToolTipView.findViewById(R.id.tooltipRootContainer);
            TextView tooltipTitleTextView = (TextView) mToolTipView.findViewById(R.id.tooltipTitleTextView);
            TextView tooltipDescriptionTextView = (TextView) mToolTipView.findViewById(R.id.tooltipDescriptionTextView);

            tooltipRootContainer.setBackgroundColor(mToolTip.getBackgroundColor());
            tooltipTitleTextView.setTextColor(mToolTip.getTextColor());
            tooltipDescriptionTextView.setTextColor(mToolTip.getTextColor());

            if (mToolTip.getTitle() == null || mToolTip.getTitle().isEmpty()) {
                tooltipTitleTextView.setVisibility(View.GONE);
            } else {
                tooltipTitleTextView.setVisibility(View.VISIBLE);
                tooltipTitleTextView.setText(mToolTip.getTitle());
            }

            if (mToolTip.getDescription() == null || mToolTip.getDescription().isEmpty()) {
                tooltipDescriptionTextView.setVisibility(View.GONE);
            } else {
                tooltipDescriptionTextView.setVisibility(View.VISIBLE);
                tooltipDescriptionTextView.setText(mToolTip.getDescription());
            }


            //Якщо позиція ToolTip не вказується вручну
            if (mToolTip.getToolTipPosition() == null) {

                //Початкові ккординати області, яка буде знаходитись в отворі
                int targetViewX;
                final int targetViewY;
                //Ширина та висота області, яка буде знаходитись в отворі
                int targetViewWidth = 0;
                int targetViewHeight = 0;

                //Зміщення
                final float adjustment = mOverlay.getPadding();

                //Якщо цільова View не null
                if (mOverlay.getTarget() != null) {
                    //Отримання координат початкової точки ціьової View
                    int[] pos = new int[2];
                    mOverlay.getTarget().getLocationOnScreen(pos);
                    targetViewX = pos[0];
                    targetViewY = pos[1];
                    //Отримання висоти та ширини цільової View
                    targetViewWidth = mOverlay.getTarget().getWidth();
                    targetViewHeight = mOverlay.getTarget().getHeight();
                    //Інакше, якщо цільова View не задана, а область буде промальовуватись за допомогою кастомних координат
                    //Зауваження: сутність Overlay містить в собі координати ОТВОРУ, який буде обводити задану ОБЛАСТЬ.
                    //Координати ОТВОРУ вираховуються на основі координат ОБЛАСТІ, в методі calculateCoordinates(), отож для отримання координат ОБЛАСТІ,
                    //можна провести зворотні арифметичні операції.
                    //ЩЕ РАЗ, ЦЕ ВАЖЛИВО! Поля класу Overlay: mStartX, mStartY, mEndX, mEndY на вході отримують координати ОБЛАСТІ, і після обчислень координат ОТВОРУ,
                    //перезаписують вказані поля розрахованими координатами ОТВОРУ. Для отримання координат ОБЛАСТІ, якщо не була задана цільова View необхідно провести зворотні арифметичні операції.
                } else {
                    int tempTargetX = 0;
                    int tempTargetY = 0;
                    //Якщо стиль отвору Прямокутник або Заокруглений прямокутник
                    if (mOverlay.getStyle() == Overlay.Style.Rectangle || mOverlay.getStyle() == Overlay.Style.RoundedRectangle) {
                        //Отримання координат початкової точки області
                        tempTargetX = (int) mOverlay.getStartX() + mOverlay.getPadding();
                        tempTargetY = (int) mOverlay.getStartY() + mOverlay.getPadding();
                        //Отримання ширини та висоти області
                        targetViewWidth = (int) mOverlay.getWidth();
                        targetViewHeight = (int) mOverlay.getHeight();
                    }
                    //Якщо стиль отвору Коло
                    if (mOverlay.getStyle() == Overlay.Style.Circle) {
                        //Якщо область задавалась діагоналлю
                        if (mOverlay.getDiagonal() != Overlay.EMPTY_VALUE && mOverlay.getDiagonal() > Overlay.EMPTY_VALUE) {
                            //Отримання координат початкової точки області
                            tempTargetX = (int) mOverlay.getStartX() - mOverlay.getDiagonal() / 2;
                            tempTargetY = (int) mOverlay.getStartY() - mOverlay.getDiagonal() / 2;
                            //Отримання ширини та висоти області
                            targetViewWidth = (int) mOverlay.getWidth();
                            targetViewHeight = (int) mOverlay.getHeight();
                        }
                        //Якщо область задавалась координатами кінцевої точки
                        if (mOverlay.getDiagonal() == Overlay.EMPTY_VALUE || mOverlay.getDiagonal() < Overlay.EMPTY_VALUE) {
                            //Отримання координат початкової точки області
                            tempTargetX = (int) mOverlay.getStartX() - (int) mOverlay.getWidth() / 2;
                            tempTargetY = (int) mOverlay.getStartY() - (int) mOverlay.getHeight() / 2;
                            //Отримання ширини та висоти області
                            targetViewWidth = (int) mOverlay.getWidth();
                            targetViewHeight = (int) mOverlay.getHeight();
                        }
                    }
                    targetViewX = tempTargetX;
                    targetViewY = tempTargetY;
                }

                //Ширина та висота ToolTip View
                mToolTipView.measure(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                int toolTipMeasuredWidth = mToolTipView.getMeasuredWidth();
                int toolTipMeasuredHeight = mToolTipView.getMeasuredHeight();

                //х, у координати view з підказками
                //Координати вираховуються на основі гравітації, ширини, початкових координат цільової View та зміщення
                Point toolTipCoordinates = new Point();
                if (toolTipMeasuredWidth > displayWidth) {
                    toolTipCoordinates.x = getXForTooTip(mToolTip.getGravity(), displayWidth, targetViewX, targetViewWidth, adjustment);
                } else {
                    toolTipCoordinates.x = getXForTooTip(mToolTip.getGravity(), toolTipMeasuredWidth, targetViewX, targetViewWidth, adjustment);
                }
                toolTipCoordinates.y = getYForTooTip(mToolTip.getGravity(), toolTipMeasuredHeight, targetViewY, targetViewHeight, adjustment);

                //Додавання view з підказками на екран
                parent.addView(mToolTipView, layoutParams);

                //Якщо ширина ToolTip > ширина вікна
                if (toolTipMeasuredWidth > displayWidth) {
                    //Ширина ToolTip буде рівна ширині екрану
                    mToolTipView.getLayoutParams().width = displayWidth;
                    toolTipMeasuredWidth = displayWidth;
                }
                //Перевірка х-координати відносно лівої межі екрану
                if (toolTipCoordinates.x < 0) {
                    mToolTipView.getLayoutParams().width = toolTipMeasuredWidth + toolTipCoordinates.x;
                    toolTipCoordinates.x = 0;
                }
                //Перевірка х-координати відносно правої межі екрану
                int tempRightX = toolTipCoordinates.x + toolTipMeasuredWidth;
                if (tempRightX > displayWidth) {
                    mToolTipView.getLayoutParams().width = displayWidth - toolTipCoordinates.x;
                }

                final int fTargetViewHeight = targetViewHeight;
                mToolTipView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mToolTipView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mToolTipView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }

                        int fixedY;
                        int toolTipHeightAfterLayout = mToolTipView.getHeight();
                        fixedY = getYForTooTip(mToolTip.getGravity(), toolTipHeightAfterLayout, targetViewY, fTargetViewHeight, adjustment);
                        layoutParams.setMargins((int) mToolTipView.getX(), fixedY, 0, 0);
                        parent.requestLayout();
                    }
                });
                layoutParams.setMargins(toolTipCoordinates.x, toolTipCoordinates.y, 0, 0);
            } else {
                parent.addView(mToolTipView, layoutParams);
                mToolTipView.getLayoutParams().width = mToolTip.getToolTipPosition().getWidth();
                mToolTipView.getLayoutParams().height = mToolTip.getToolTipPosition().getHeight();
                layoutParams.setMargins(mToolTip.getToolTipPosition().getMarginLeft(), mToolTip.getToolTipPosition().getMarginTop(), mToolTip.getToolTipPosition().getMarginRight(), mToolTip.getToolTipPosition().getMarginBottom());
                parent.requestLayout();
            }
        }
    }

    private int getXForTooTip(int gravity, int toolTipMeasuredWidth, int targetViewX, int targetViewWidth, float adjustment) {
        int x;
        if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
            x = targetViewX - toolTipMeasuredWidth + (int) adjustment;
        } else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            x = targetViewX + targetViewWidth - (int) adjustment;
        } else {
            x = targetViewX + targetViewWidth / 2 - toolTipMeasuredWidth / 2;
        }
        return x;
    }

    private int getYForTooTip(int gravity, int toolTipMeasuredHeight, int targetViewY, int targetViewHeight, float adjustment) {
        int y;
        if ((gravity & Gravity.TOP) == Gravity.TOP) {

            if (((gravity & Gravity.LEFT) == Gravity.LEFT) || ((gravity & Gravity.RIGHT) == Gravity.RIGHT)) {
                y = targetViewY - toolTipMeasuredHeight + (int) adjustment;
            } else {
                y = targetViewY - toolTipMeasuredHeight - (int) adjustment;
            }
        } else {
            if (((gravity & Gravity.LEFT) == Gravity.LEFT) || ((gravity & Gravity.RIGHT) == Gravity.RIGHT)) {
                y = targetViewY + targetViewHeight - (int) adjustment;
            } else {
                y = targetViewY + targetViewHeight + (int) adjustment;
            }
        }
        return y;
    }
}
