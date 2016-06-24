package com.makovskyi.showcaseguide.showcase;

import android.app.Activity;
import android.view.View;

/**
 * <b>Клас Overlay(Накладка)</b><br/>
 * Призначений для зберігання даних про шар, який буде накладатись на Activity в момент роботи ShowcaseView - помічника.<br/>
 * <ul>Клас Overlay приймає наступні дані:
 * <li>цільова активність до якої буде прикріплена накладка;</li>
 * <li>цільовий View елемент, який буде виділений в накладці (опціонально);</li>
 * <li>кастомні координати, що будуть інтерпритуватись як координати View елементу;</li>
 * <li>колір накладки;</li>
 * <li>фігура отвору в накладці (Коло, Прямокутник, Заокруглений прямокутник);</li>
 * <li>прапорці обробки натискань на екран в момент роботи помічника (Дозволяють блокувати/пропускати натискання через накладку та отвір.);</li></ul>
 *
 * @author Denis Makovskyi
 */

public class Overlay {

    private Activity mActivity;
    private View mTarget;
    private float mDensity;
    private int mPadding;
    public int mPaddingPercent;
    private int mBackGroundColor;
    private Style mStyle;
    private float mStartX = EMPTY_VALUE;
    private float mStartY = EMPTY_VALUE;
    private float mEndX = EMPTY_VALUE;
    private float mEndY = EMPTY_VALUE;
    private float mWidth;
    private float mHeight;
    private int mRadius = EMPTY_VALUE;
    private int mDiagonal = EMPTY_VALUE;
    private boolean mClickThroughHole;
    private boolean mClickThroughOverlay;
    public static final int EMPTY_VALUE = -1;

    public enum Style {
        Rectangle, Circle, RoundedRectangle
    }

