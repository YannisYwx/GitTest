package com.yannis.android.ui.sampleview;

import com.yannis.android.ui.Logger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;


public class RulerView extends View {
	private static final float LONG_LINE_SCALE=1.0f;//长线的长度
	private static final float MEDIUM_LINE_SCALE=0.7f;//中线的长度
	private static final float SHORT_LINE_SCALE=0.4f;//短线的长度
	private static final float LINE_SCALE=1/4F;//短线的长度
	private static final int   LINE_WIDTH=2;//线的宽度
	
	protected int mRulerColor = 0xFFFFFFFF;
	protected int mLightColor = 0xFFA3D347;


	private int w,h;
	private int sRuleCount=5;//5格
	private int maxLineCount;//一共有多少条线
	private int lineCount;//满屏时一共有多少条线
	private int spaceWidth;//每一格的宽度
	private int line_spaceWidth;//每一格和开始线的宽度
	private int defaultOffset;//默认偏移量
	private float lineLength;//长线的长度
	private float mlineLength;//中线的长度
	private float slineLength;//短线的长度
	private int maxLeftOffset;//最大的左偏移
	private int maxRightOffset;//最大的右偏移
	
	
	private int maxData=10000;
	private int minData=5000;
	private int stepData=100;
	private String[] units={"m"};
	String content;
	
	protected int[] data;// 当前选中的data

	
	
	private Paint mPaint;
	private float textSize;

	public RulerView(Context context) {
		super(context);
		init();
	}

	public RulerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public void init(){
		mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStrokeWidth(LINE_WIDTH);
		mPaint.setTextSize(textSize);

	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		//在这里计算好所有的数据
		Log.d("ywx", "onSizeChanged("+w+","+h+","+oldw+","+oldh+")");
		Log.d("ywx", "pl:"+getPaddingLeft()+" pr:"+getPaddingRight()+" pb:"+getPaddingBottom()+" pt:"+getPaddingTop());
		this.w=w-getPaddingLeft()-getPaddingRight();
		this.h=h-getPaddingBottom()-getPaddingTop();

		this.maxLineCount=((maxData-minData)/stepData)*sRuleCount*2+1;
		this.lineCount=4*2*sRuleCount+1;
		this.lineLength=this.h*LINE_SCALE;
		this.mlineLength=lineLength*MEDIUM_LINE_SCALE;
		this.slineLength=lineLength*SHORT_LINE_SCALE;
		this.spaceWidth=(this.w-lineCount*LINE_WIDTH)/(4*2*sRuleCount-1);
		this.line_spaceWidth=spaceWidth+LINE_WIDTH;
		this.defaultOffset=(w-LINE_WIDTH)/2;
		this.textSize = lineLength * 0.2f;
		this.data = getDataByOffset(getRealScroll(getScrollX()));
		this.maxRightOffset=((maxData-minData)/stepData)*sRuleCount*2*line_spaceWidth+defaultOffset-getPaddingRight();
		this.maxLeftOffset=-defaultOffset+getPaddingLeft();
		Logger.d("maxLeft:"+maxLeftOffset+" maxRight:"+maxRightOffset);
		super.onSizeChanged(this.w,this.h, oldw, oldh);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		drawTickMark(canvas);
		drawLaber(canvas);
	}
	
