package com.yannis.android.ui.sampleview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.yannis.android.ui.R;
import com.yannis.android.ui.R.color;

@SuppressLint("NewApi")
public class ColorFilterView extends View{
	private Paint mPaint;
	private Context mContext;
	private Bitmap foot;
	private boolean isClick;

	public ColorFilterView(Context context) {
		// TODO Auto-generated constructor stub
		this(context, null);
	}

	public ColorFilterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ColorFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
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
		initRes();
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
        
       final ColorMatrix colorMatrix=new ColorMatrix(new float[]{
        		 	0.1f, 0, 0, 0, 0,  
        	        0, 0.1f, 0, 0, 0,  
        	        0, 0, 1, 0, 0,  
        	        0, 0, 0, 0.5f, 0,  
        });
//        mPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
		
		this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isClick){
					isClick=false;
			        mPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
				}else{
					isClick=true;
					mPaint.setColorFilter(null);
				}
				invalidate();
			}
		});
	}
	
	private void initRes(){
		foot=BitmapFactory.decodeResource(getResources(), R.drawable.foot);
		
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawBitmap(foot,(getMeasuredWidth()-foot.getWidth())/2.0f,(getMeasuredHeight()-foot.getHeight())/2.0f,mPaint);

	}

	public int[] getWidthHight(Activity act) {
		int[] wh = { 0, 0 };
		wh[0] = act.getWindowManager().getDefaultDisplay().getWidth();
		wh[1] = act.getWindowManager().getDefaultDisplay().getHeight();
		return wh;
	}

}
