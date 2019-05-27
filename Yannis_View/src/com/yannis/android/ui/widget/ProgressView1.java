package com.yannis.android.ui.widget;

import java.util.Random;

import com.yannis.android.ui.test.ViewUtil;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ProgressView1 extends View {
	
	public static final int ARC_FULL_DEGREE =300;
	
	public static final int ARC_START_DEGREE =-240;
	
	public static final float ARC_WIDTH_SCALE=0.05f;
	
	public static final float ARC_RADIUS_SCALE=0.35f;
	
	public static final float THUMB_RADIUS_SCALE=0.08f;
	

	private Paint arcPaint;
	private Paint thumbPaint;
	private Paint textPaint;
	
	private int w;//view的宽
	private int h;//view的高
	private float centerX;//画布的圆心X
	private float centerY;//画布的圆心Y
	private float arcWidth;//弧的宽度
	private float circleWidth;//半径
	private float arcRadius;//弧半径
	private float radius;//thumb半径
	private float textSize;
	
	/** 
     * 进度条最大值和当前进度值 
     */  
    private float max=100f, progress=58f;  
    private float maxValue=100.0f, minValue=00.0f;  
    private float currentValue=75.0f;
    
	private int color_Arc_bg=0x22ffffff;
	
	/** 
     * 绘制弧线的矩形区域 
     */  
    private RectF circleRectF;
	
	
	
	public ProgressView1(Context context) {
		super(context);
		init();
	}

	public ProgressView1(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ProgressView1(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
		setMeasuredDimension(size, size);//必须调用 setMeasuredDimension(int,int)来存储这个View经过测量得到的measured width and height
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		this.w=w;
		this.h=h;
		this.centerX=w/2;
		this.centerY=h/2;
		this.arcWidth=w*ARC_WIDTH_SCALE;
		this.arcRadius=w*ARC_RADIUS_SCALE+arcWidth;
		this.radius=arcWidth*1.1f/2f;
		this.textSize=arcRadius*0.35f;
		
		circleRectF.top=centerY-arcRadius;
		circleRectF.left=centerX-arcRadius;
		circleRectF.right=centerX+arcRadius;
		circleRectF.bottom=centerY+arcRadius;
	}
	
	public void init(){
		arcPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		arcPaint.setStyle(Style.STROKE);
		arcPaint.setStrokeCap(Cap.ROUND);
		arcPaint.setColor(0x22ffffff);
		
		thumbPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		thumbPaint.setStyle(Style.FILL);
		
		textPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(Color.WHITE);
		textPaint.setTextAlign(Align.CENTER);
		
		circleRectF=new RectF();

	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//画背景
		arcPaint.setStrokeWidth(arcWidth);
		arcPaint.setColor(0x22000000);
		canvas.drawArc(circleRectF, ARC_START_DEGREE, ARC_FULL_DEGREE, false, arcPaint);
		//画进度
		arcPaint.setColor(Color.WHITE);
		float progressDegree=ARC_FULL_DEGREE*(currentValue/maxValue);
		canvas.drawArc(circleRectF, ARC_START_DEGREE, progressDegree, false, arcPaint);
		//画thumb
		double progressRadians=((30.0f+progressDegree)/180.0f)*Math.PI;
		float cx=(float) (centerX-arcRadius*Math.sin(progressRadians));
		float cy=(float) (centerY+arcRadius*Math.cos(progressRadians));
		thumbPaint.setColor(0x22000000);
		canvas.drawCircle(cx, cy, radius*1.30f, thumbPaint);
		thumbPaint.setColor(0x32000000);
		canvas.drawCircle(cx, cy, radius*1.70f, thumbPaint);
		thumbPaint.setColor(0x50000000);
		canvas.drawCircle(cx, cy, radius*2.20f, thumbPaint);
		thumbPaint.setColor(0xeeffffff);
		canvas.drawCircle(cx, cy, radius, thumbPaint);
		//画数字
		textPaint.setColor(0xffffffff);
		textPaint.setTextSize(textSize);
		String strValue=String.format("%.1f", currentValue);
		float currentValueY=centerY-(textPaint.ascent()+textPaint.descent())/2.0f;
		canvas.drawText(strValue, centerX, currentValueY, textPaint);
		//画单位
		textPaint.setColor(0x99ffffff);
		float unitX=centerX+ViewUtil.getTextRectWidth(textPaint, strValue)/2.0f*1.3f;
		float unitY=currentValueY-ViewUtil.getTextHeight(textPaint)/2.0f;
		textPaint.setTextSize(textSize*0.35f);
		unitY-=textPaint.ascent()+textPaint.descent()/2.0f;
		canvas.drawText("℃", unitX, unitY, textPaint);
		//画下标题
		textPaint.setTextSize(textSize*0.5f);
		textPaint.setColor(0xabffffff);
		float titleY=currentValueY+arcRadius/3.0f-(textPaint.ascent()+textPaint.descent())/2.0f;
		canvas.drawText("开始测量", centerX, titleY, textPaint);
	}
	
	public void setMaxValue(float maxValue){
		this.max=maxValue;
	}
	
	public void setProgress(float progress){
		if(progress<=0){
			progress=0;
		}
		if(progress>=100){
			progress=100;
		}
		this.currentValue=progress;
		invalidate();
	}
	
	public void startAnimation(){
		float p=new Random().nextFloat()*100.0f;
		int duration=(int) Math.abs((p-currentValue))*20;
		ObjectAnimator.ofFloat(this, "progress", this.currentValue,p).setDuration(duration).start();
		this.currentValue=p;
	}
	
	public void startAnimation(float value){
		if(value<=0){
			value=0;
		}
		if(value>=100){
			value=100;
		}
		int duration=(int) Math.abs((value-currentValue))*20;
		ObjectAnimator.ofFloat(this, "progress", this.currentValue,value).setDuration(duration).start();
		this.currentValue=value;

	}
	
	
	
	float currentX,currentY;
	boolean isDownInArc=false;
	boolean isByRomdamDown=false;
	boolean isByRomdam=false;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isDownInArc=false;
			isByRomdam=true;
			isByRomdamDown=false;
			currentX=event.getX();
			currentY=event.getY();
			isDownInArc=isTouchInArc(currentX, currentY);
			if(isDownInArc){
				//当前的进度
				float currentValue = calDegreeByPosition(currentX, currentY) / ARC_FULL_DEGREE * (maxValue-minValue)+minValue;
				setProgress(currentValue);
				isByRomdamDown=false;
			}else{
				isByRomdamDown=true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			currentX=event.getX();
			currentY=event.getY();
			isDownInArc=isTouchInArc(currentX, currentY);
			if(isDownInArc){
				float currentValue = calDegreeByPosition(currentX, currentY) / ARC_FULL_DEGREE * (maxValue-minValue)+minValue;
				setProgress(currentValue);
				isByRomdam=false;
			}else{
				isByRomdam=true;
			}
			break;
		case MotionEvent.ACTION_UP:
			isDownInArc=false;
			if(isByRomdam&&isByRomdamDown){
				startAnimation();
			}
			isByRomdam=false;
			isByRomdamDown=false;
			break;

		default:
			break;
		}
		
		return true;
	}
	
	 /** 
     * 判断该点是否在弧线上（附近） 
     */  
    private boolean isTouchInArc(float currentX, float currentY) {  
        float distance = calDistance(currentX, currentY, centerX, centerY);  
        float degree = calDegreeByPosition(currentX, currentY);  
        return distance > arcRadius - arcWidth * 2 && distance < arcRadius + arcWidth * 2  
                && (degree >= -8 && degree <= ARC_FULL_DEGREE + 8);  
    }
    
    private float calDistance(float x1, float y1, float x2, float y2) {  
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));  
    }
        
	
	/** 
     * 根据当前位置，计算出进度条已经转过的角度。 
     */  
    private float calDegreeByPosition(float currentX, float currentY) {  
        float a1 = (float) (Math.atan(1.0f * (centerX - currentX) / (currentY - centerY)) / Math.PI * 180);  
        if (currentY < centerY) {  
            a1 += 180;  
        } else if (currentY > centerY && currentX > centerX) {  
            a1 += 360;  
        }    
        return a1 - (360 - ARC_FULL_DEGREE) / 2;  
    }  
	

}
