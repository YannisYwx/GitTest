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
		 * ���û�����ʽΪ��ߣ�Բ�������Ȼ������䲻Ȼ��ô��˼��
		 * 
		 * ������ʽ�����֣� 1.Paint.Style.STROKE����� 2.Paint.Style.FILL_AND_STROKE����߲����
		 * 3.Paint.Style.FILL�����
		 */
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//		mPaint.setColor(color.lightgray);
		mPaint.setStrokeWidth(10);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		
		// ���û�����ɫΪ�Զ�����ɫ  
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
		 * ȷ���̲߳���ִ�в���ˢ�½���
		 */
		while (true) {
			try {
				/*
				 * ����뾶С��200���Լӷ������200�����ð뾶ֵ��ʵ������
				 */
				if (circle_radiu <= 200) {
					circle_radiu += 10;

					// ˢ��View
					postInvalidate();
				} else {
					circle_radiu = 0;
				}

				// ÿִ��һ����ͣ40����
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
