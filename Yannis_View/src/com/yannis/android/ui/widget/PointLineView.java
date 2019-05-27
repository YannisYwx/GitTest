package com.yannis.android.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PointLineView extends View {
	
	private static final float TOP_BOTTOM_LINE_HIGH_SCALE=1/8f;
	private static final float SMALL_CIRCLE_SCALE=1/4f;
	
	private int vWidth,vHigh;//view 的宽和高
	private float small_Circle_Raidu;//小圆圈的半径
	private float big_Circle_Raidu;//大圆圈的半径
	private float circleLineXWidth;//圈线的X轴宽
	
	
	private Paint paint;
	
	
	private int top_bottom_lineColor=0xffef5440;
	private int clCount=7;

	public PointLineView(Context context) {
		super(context);
		init();
	}

	public PointLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PointLineView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		this.vWidth=w;
		this.vHigh=h;
		this.circleLineXWidth=vWidth/clCount;
		this.small_Circle_Raidu=circleLineXWidth*SMALL_CIRCLE_SCALE;
		this.big_Circle_Raidu=small_Circle_Raidu*1.3f;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		drawTopButtomLine(canvas);
	}
	
	private void drawTopButtomLine(Canvas canvas){
		int x=getScrollX();
		canvas.drawLine(x, vHigh*TOP_BOTTOM_LINE_HIGH_SCALE, vWidth+x, vHigh*TOP_BOTTOM_LINE_HIGH_SCALE, paint);
		canvas.drawLine(x, vHigh-vHigh*TOP_BOTTOM_LINE_HIGH_SCALE, vWidth+x, vHigh-vHigh*TOP_BOTTOM_LINE_HIGH_SCALE, paint);
	}
	
	public void init(){
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.STROKE);
	}

}
