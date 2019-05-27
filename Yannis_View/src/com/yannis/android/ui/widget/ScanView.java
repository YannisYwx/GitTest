package com.yannis.android.ui.widget;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

/**
 * Created by Wayne on 2015/7/27.
 */
public class ScanView extends View {

    private Rect textRect;
    private int maxStarRadius;
    private final Matrix mMatrix;
    private Paint mPaint;
    private Paint mScanPaint;
    private int mainColor = Color.parseColor("#fa5959");
    private int scanStartColor = Color.parseColor("#eeff8585");
    private int scanEndColor = Color.parseColor("#00fa5959");
    private int smallBallColor = Color.parseColor("#3883c0");
    private int circleColor = Color.parseColor("#ff8383");
    private Shader mShader;
    private int starNumber = 10;
    private int mWidth;
    private RectF mRect;
    private int mCenter;
    private int mRadius;
    private int mPanelRadius;
    private int largeBallAngle;
    private int large2BallAngle;
    private int smallBallAngle;
    private List<Integer> radiusList;
    private int angle = 0;
    private int scanTime = 10;
    private ObjectAnimator largeBallAnim;
    private ObjectAnimator large2BallAnim;
    private final ObjectAnimator smallBallAnim;
    private final ObjectAnimator scanAnim;
    private final ValueAnimator starSizeAnim;
    private ValueAnimator tryAnim;
    private List<Point> points;
    private boolean isRunning = false;
    public static final int STATE_SCAN = 0x1;
    public static final int STATE_RETRY = 0x2;
    private int STATE = STATE_SCAN;
    private Paint textPaint;
    private int tryAlpha = 255;
    private float tryAngle = 0;
    private float textHeight;
    private int textWidth;
    private boolean mClickable = false;
    private int ballSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,getResources().getDisplayMetrics());

    /**
     * 当第一次扫描结束后，如果没有发现任何设备，可以调用这个函数显示重试的View进行重试
     */
    public void showRetryView() {
        if (!isRunning) {
            STATE = STATE_RETRY;
            invalidate();
            mClickable = false;
            this.setVisibility(View.VISIBLE);
            tryAnim.start();
        } else {
            STATE = STATE_SCAN;
            throw new IllegalStateException("必须在回调函数中调用此方法，并确保扫描已经结束！");
        }
    }

    public ScanView(Context context) {
        this(context, null);
    }

    /**
     * 单位 秒  默认10秒
     *
     * @param time
     */
    public void setScanTime(int time) {
        this.scanTime = time;
    }

    /**
     * 开始扫描
     */
    public void start() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isRunning) {
                    largeBallAnim.start();
                    large2BallAnim.start();
                    smallBallAnim.start();
                    scanAnim.start();
                    starSizeAnim.start();
                    isRunning = true;
                    STATE = STATE_SCAN;
                }
            }
        }, 500);
    }

    public void restart() {
        if (!isRunning) {
            if (radiusList != null) radiusList.clear();
            if (points != null) points.clear();
            largeBallAnim.start();
            large2BallAnim.start();
            smallBallAnim.start();
            scanAnim.start();
            starSizeAnim.start();
            isRunning = true;
            STATE = STATE_SCAN;
        }
    }

    public ScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mRect = new RectF();
        maxStarRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
       
        mScanPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//雷达Shader画笔
        mScanPaint.setStrokeCap(Paint.Cap.ROUND);
        mScanPaint.setStrokeWidth(4);
        
        mShader = new SweepGradient(0, 0, scanEndColor, scanStartColor);
        mMatrix = new Matrix();
        mShader.getLocalMatrix(mMatrix);
        
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                15.5f, getResources().getDisplayMetrics()));
        textPaint.setColor(mainColor);
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        textRect = new Rect();
        textPaint.getTextBounds("重试", 0, 1, textRect);
        textHeight = (metrics.ascent + metrics.descent);
        textWidth = textRect.width();
        
        //初始化动画
        largeBallAnim = ObjectAnimator.ofInt(this, "largeBallAngle", 0, 360);
        largeBallAnim.setDuration(scanTime * 1000);
        largeBallAnim.setInterpolator(new LinearInterpolator());
        largeBallAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                stopAnimation();
                if (mListener != null) {
                    mListener.scanEnd();
                }
            }
        });
        largeBallAnim.addUpdateListener(AnimationListener);

        //初始化动画
        large2BallAnim = ObjectAnimator.ofInt(this, "large2BallAngle", 0, 360);
        large2BallAnim.setRepeatCount(ObjectAnimator.INFINITE);
        large2BallAnim.setRepeatMode(ObjectAnimator.RESTART);
        large2BallAnim.setDuration(scanTime * 3 / 4 * 1000);
        large2BallAnim.setInterpolator(new LinearInterpolator());
        large2BallAnim.addUpdateListener(AnimationListener);

        smallBallAnim = ObjectAnimator.ofInt(this, "smallBallAngle", 0, 360);
        smallBallAnim.setRepeatCount(ObjectAnimator.INFINITE);
        smallBallAnim.setRepeatMode(ObjectAnimator.RESTART);
        smallBallAnim.setDuration((scanTime * 2 / 3) * 1000);
        smallBallAnim.setInterpolator(new LinearInterpolator());
        smallBallAnim.addUpdateListener(AnimationListener);

        scanAnim = ObjectAnimator.ofInt(this, "angle", 0, 360);
        scanAnim.setRepeatCount(ObjectAnimator.INFINITE);
        scanAnim.setRepeatMode(ObjectAnimator.RESTART);
        scanAnim.setDuration((scanTime / 3) * 1000);
        scanAnim.setInterpolator(new LinearInterpolator());
        scanAnim.addUpdateListener(AnimationListener);

        starSizeAnim = ValueAnimator.ofInt(0, maxStarRadius, 0, maxStarRadius, 0, maxStarRadius);
        starSizeAnim.setRepeatCount(ObjectAnimator.INFINITE);
        starSizeAnim.setRepeatMode(ObjectAnimator.REVERSE);
        starSizeAnim.setDuration((scanTime - 2) * 1000);
        starSizeAnim.setInterpolator(new LinearInterpolator());
        starSizeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                maxStarRadius = value;
                postInvalidate();
            }
        });

        tryAnim = ValueAnimator.ofFloat(0, 360);
        tryAnim.setDuration((long) (1.5 * 1000L));
        tryAnim.setInterpolator(new LinearInterpolator());
        tryAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                tryAngle = value;
                tryAlpha = (int) ((255F / 360F) * value);
                invalidate();
            }
        });
        tryAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                mClickable = true;
            }
        });

    }

    private void stopAnimation() {
        if (isRunning) {
            if (large2BallAnim != null && large2BallAnim.isRunning())
                large2BallAnim.end();
            if (smallBallAnim != null && smallBallAnim.isRunning())
                smallBallAnim.end();
            if (scanAnim != null && scanAnim.isRunning())
                scanAnim.end();
            if (starSizeAnim != null && starSizeAnim.isRunning())
                starSizeAnim.end();
            isRunning = false;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(), measureWidth());
    }

    private int measureWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels > outMetrics.heightPixels ?
                outMetrics.heightPixels : outMetrics.widthPixels;
        width = width + 10;
        return width;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mCenter = mWidth / 2;
        mRadius = mCenter * 5 / 6;
        mPanelRadius = mRadius * 11 / 12;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (STATE == STATE_RETRY) {
            drawRetryButton(canvas);
        } else if (STATE == STATE_SCAN) {
            drawOutCircle(canvas);
            drawRingCircle(canvas);
            drawBluePanel(canvas);
            drawStars(canvas);
            drawInnerCircle(canvas);
            drawScanShader(canvas);
        }
    }

    private void drawRetryButton(Canvas canvas) {
        canvas.save();
        canvas.translate(mCenter, mCenter);
        canvas.rotate(-90);
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(circleColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(255);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        int radius = mRadius * 2 / 5;
        mRect.set(-radius, -radius, radius, radius);
        canvas.drawArc(mRect, 0, tryAngle, false, mPaint);
        canvas.restore();

        textPaint.setAlpha(tryAlpha);
        canvas.drawText("重试", mCenter - textWidth, mCenter - textHeight / 2, textPaint);
    }
    
    /**
     * 画蓝球
     * @param canvas
     */
    private void drawScanShader(Canvas canvas) {
        canvas.save();
        canvas.translate(mCenter, mCenter);
        canvas.rotate(-90);
        mMatrix.setRotate(angle);
        mShader.setLocalMatrix(mMatrix);
        mScanPaint.setShader(mShader);
        canvas.drawCircle(0, 0, mPanelRadius, mScanPaint);
        mPaint.reset();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(smallBallColor);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawCircle((float) (mRadius * Math.cos(Math.toRadians(largeBallAngle))), (float) (mRadius * Math.sin(Math.toRadians(largeBallAngle))), ballSize, mPaint);
        canvas.drawCircle((float) (mRadius * Math.cos(Math.toRadians(large2BallAngle + 180))), (float) (mRadius * Math.sin(Math.toRadians(large2BallAngle + 180))), ballSize, mPaint);
        canvas.drawCircle((float) (mRadius * Math.cos(Math.toRadians(smallBallAngle + 90))), (float) (mRadius * Math.sin(Math.toRadians(smallBallAngle + 90))), ballSize/2, mPaint);
        canvas.restore();
    }
    
    /**
     * 画扫描点
     * @param canvas
     */
    private void drawStars(Canvas canvas) {
        canvas.save();
        canvas.translate(mCenter, mCenter);
        mPaint.reset();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(Color.WHITE);
        if (points == null || points.size() == 0) {
            points = new ArrayList<Point>();
            radiusList = new ArrayList<Integer>();
            for (int i = 0; i < starNumber; i++) {
                int radius = (int) (Math.random() * maxStarRadius + 1);
                int x = ((int) Math.rint(Math.random() * mPanelRadius)) * (Math.random() > 0.5 ? -1 : 1);
                int y = ((int) Math.rint(Math.random() * mPanelRadius)) * (Math.random() > 0.5 ? -1 : 1);
                int len = (int) Math.sqrt(Math.pow((x), 2) + Math.pow((y), 2));
                if (len <= (mPanelRadius - maxStarRadius / 2)) {
                    canvas.drawCircle(x, y, (maxStarRadius - radius), mPaint);
                    Point point = new Point(x, y);
                    points.add(point);
                    radiusList.add(radius);
                }
            }
        } else {
            for (int i = 0; i < points.size(); i++) {
                canvas.drawCircle(points.get(i).x, points.get(i).y, (maxStarRadius - radiusList.get(i)), mPaint);
            }
        }
        canvas.restore();
    }

    private void drawInnerCircle(Canvas canvas) {
        mPaint.reset();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(circleColor);
        mPaint.setStrokeWidth(2);
        canvas.drawCircle(mCenter, mCenter, mRadius * 2 / 4, mPaint);
        canvas.drawCircle(mCenter, mCenter, mRadius * 2 / 7, mPaint);
        canvas.save();
        canvas.translate(mCenter, mCenter);
        mPaint.setColor(Color.WHITE);
        canvas.drawLine(-mRadius * 11 / 12, 0, mRadius * 11 / 12, 0, mPaint);
        canvas.drawLine(0, -mRadius * 11 / 12, 0, mRadius * 11 / 12, mPaint);
        canvas.restore();
    }
    
    /**
     * 画蓝球
     * @param canvas
     */
    private void drawBluePanel(Canvas canvas) {
        mPaint.reset();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mainColor);
        canvas.drawCircle(mCenter, mCenter, mPanelRadius, mPaint);
    }
    
    /**
     * 画第二个圈
     * @param canvas
     */
    private void drawRingCircle(Canvas canvas) {
        mPaint.reset();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(circleColor);
        mPaint.setStrokeWidth(3);
        canvas.drawCircle(mCenter, mCenter, mRadius, mPaint);
    }
    
    /**
     * 画最外圈
     * @param canvas
     */
    private void drawOutCircle(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(circleColor);
        mPaint.setAlpha(180);
        mPaint.setStrokeWidth(2);
        mRect.set(0, 0, mWidth, mWidth);
        canvas.drawArc(mRect, 0, 70, false, mPaint);
        canvas.drawArc(mRect, 110, 70, false, mPaint);
        canvas.drawArc(mRect, 180, 70, false, mPaint);
        canvas.drawArc(mRect, 290, 70, false, mPaint);
    }

    private ValueAnimator.AnimatorUpdateListener AnimationListener = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator animation) {
            invalidate();
        }
    };

    /**
     * @hide
     */
    public void setLargeBallAngle(int largeBallAngle) {
        this.largeBallAngle = largeBallAngle;
    }

    /**
     * @hide
     */
    public void setSmallBallAngle(int smallBallAngle) {
        this.smallBallAngle = smallBallAngle;
    }

    /**
     * @hide
     */
    public void setAngle(int angle) {
        this.angle = angle;
    }

    /**
     * @hide
     */
    public void setLarge2BallAngle(int large2BallAngle) {
        this.large2BallAngle = large2BallAngle;
    }

    private OnScanEndListener mListener;

    public void setListener(OnScanEndListener mListener) {
        this.mListener = mListener;
    }

    public interface OnScanEndListener {
        void scanEnd();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mClickable) {
                double distance = Math.pow((x - mCenter), 2) + Math.pow((y - mCenter), 2);
                if (distance <= ((mRadius * 2 / 5) * (mRadius * 2 / 5))) {
                    restart();
                }
            }
            return true;
        }

        return super.onTouchEvent(event);
    }
}