package com.yannis.android.ui.sampleview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yannis.android.ui.R.color;

public class CircleView extends View implements Runnable {
	private Paint mPaint;
	private Context mContext;
	private int w, h;
	private int circle_radiu=200;

	public CircleView(Context context) {
		// TODO Auto-generated constructor stub
		this(context, null);
	}

	public CircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init(context);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d("ywx", "onSizeChanged(" + w + "," + h + "," + oldw + "," + oldh
				+ ")");
		Log.d("ywx", "Width=" + getWidthHight((Activity) mContext)[0]
				+ ",Hight:" + getWidthHight((Activity) mContext)[1]);
		Log.d("ywx", "_Width=" + getMeasuredWidth() + ",_Hight:"
				+ getMeasuredHeight());
		Log.d("ywx", "paddingleft=" + getPaddingLeft() + ",paddingright:"
				+ getPaddingRight());

	}

	public void init(Context context) {
		mContext = context;

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		/*
		 * 设置画笔样式为描边，圆环嘛……当然不能填充不然就么意思了
		 * 
		 * 画笔样式分三种： 1.Paint.Style.STROKE：描边 2.Paint.Style.FILL_AND_STROKE：描边并填充
		 * 3.Paint.Style.FILL：填充
		 */
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//		mPaint.setColor(color.lightgray);
		mPaint.setStrokeWidth(10);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		
		// 设置画笔颜色为自定义颜色  
//        mPaint.setColor(Color.argb(255, 255, 128, 103));  
        
        ColorMatrix colorMatrix=new ColorMatrix(new float[]{
        		 	0.1f, 0, 0, 0, 0,  
        	        0, 0.1f, 0, 0, 0,  
        	        0, 0, 1, 0, 0,  
        	        0, 0, 0, 0.5f, 0,  
        });
        mPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		// canvas.drawCircle(getWidthHight(act)[0]/2, getWidthHight(act)[1]/2,
		// 200, mPaint);
		canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2,
				circle_radiu, mPaint);
		// canvas.drawLine(64, 64, 100, 150, mPaint);

	}

	public int[] getWidthHight(Activity act) {
		int[] wh = { 0, 0 };
		wh[0] = act.getWindowManager().getDefaultDisplay().getWidth();
		wh[1] = act.getWindowManager().getDefaultDisplay().getHeight();
		return wh;
	}

	public synchronized void setRadiu(int radiu) {
		this.circle_radiu = radiu;
		invalidate();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		/*
		 * 确保线程不断执行不断刷新界面
		 */
		while (true) {
			try {
				/*
				 * 如果半径小于200则自加否则大于200后重置半径值以实现往复
				 */
				if (circle_radiu <= 200) {
					circle_radiu += 10;

					// 刷新View
					postInvalidate();
				} else {
					circle_radiu = 0;
				}

				// 每执行一次暂停40毫秒
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