    private Overlay(Builder overlayBuilder) {
        this.mActivity = overlayBuilder.mActivity;
        this.mTarget = overlayBuilder.mTarget;
        this.mDensity = overlayBuilder.mDensity;
        this.mPaddingPercent = overlayBuilder.mPaddingPercent;
        this.mBackGroundColor = overlayBuilder.mBackGroundColor;
        this.mStyle = overlayBuilder.mStyle;
        this.mStartX = overlayBuilder.mStartX;
        this.mStartY = overlayBuilder.mStartY;
        this.mEndX = overlayBuilder.mEndX;
        this.mEndY = overlayBuilder.mEndY;
        this.mDiagonal = overlayBuilder.mDiagonal;
        this.mClickThroughHole = overlayBuilder.mClickThroughHole;
        this.mClickThroughOverlay = overlayBuilder.mClickThroughOverlay;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public View getTarget() {
        return mTarget;
    }

    public float getDensity() {
        return mDensity;
    }

    public int getPadding() {
        return mPadding;
    }

    public int getPaddingPercent() {
        return mPaddingPercent;
    }

    public int getBackGroundColor() {
        return mBackGroundColor;
    }

    public Style getStyle() {
        return mStyle;
    }

    public float getStartX() {
        return mStartX;
    }

    public float getStartY() {
        return mStartY;
    }

    public float getEndX() {
        return mEndX;
    }

    public float getEndY() {
        return mEndY;
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public int getRadius() {
        return mRadius;
    }

    public int getDiagonal() {
        return mDiagonal;
    }

    public boolean getClickThroughHole() {
        return mClickThroughHole;
    }

    public boolean getClickThroughOverlay() {
        return mClickThroughOverlay;
    }

    public void calculateCoordinates() {

        //Отримання метрики вікна
        mDensity = mActivity.getResources().getDisplayMetrics().density;
        //Зміщення - це значення на скільки воронка буде відступати від меж цільової View
        mPadding = (int) (mPaddingPercent * mDensity);

        //Якщо стиль отвору коло
        if (mStyle == Overlay.Style.Circle) {

            //Якщо цільова View не null
            if (mTarget != null) {
                //Отримання початкових координат цільової View
                int[] position = new int[2];
                mTarget.getLocationOnScreen(position);
                //Обрахування радіусу кола по ширині та висоті цільової View
                if (mTarget.getWidth() == mTarget.getHeight()) {
                    //Якщо View одинакова по ширині та висоті, радіус кола вірно вирахується і за висотою і шириною
                    mRadius = mTarget.getWidth() / 2 + mPadding;
                } else if (mTarget.getHeight() > mTarget.getWidth()) {
                    //Якщо View вища ніж ширша, радіус кола вираховується за висотою
                    mRadius = mTarget.getHeight() / 2 + mPadding;
                } else {
                    //Якщо View ширша ніж вища, радіус кола вираховується за шириною
                    mRadius = mTarget.getWidth() / 2 + mPadding;
                }
                //Обрахування початкових координат отвору
                mStartX = position[0] + mTarget.getWidth() / 2;
                mStartY = position[1] + mTarget.getHeight() / 2;
                //Інакше, якщо цільової View не передбачено і користувач задає координати області, яку потрібно обвести, вручну
                //Зауваження: задані користувачем власні координати, трактуються
                //ніби це координати View елементу, яку виділить отвір. Отож координати
                //отвору будуть все одно обраховуватись з розрахунком на зміщення від кастомних координат,
                //напряму заданими користувачем.
            } else {
                //Якщо задана діагональ і вона має коректне значення
                if (mDiagonal != EMPTY_VALUE && mDiagonal > EMPTY_VALUE) {
                    //Діагональ виступає шириною і висотою
                    mWidth = mDiagonal;
                    mHeight = mDiagonal;
                    //Обрахування радіусу кола, з розрахунком на зміщення
                    mRadius = mDiagonal / 2 + mPadding;
                    //Обрахування початкових координат отвору
                    mStartX = mStartX + mDiagonal / 2;
                    mStartY = mStartY + mDiagonal / 2;

                }
                //Якщо діагональ не задана та координати кінця мають коректні значення
                if ((mDiagonal == EMPTY_VALUE || mDiagonal < EMPTY_VALUE) && (mEndX != EMPTY_VALUE && mEndX > EMPTY_VALUE && mEndY != EMPTY_VALUE && mEndY > EMPTY_VALUE)) {
                    //По координатам кінця і початку, вираховуються ширина та висота
                    mWidth = mEndX - mStartX;
                    mHeight = mEndY - mStartY;
                    //Обрахування радіусу кола, з розрахунком на зміщення
                    if (mWidth == mHeight) {
                        //Якщо ширина та висота одинакові, радіус кола вірно вирахується і за висотою і шириною
                        mRadius = (int) mWidth / 2 + mPadding;
                    } else if (mHeight > mWidth) {
                        //Якщо висота більша за ширину,
                        mRadius = (int) mHeight / 2 + mPadding;
                    } else {
                        //Якщо ширина більша за висоту,
                        mRadius = (int) mWidth / 2 + mPadding;
                    }
                    //Обрахування початкових координат отвору
                    mStartX = mStartX + mWidth / 2;
                    mStartY = mStartY + mHeight / 2;
                }

                if (mStartX == EMPTY_VALUE || mStartX < EMPTY_VALUE || mStartY == EMPTY_VALUE || mStartY < EMPTY_VALUE) {
                    throw new IllegalArgumentException("Custom area must have correct start coordinates");
                }
            }

            if (mStartX > mActivity.getResources().getDisplayMetrics().widthPixels) {
                throw new IndexOutOfBoundsException("Start x value biggest than screen width");
            }
            if (mStartY > mActivity.getResources().getDisplayMetrics().heightPixels) {
                throw new IndexOutOfBoundsException("Start y value biggest than screen height");
            }

        }

        //Якщо стиль отвору прямокутник або заокруглений прямокутник
        if (mStyle == Overlay.Style.Rectangle || mStyle == Overlay.Style.RoundedRectangle) {

            //Радіус стає не потрібний
            mRadius = EMPTY_VALUE;

            //Якщо цільова View не null
            if (mTarget != null) {
                //Отримання початкових координат цільової View
                int[] position = new int[2];
                mTarget.getLocationOnScreen(position);
                //Обрахування початкових координат отвору з розрахунком на зміщення
                mStartX = position[0] - mPadding;
                mStartY = position[1] - mPadding;
                //Обрахування кінцевих координат отвору з розрахунком на зміщення
                mEndX = position[0] + mTarget.getWidth() + mPadding;
                mEndY = position[1] + mTarget.getHeight() + mPadding;
                //Інакше, якщо цільової View не передбачено і користувач задає координати області, яку потрібно обвести, вручну
                //Зауваження: задані користувачем власні координати, трактуються
                //ніби це координати View елементу, яку виділить отвір. Отож координати
                //отвору будуть все одно обраховуватись з розрахунком на зміщення від кастомних координат,
                //напряму заданими користувачем.
            } else {
                //Якщо задана діагональ і вона має коректне значення
                if (mDiagonal != EMPTY_VALUE && mDiagonal > EMPTY_VALUE) {
                    //Діагональ виступає шириною і висотою
                    mWidth = mDiagonal;
                    mHeight = mDiagonal;
                    //Обрахування кінцевих координат воронки з розрахунком на зміщення
                    mEndX = mStartX + mDiagonal + mPadding;
                    mEndY = mStartY + mDiagonal + mPadding;
                }
                //Якщо діагональ не задана та координати кінця мають коректні значення
                if ((mDiagonal == EMPTY_VALUE || mDiagonal < EMPTY_VALUE) && (mEndX != EMPTY_VALUE && mEndX > EMPTY_VALUE && mEndY != EMPTY_VALUE && mEndY > EMPTY_VALUE)) {
                    //По координатам кінця і початку, вираховуються ширина та висота
                    mWidth = mEndX - mStartX;
                    mHeight = mEndY - mStartY;
                    //Обрахування кінцевих координат отвору з розрахунком на зміщення
                    mEndX = mStartX + mWidth + mPadding;
                    mEndY = mStartY + mHeight + mPadding;
                }
                //Обрахування початкових координат отвору
                mStartX = mStartX - mPadding;
                mStartY = mStartY - mPadding;

                if (mStartX == EMPTY_VALUE || mStartX < EMPTY_VALUE || mStartY == EMPTY_VALUE || mStartY < EMPTY_VALUE) {
                    throw new IllegalArgumentException("Custom area must have correct start coordinates");
                }
                if ((mDiagonal == EMPTY_VALUE || mDiagonal < EMPTY_VALUE) && (mEndX == EMPTY_VALUE || mEndX < EMPTY_VALUE) || (mEndY == EMPTY_VALUE || mEndY < EMPTY_VALUE)) {
                    throw new IllegalArgumentException("Custom area has neither diagonal nor end coordinates");
                }
            }

            if (mStartX > mActivity.getResources().getDisplayMetrics().widthPixels) {
                throw new IndexOutOfBoundsException("Start x value biggest than screen width");
            }
            if (mStartY > mActivity.getResources().getDisplayMetrics().heightPixels) {
                throw new IndexOutOfBoundsException("Start y value biggest than screen height");
            }

        }

    }

    /**
     * <b>Клас Builder(Будівельник)</b><br/>
     * Паттерн проектування Будівельник, який створює екземпляр класу Overlay з заданами параметрами
     *
     * @author Denis Makovskyi
     */
    public static class Builder {

        public Activity mActivity;
        public View mTarget;
        public float mDensity;
        public int mPaddingPercent = 10;
        public int mBackGroundColor;
        public Style mStyle;
        public float mStartX = EMPTY_VALUE;
        public float mStartY = EMPTY_VALUE;
        public float mEndX = EMPTY_VALUE;
        public float mEndY = EMPTY_VALUE;
        public int mDiagonal = EMPTY_VALUE;
        public boolean mClickThroughHole = true;
        public boolean mClickThroughOverlay = false;
        public static final int EMPTY_VALUE = -1;

        public Builder() {
        }

        /**
         * <b>Задає цільову активність на якій буде відображатись помічник</b>
         *
         * @param activity - цільова Activity
         */
        public Builder setActivity(Activity activity) {
            this.mActivity = activity;
            return this;
        }

        /**
         * <b>Задає цільовий View елемент, який буде виділений прозорою областю (отвором) в накладці. (Опціонально)</b>
         *
         * @param target - цільовий View елемент
         */
        public Builder setTarget(View target) {
            this.mTarget = target;
            return this;
        }

        /**
         * <b>Задає колір накладки</b>
         *
         * @param backgroundColor - колір накладки
         */
        public Builder setBackgroundColor(int backgroundColor) {
            this.mBackGroundColor = backgroundColor;
            return this;
        }

        /**
         * <b>Задає стиль отвору в накладці</b>
         *
         * @param style - стиль отвору
         * @see Overlay.Style
         */
        public Builder setStyle(Overlay.Style style) {
            this.mStyle = style;
            return this;
        }

        /**
         * <b>Початкові координати області, що має знаходитись в отворі.(Опціонально)</b><br/>
         * <u>Якщо необхідно виділити довільну область а не прив’язуватись до конкретного View елементу.</u><br/>
         * <u>Актуально лиш якщо не був заданий цільовий View елемент.</u>
         *
         * @param startX - Х - координата точки початку
         * @param startY - У - координата точки початку
         */
        public Builder setStartCoordinates(int startX, int startY) {
            this.mStartX = startX;
            this.mStartY = startY;
            return this;
        }

        /**
         * <b>Кінцеві координати області, що має знаходитись в отворі.(Опціонально)</b><br/>
         * <u>Якщо необхідно виділити довільну область а не прив’язуватись до конкретного View елементу.</u><br/>
         * <u>Якщо використувується метод setDiagonal(int diagonal), то кінцеві координати вирахуються автоматично. Викликати даний метод немає необхідності.</u><br/>
         * <u>Актуально лиш якщо не був заданий цільовий View елемент.</u>
         *
         * @param endX - Х - координата точки кінця
         * @param endY - У - координата точки кінця
         */
        public Builder setEndCoordinates(int endX, int endY) {
            this.mEndX = endX;
            this.mEndY = endY;
            return this;
        }

        /**
         * <b>Діагональ яка пролягає від початкових точок області, що має знаходитись в отрворі.(Опціонально)</b><br/>
         * <u>Якщо необхідно виділити довільну область а не прив’язуватись до конкретного View елементу.</u><br/>
         * <u>Точки кінця для стилю Квадрат/Заокруглений квадрат та радіус для стилю Коло прорахуються автоматично.</u><br/>
         * <u>Актуально лиш якщо не був заданий цільовий View елемент.</u>
         *
         * @param diagonal - довжина діагоналі
         */
        public Builder setDiagonal(int diagonal) {
            this.mDiagonal = diagonal;
            return this;
        }

        /**
         * <b>Відсоток відступу отвору від цілової View/довільної області. По замовчуванню становить 10 * screenDensity.(Опціонально)</b><br/>
         * <u>Актуально лиш якщо не був заданий цільовий View елемент.</u>
         *
         * @param percent - відсоток відступу отвору від цілової View/довільної області
         */
        public Builder setPaddingPercent(int percent) {
            this.mPaddingPercent = percent;
            return this;
        }

        /**
         * <b>Прапорець активності кліку по отвору в накладці</b><br/>
         * <u>Якщо прапорець встановлений в Істина - подія натискання буде спрацьовувати в отворі, інакше, якщо прапорець встановлений в Хиба - подія натискання буде блокуватись</u><br/>
         * <u>Якщо натискання активне - натискання буде спрацьовувати на View елементі що знаходиться в отворі</u>
         *
         * @param clickThroughHole - прапорець true або false
         */
        public Builder setClickThroughHole(boolean clickThroughHole) {
            this.mClickThroughHole = clickThroughHole;
            return this;
        }

        /**
         * <b>Прапорець активності кліку по накладці (Не має відношення до отвору в накладці)</b><br/>
         * <u>Якщо прапорець встановлений в Істина - подія натискання буде спрацьовувати на накладці, інакше, якщо прапорець встановлений в Хиба - подія натискання буде блокуватись</u><br/>
         * <u>Якщо натискання активне - натискання буде спрацьовувати на View елементі що знаходиться під накладкою</u>
         *
         * @param clickThroughOverlay - прапорець true або false
         */
        public Builder setClickThroughOverlay(boolean clickThroughOverlay) {
            this.mClickThroughOverlay = clickThroughOverlay;
            return this;
        }

        /**
         * <b>Створює екземпляр класу Overlay з заданими параметрами</b><br/>
         *
         * @return new Overlay(this)
         */
        public Overlay build() {
            return new Overlay(this);
        }

    }
}
