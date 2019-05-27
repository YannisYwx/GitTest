package com.yannis.android.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

public class BatteryView extends View {
	
	private Paint mPanit;
	private Paint mPowerPanit;
	
	private RectF batteryRect;//��ص�����
	private RectF batteryCapRect;//��ظ�ñ
	private RectF batteryPowerRect;//��ص���
	
	private float mBatteryStrokeWidth=2.0f;//�߿�
	private float mBatteryWidth=60f;//��صĿ�
	private float mBatterHeigth=30f;//��صĸ�
	private float mBatteryCapWidth=5f;//��صĿ�
	private float mBatterCapHeigth=15f;//��صĸ�
	private float mBatterPowerPadding=1f;
	private float allPowerWidth=mBatteryWidth-mBatterPowerPadding*2-mBatteryStrokeWidth;
	private float powerHeigth=mBatterHeigth-mBatterPowerPadding*2-mBatteryStrokeWidth;
	
	private int powerProgress=100;
	
	private int measureWidth;
	private int measureHeigth;
	
	public BatteryView(Context context) {
		super(context);
		init();
	}

	public BatteryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public void init(){
		mPanit=new Paint();
		mPanit.setAntiAlias(true);
		mPanit.setColor(Color.GRAY);
		mPanit.setStyle(Style.STROKE);
		mPanit.setStrokeWidth(mBatteryStrokeWidth);
		
		mPowerPanit=new Paint();
		mPowerPanit.setAntiAlias(true);
		mPowerPanit.setColor(Color.RED);
		mPowerPanit.setStyle(Style.FILL);
		mPowerPanit.setStrokeWidth(mBatteryStrokeWidth);
		this.setBackgroundColor(Color.BLACK);
		batteryRect=new RectF(0, 0, mBatteryWidth, mBatterHeigth);
		batteryCapRect=new RectF(mBatteryWidth, (mBatterHeigth-mBatterCapHeigth)/2f, mBatteryWidth+mBatteryCapWidth, (mBatterHeigth+mBatterCapHeigth)/2f);
		batteryPowerRect=new RectF(mBatteryStrokeWidth+mBatterPowerPadding, mBatteryStrokeWidth+mBatterPowerPadding, mBatterPowerPadding+allPowerWidth*powerProgress/100f, powerHeigth+mBatterPowerPadding);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		measureWidth = MeasureSpec.getSize(widthMeasureSpec);
		measureHeigth = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(measureWidth, measureHeigth);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.save();
		canvas.translate(measureWidth/2f, measureHeigth/2f);
		canvas.drawRoundRect(batteryRect, 2f, 2f,mPanit);
		canvas.drawRoundRect(batteryCapRect, 2f, 2f,mPanit);
		canvas.drawRect(batteryPowerRect, mPowerPanit);
		
		canvas.restore();
	}

}
