package com.yannis.android.ui.sampleview;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MaskFilterView extends View {
	private Paint mPaint;
	private static final int RECT_SIZE = 400;
	private int left, top, right, bottom;

	public MaskFilterView(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}

	public MaskFilterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}

	public MaskFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
		// TODO Auto-generated constructor stub
	}
	
	public void init(){
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);  
        mPaint.setStyle(Paint.Style.FILL);  
        mPaint.setColor(0xFFff3811);  
  
        // …Ë÷√ª≠± ’⁄’÷¬Àæµ  
        mPaint.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.NORMAL)); 
        
        setLayerType(LAYER_TYPE_SOFTWARE, null);  
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		left = getMeasuredWidth() / 2 - RECT_SIZE / 2;
		top = getMeasuredHeight() / 2 - RECT_SIZE / 2;
		right = getMeasuredWidth() / 2 + RECT_SIZE / 2;
		bottom = getMeasuredHeight() / 2 + RECT_SIZE / 2;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
//		canvas.drawRect(150, 120, 250, 170, mPaint);
		Log.d("ywx", "left, top, right, bottom--"+left+"-"+top+"-"+right+"-"+bottom);
		canvas.drawRect(left, top, right, bottom, mPaint);
	}
}