	private int cX=0;
	public void drawTickMark(Canvas canvas){
		int scrollX = getScrollX();
		canvas.save();
		/*
		 * 重点将画布的零点平移到中间位置 便于计算（滑动的时候零点也会平移到中间位置 ）
		 * 零点平移到第一个可见的line
		 */
		cX=defaultOffset-scrollX;
//		if(scrollX<=((maxData-minData)/stepData)*sRuleCount*2*line_spaceWidth){
//			if(cX<getPaddingLeft()){
//				cX+=getPaddingLeft()-line_spaceWidth+5;
//			}
//		}
		canvas.translate(cX, 0);
		
		Logger.d("画刻度线--当前滑动了X轴："+scrollX+"  平移坐标到("+(defaultOffset-scrollX)+",0)");
		Logger.d("viewTags","--滑动了 实际 scrollX:"+scrollX);
		Logger.d("viewTags","--滑动了 计算后 scrollX:"+scrollX);
		mPaint.setColor(mRulerColor);
		mPaint.setTextSize(textSize);
		//求出刻度线index的偏移值 ,如果还未满屏，那第一条线的index始终为0，如果已经满屏，那么出屏的距离除以每个线距+1即为第一个可见线的index
		int maxLeft=line_spaceWidth*2*sRuleCount;
		
		int i = scrollX < defaultOffset ? 0 : (scrollX - defaultOffset) /line_spaceWidth + 1;
		
		int lastShowLineNum=Math.min(lineCount+i, maxLineCount);
		float yScale=1.0f;
		Log.d("drawTickMark","defaultOffset:"+defaultOffset+" scrollX:"+scrollX+" i:"+i+" I:"+lastShowLineNum);
		boolean isStartWithZero=false;
		for(;i<lastShowLineNum;i++){
			int x=i*line_spaceWidth+scrollX;//x轴的坐标--偏移量
			
			Logger.d("i:"+i+" l_s"+lastShowLineNum+" x:"+x);
			Log.d("abc","i:"+i+"  l_s:"+line_spaceWidth+" scrollX:"+scrollX);
			if(i%sRuleCount==0){
				if(i%(sRuleCount*2)==0){
					//长线
					yScale=LONG_LINE_SCALE;
					String content=getDataByOffset(x-scrollX)[0]+"";
					content=(minData+i*(stepData/(sRuleCount*2)))+"";
					canvas.drawText(content, 
							x-getTextRectWidth(mPaint, content)/2, this.h - lineLength * 1.1f, mPaint);
				}else{
					//中线
					yScale=MEDIUM_LINE_SCALE;
				}
			}else{
				//短线
				yScale=SHORT_LINE_SCALE;
			}
			Log.d("drawTickMark","x:"+x+" Y:"+(this.h-lineLength*yScale)+" h:"+h);
			canvas.drawLine(x, h, x, this.h-lineLength*yScale, mPaint);

		}
		canvas.restore();
	}
	
	private void drawLaber(Canvas canvas){
		//计算中心 画中线
		int x=getWidth()/2-LINE_WIDTH/2+ getScrollX();
		int y=(int) (this.h-lineLength*1.8f);
		mPaint.setColor(mLightColor);
		canvas.drawLine(x, this.h, x, y, mPaint);
		//画三角形
		float triangleSize = lineLength * 0.08f;
		Path path=new Path();
		path.moveTo(x, y);
		path.lineTo(x+triangleSize, y);
		path.lineTo(x, y-triangleSize*1.5f);
		path.lineTo(x-triangleSize, y);
		path.close();
		canvas.drawPath(path, mPaint);
		//画字体
		// 初始化字体大小
		float textSizeBig = textSize * 1.4f;
		float textSizeSmall = textSize * 1.2f;
		y-=lineLength*0.3f;
		mPaint.setTextSize(textSizeSmall);
		content=units[0];
		canvas.drawText(content, x-getTextRectWidth(mPaint, content)/2, y, mPaint);
		mPaint.setTextSize(textSizeBig);
		y-=getTextHeight(mPaint);
		content=data[1]+"";
		canvas.drawText(content, x-getTextRectWidth(mPaint, content)/2, y, mPaint);
		y -= getTextHeight(mPaint);
		mPaint.setTextSize(textSizeSmall);
		content="身高";
		canvas.drawText(content, x-getTextRectWidth(mPaint, content)/2, y, mPaint);
	}
	
	/**
	 * scroll不能小于0 ， 不能大于最大数值
	 * 
	 * @param scrollX
	 * @return
	 */
	protected int getRealScroll(int scroll) {
		int maxOffset = (((maxData - minData) / stepData) * sRuleCount * 2)
				* line_spaceWidth;
		return Math.min(maxOffset, Math.max(0, scroll));
	}
	
