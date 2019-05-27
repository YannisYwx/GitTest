package com.yannis.android.ui.widget;

import com.yannis.android.ui.Logger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.MonthDisplayHelper;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

public class DataScrollView extends View {
	
	public static final float CENTER_CIRCLE_RADIUS_SCALE=0.35f;
	public static final float TEXTSIZE_WITH_WIDTH_SCALE=0.35f;
	
	private float w,h;//宽高
	private float centerX,centerY;//中心点
	private float radius;//圆的半径
	
	private float defaultXOffset;
	
	private int halfShowItems=2;//每边显示的个数
	private int fullScreenItems;//一屏幕显示的格式
	private float dataWidth;//一个数字的宽度
	private float textSize;//默认数字的大小
	
	private Paint paint;
	private Paint textPaint;
	
	private int minData=1;
	private int maxData=100;

	public DataScrollView(Context context) {
		super(context);
		init();
	}

	public DataScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DataScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		this.setBackgroundColor(0x77ffeedd);
		this.w=w;
		this.h=h;
		this.centerX=w/2f;
		this.centerY=h/2f;
		this.radius=h*CENTER_CIRCLE_RADIUS_SCALE;
		this.defaultXOffset=w/2f;
		this.fullScreenItems=halfShowItems*2+1;
		this.dataWidth=w/fullScreenItems*1.0f;
		this.textSize=dataWidth*TEXTSIZE_WITH_WIDTH_SCALE;
		
		
		
		paint.setStrokeWidth(radius*0.05f);
		textPaint.setTextSize(textSize);

	}
	
	public void init(){
		paint=new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.parseColor("#9f55bc"));
		
		textPaint=new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.WHITE);
		textPaint.setTextAlign(Align.CENTER);

	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		drawData(canvas);
		canvas.drawCircle(centerX+getScrollX(), centerY, radius, paint);
		canvas.drawLine(0+getScrollX(), centerY, w+getScrollX(), centerY, paint);
		
		for(int i=1;i<fullScreenItems;i++){
			canvas.drawLine(dataWidth*i+getScrollX(), 0, dataWidth*i+getScrollX(), h, paint);
		}
	}
	
	public void drawData(Canvas canvas){
		int scrollX=getScrollX();
		int dataSize=maxData-minData;
		int startIndex=(int) (defaultXOffset>scrollX?0:(scrollX-defaultXOffset)/dataWidth);
		int endIndex=Math.max(fullScreenItems+startIndex, (maxData-minData));
		if(endIndex>=dataSize){
			endIndex=dataSize;
		}
		 
		float translateX=defaultXOffset-scrollX;
		canvas.save();
		canvas.translate(translateX, 0);
		for(int i=startIndex;i<endIndex;i++){
			String text=(minData+i)+"";
			float x=dataWidth*i;
			float y=centerY-(textPaint.ascent()+textPaint.descent())/2.0f;
			canvas.drawText(text, x, y, textPaint);
		}
		canvas.restore();
		
	}
	private float startX,pre;
	protected int mVelocity, drawCount;
	private VelocityTracker velocityTracker;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
//			removeCallbacks(endDraw);
//			initOrResetVelocityTracker(event);
//			getParent().requestDisallowInterceptTouchEvent(true);
			startX=event.getRawX();
			pre=startX;
			drawCount = 1;
			break;
		case MotionEvent.ACTION_MOVE:
//			velocityTracker.addMovement(event);
			getParent().requestDisallowInterceptTouchEvent(true);

//			Logger.d("startX:"+startX+"-rawX:"+event.getRawX()+" ="+(pre-event.getRawX()));
			/*
			 * scrollBy 相对目前的移动多少px
			 * scrollTo 移动到(X, Y)坐标
			 * event.getRawX() 相对于屏幕的x坐标
			 * event.getX();
			 */
			scrollBy((int)(pre-event.getRawX()), 0);
			pre=event.getRawX();
			Logger.d("abc","scroll:"+getScrollX());
			break;
		case MotionEvent.ACTION_UP:
			endScroll();
//			velocityTracker.addMovement(event);
//			velocityTracker.computeCurrentVelocity(1000,ViewConfiguration.get(getContext()).
//					getScaledMaximumFlingVelocity());//获得允许执行一个fling手势动作的最大速度值
//			mVelocity = (int) velocityTracker.getXVelocity();
//			postDelayed(endDraw, 50);
			break;
		case MotionEvent.ACTION_CANCEL:
			endScroll();
			break;

		default:
			break;
		}
		return true;
	}
	
	/**
	 * 初始化速度监测器
	 * @param event
	 */
	public void initOrResetVelocityTracker(MotionEvent event){
		if(velocityTracker==null){
			velocityTracker=VelocityTracker.obtain();
			velocityTracker.addMovement(event);
		}else{
			velocityTracker.clear();
		}
	}
	
	/**
	 * 释放速度监测器资源
	 */
	public void releaseVelocityTracker(){
		if(velocityTracker!=null){
			velocityTracker.clear();
			velocityTracker.recycle();
			velocityTracker=null;
		}
	}
	
	/**
	 * 此处将进行最后一次计算  画图--测速 然后画十次 每次的画的时间间隔都不同
	 * 产生滑动的效果！
	 */
	private Runnable endDraw=new Runnable() {
		private int MAX_REDRAW_COUNT = 8;

		@Override
		public void run() {
			if (drawCount < MAX_REDRAW_COUNT && Math.abs(mVelocity) > 5000) {
				int dis = (int) (mVelocity * 0.05 / MAX_REDRAW_COUNT * (MAX_REDRAW_COUNT
						- drawCount + 0.5));
				scrollBy(-dis, 0);
				drawCount++;
				postDelayed(endDraw, 20+drawCount*10);
			} else {
				endScroll();
			}
		}
	};
	
	/**
	 * 滑动结束用于中线居中显示，计算滑动的
	 */
	public void endScroll(){
		int scroll = getScrollX();
		int left = (int) (scroll % dataWidth);
		scroll += left > (dataWidth /2f )? dataWidth/1f - left : -left;
		int MaxScroll=(int) ((maxData-minData+1)*dataWidth);
		Logger.d("abc", "END--scroll:"+scroll+"  dataWidth:"+dataWidth+" left:"+left);
		if(scroll<0){
			scroll=0;
		}else{
			if(scroll>=MaxScroll){
				scroll=MaxScroll;
			}
		}
		Logger.d("abc", "END--scrollTo:"+scroll);

		scrollTo(scroll,0);
		releaseVelocityTracker();
		getParent().requestDisallowInterceptTouchEvent(false);
	 }

}
