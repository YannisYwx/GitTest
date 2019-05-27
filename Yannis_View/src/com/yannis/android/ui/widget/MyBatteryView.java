package com.yannis.android.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
/** * @author kince 
 *  * @category �Զ���View���
 *  * @time 2014.2.13 
 *  * @qq 543781062 *  */
public class MyBatteryView extends View {	
	/**	 * ������Ϣ	 */	
	private Paint mBatteryPaint;
	private Paint mPowerPaint;
	private float mBatteryStroke = 2f;
	/**	 * ��Ļ�߿�	 */	
	private int measureWidth;
	private int measureHeigth;
	/**	 * 	 * ��ز���	 */	
	// ��صĸ߶�	
	private float mBatteryHeight = 30f;
	// ��صĿ��	
	private float mBatteryWidth = 60f; 
	private float mCapHeight = 15f;	
	private float mCapWidth = 5f;	
	/**	 * 	 * ��ص���	 */	
	private float mPowerPadding = 1;	
	private float mPowerHeight = mBatteryHeight - mBatteryStroke- mPowerPadding * 2; 
	// �������ĸ߶�	
	private float mPowerWidth = mBatteryWidth - mBatteryStroke - mPowerPadding* 2;
	// ���������ܿ��	
	private float mPower = 0f;	
	/**	 * 	 * ����	 */	
	private RectF mBatteryRect;	
	private RectF mCapRect;	
	private RectF mPowerRect;
	
	public MyBatteryView(Context context) {
		super(context);
		initView();	
	}	
	
	public MyBatteryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();	
	}	
	
	public MyBatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();	
	}	
	
	public void initView() {
		/**		 * ���õ�ػ���		 */		
		mBatteryPaint = new Paint();
		mBatteryPaint.setColor(Color.GRAY);
		mBatteryPaint.setAntiAlias(true);
		mBatteryPaint.setStyle(Style.STROKE);
		mBatteryPaint.setStrokeWidth(mBatteryStroke);
		/**		 * ���õ�������		 */		
		mPowerPaint = new Paint();
		mPowerPaint.setColor(Color.RED);
		mPowerPaint.setAntiAlias(true);	
		mPowerPaint.setStyle(Style.FILL);
		mPowerPaint.setStrokeWidth(mBatteryStroke);	
		/**
		 * * ���õ�ؾ���
		 * */
		mBatteryRect = new RectF(mCapWidth, 0, mBatteryWidth, mBatteryHeight);
		/**		 * ���õ�ظǾ���		 */
		mCapRect = new RectF(0, (mBatteryHeight - mCapHeight) / 2, mCapWidth,
				(mBatteryHeight - mCapHeight) / 2 + mCapHeight);
		/**		 * ���õ�������		 */	
		mPowerRect = new RectF(mCapWidth + mBatteryStroke / 2 + mPowerPadding+ mPowerWidth * ((100f - mPower) / 100f),
				mPowerPadding + mBatteryStroke / 2, mBatteryWidth - mPowerPadding * 2, mBatteryStroke / 2
				+ mPowerPadding + mPowerHeight);	}
	@Override	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		canvas.translate(measureWidth / 2, measureHeigth / 2);
		canvas.drawRoundRect(mBatteryRect, 2f, 2f, mBatteryPaint);
		// �����������Ҫ���� ���ʵĿ��
		canvas.drawRoundRect(mCapRect, 2f, 2f, mBatteryPaint);
		// ����ظ�
		canvas.drawRect(mPowerRect, mPowerPaint);
		// ������	
		canvas.restore();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureWidth = MeasureSpec.getSize(widthMeasureSpec);
		measureHeigth = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(measureWidth, measureHeigth);
	}
	
	public void setPower(float power) {
		mPower = power;
		if (mPower< 0) {
			mPower = 0;	
			}		
		mPowerRect = new RectF(mCapWidth + mBatteryStroke / 2 + mPowerPadding+ mPowerWidth * ((100f - mPower) / 100f), 
				mPowerPadding + mBatteryStroke / 2, 
				mBatteryWidth - mPowerPadding * 2, 
				mBatteryStroke / 2+ mPowerPadding + mPowerHeight);
		invalidate();
	}
}