	/**
	 * 根据偏移量获得数据
	 * @param offset
	 * @return
	 */
	public int[] getDataByOffset(int offset){
		int[] data=new int[2];
		data[0]=(offset/((sRuleCount*2)*line_spaceWidth))*stepData+minData;
		data[1]=(offset/line_spaceWidth)*(stepData/(sRuleCount*2))+minData;
		return data;
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		int scrollX=getScrollX();
		cX=defaultOffset-scrollX;
		if(scrollX<((maxData-minData)/stepData)*sRuleCount*2*line_spaceWidth){
			if(cX<getPaddingLeft()){
				scrollX=scrollX-(getPaddingLeft()-line_spaceWidth+5);
			}
			
		}
		data = getDataByOffset(getRealScroll(scrollX));
	}
	
	private float startX,pre;
	protected int mVelocity, drawCount;
	private VelocityTracker velocityTracker;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			//手指按下记录位置 以及初始化 velocityTracker
			removeCallbacks(endDraw);
			initOrResetVelocityTracker(event);
			startX=event.getRawX();
			pre=startX;
			drawCount = 1;
			break;
		case MotionEvent.ACTION_MOVE: 
			velocityTracker.addMovement(event);
			
			Logger.d("startX:"+startX+"-rawX:"+event.getRawX()+" ="+(pre-event.getRawX()));
			
			/*
			 * scrollBy 相对目前的移动多少px
			 * scrollTo 移动到(X, Y)坐标
			 * event.getRawX() 相对于屏幕的x坐标
			 */
			scrollBy((int)(pre-event.getRawX()), 0);
			
			
			
			pre=event.getRawX();
			break;
		case MotionEvent.ACTION_UP:
			velocityTracker.addMovement(event);
			velocityTracker.computeCurrentVelocity(1000,ViewConfiguration.get(getContext()).
					getScaledMaximumFlingVelocity());//获得允许执行一个fling手势动作的最大速度值
			mVelocity = (int) velocityTracker.getXVelocity();
			postDelayed(endDraw, 50);
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
		private int MAX_REDRAW_COUNT = 10;

		@Override
		public void run() {
			if (drawCount < MAX_REDRAW_COUNT && Math.abs(mVelocity) > 5000) {
				int dis = (int) (mVelocity * 0.05 / MAX_REDRAW_COUNT * (MAX_REDRAW_COUNT
						- drawCount + 0.5));
				Logger.d("Logger","--------------【"+dis+"  mVelocity:"+mVelocity);
				scrollBy(-dis, 0);
				drawCount++;
				postDelayed(endDraw, 20+drawCount*15);
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
		int left = (scroll % line_spaceWidth);
		scroll += left > line_spaceWidth * 0.5f ? line_spaceWidth - left : -left;
		int MaxScroll=((maxData-minData)/stepData)*sRuleCount*2*line_spaceWidth;
//		int MaxScroll=(50)*mRulerColor*2*line_spaceWidth;
		Logger.d("最大偏移--max-scroll:"+MaxScroll+" space:"+line_spaceWidth
				+" scroll:"+scroll);
		Logger.d("endScroll scroll:"+getScrollX()+"  scrollTo("+scroll+", 0)");
		if(scroll<0){
			scroll=0;
		}else{
			if(scroll>=MaxScroll){
				scroll=MaxScroll;
			}
		}
		Logger.d("最终 endScroll scroll:"+getScrollX()+"  scrollTo("+scroll+", 0)");

		scrollTo(scroll,0);
		

//		scroll = getRealScroll(scroll);
//		scrollTo(-scroll, 0);
		releaseVelocityTracker();
		getParent().requestDisallowInterceptTouchEvent(false);
	}
	
	/**
	 * 
	 * @param paint
	 * @param content
	 * @return
	 */
	public static float getTextRectWidth(Paint paint , String content){
		Rect rect = new Rect();
		paint.getTextBounds(content, 0,  content.length()  , rect);
		return rect.width();
	}
	
	/**
	 * 
	 * @param paint 测试前，必须保证paint已经设置过textSize , Typeface;
	 * @return 该paint画出文字的理应高度
	 */
	public static float getTextHeight(Paint paint){
		FontMetrics m = paint.getFontMetrics();
		return m.bottom - m.top;
	}
}
