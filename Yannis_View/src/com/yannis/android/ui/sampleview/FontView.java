package com.yannis.android.ui.sampleview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class FontView extends View{
	private Paint textPaint,linePaint;
	private Context mContext;
    private static final String TEXT = "ap爱哥ξτβбпшㄎㄊěǔぬも┰┠№＠↓"; 
    private int baseX,baseY;


	public FontView(Context context) {
		// TODO Auto-generated constructor stub
		this(context, null);
	}

	public FontView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public FontView(Context context, AttributeSet attrs, int defStyleAttr) {
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
		initPaint();
	}
	
	/** 
     * 初始化画笔 
     */  
    private void initPaint() {  
        // 实例化画笔  
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
        textPaint.setTextSize(70);  
        textPaint.setColor(Color.BLACK);  
  
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
        linePaint.setStyle(Paint.Style.STROKE);  
        linePaint.setStrokeWidth(1);  
        linePaint.setColor(Color.RED);  
    }  
  
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
  
        // 计算Baseline绘制的起点X轴坐标  
        Log.d("ywx", "canvas.width:"+canvas.getWidth()+"  canvas.height:"+canvas.getHeight());
        baseX = (int) (canvas.getWidth() / 2 - textPaint.measureText(TEXT) / 2);  
  
        // 计算Baseline绘制的Y坐标  
        baseY = (int) ((canvas.getHeight() / 2) + ((Math.abs(textPaint.ascent()-Math.abs(textPaint.descent()))) / 2));  
        canvas.drawText(TEXT, baseX, baseY, textPaint);  
  
        // 为了便于理解我们在画布中心处绘制一条中线  
        baseY = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));  
        canvas.drawLine(0, canvas.getHeight() / 2, canvas.getWidth(), canvas.getHeight() / 2, linePaint); 
        //baseLine
        canvas.drawLine(0, baseY, canvas.getWidth(), baseY, linePaint);  
    } 
	

	public int[] getWidthHight(Activity act) {
		int[] wh = { 0, 0 };
		wh[0] = act.getWindowManager().getDefaultDisplay().getWidth();
		wh[1] = act.getWindowManager().getDefaultDisplay().getHeight();
		return wh;
	}

}
