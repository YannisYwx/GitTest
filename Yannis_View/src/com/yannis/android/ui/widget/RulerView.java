package com.yannis.android.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import com.yannis.android.ui.R;

@SuppressLint("ClickableViewAccessibility")
public class RulerView extends View {
	private static final float LONG_LINE_SCALE=1.0F;//长线的长度比
	private static final float SHORT_LINE_SCALE=0.4F;//短线的长度比
	private static final float MEDIUM_LINE_SCALE=0.7f;//中线的长度比
	private static final float LINE_SCALE_FOR_HIGHT=1/4F;//相对View高的长线长度
	
	
	private static final int LINE_WIDTH=2;
	
//	private int cX;//View的X轴中心
	private int vWidth,vHight;//View 的宽高
	private int stepLineNum;//一小步多少条线
	private int maxLineNum;//最大多少条线
	private int maxScreenLineNum;//满屏线的数量
	private int spaceWidth;//空格的宽度
	private int smallStepWidth;//一小格宽度
	private int defaultOffset;//默认偏移量
	private int maxOffset;//最大的平移量
	private int centerData;//中间的值的大小
	private int halfScreenLines;//一屏中最多多少条数据
	private float longLineLength;//长线的长度
	private float textSize;//字体的大小
//	private int stepLines;//一步多少条短线
	
	private float lineScale;//线长比
	private String content;
	
	private int maxData=1000;
	private int minData=0;
	private int stepData=10;
	private int mScaleLineColor = 0xFFFFFFFF;//刻度线颜色
	private int mArrowLineColor = 0xA5D448;//箭头颜色
	private String title;//标题
	private String unit="";
	private String unitBig="";
	private Paint paint;
	
	private boolean isCalculateFinish=false;//判断是否计算完成
	
	//默认的位置
	private int defaultRulerScrollX=0;
	private int defaultRulerData=0;
	
	
	
	private OnDataSelectedListener listener;
	
	public RulerView(Context context) {
		super(context);
		init();
	}

	public RulerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.RulerView);
		mScaleLineColor=ta.getColor(R.styleable.RulerView_scaleLineColor,  0xFFFFFFFF);
		mArrowLineColor=ta.getColor(R.styleable.RulerView_arrowLineColor,  0xA5D448);
		maxData=ta.getInteger(R.styleable.RulerView_maxData,100);
		minData=ta.getInteger(R.styleable.RulerView_minData,0);
		stepData=ta.getInteger(R.styleable.RulerView_stepData,10);
		halfScreenLines=ta.getInteger(R.styleable.RulerView_halfScreenLines,20);
		stepLineNum=ta.getInt(R.styleable.RulerView_stepLineNum, 5);
		unit=ta.getString(R.styleable.RulerView_unit);
		unitBig=ta.getString(R.styleable.RulerView_unitBig);
		title=ta.getString(R.styleable.RulerView_title);
		
		if(unit==null||unit.equals("")){
			unit="";
		}
		if(unitBig==null||unitBig.equals("")){
			unitBig="";
		}
		if(title==null||title.equals("")){
			title="";
		}
		
		ta.recycle();
		init();
	}

	public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d("ywx", "onSizeChanged("+w+","+h+","+oldw+","+oldh+")");
		Log.d("ywx", "pLeft:"+getPaddingLeft()+" pRight:"+getPaddingRight()+" pBottom:"+getPaddingBottom()+" pTop:"+getPaddingTop());
		Log.d("ywx", "getWidth:"+getWidth()+" getHeight:"+getHeight());
		
		this.vWidth=w-getPaddingLeft()-getPaddingRight();
		this.vHight=h-getPaddingBottom()-getPaddingTop();
		this.longLineLength=this.vHight*LINE_SCALE_FOR_HIGHT;
		this.maxLineNum=((maxData-minData)/stepData)*(stepLineNum*2)+1;
		this.maxScreenLineNum=halfScreenLines*2+1;
		this.spaceWidth=(this.vWidth-this.maxScreenLineNum*LINE_WIDTH)/(this.maxScreenLineNum-1);
		this.smallStepWidth=this.spaceWidth+LINE_WIDTH;
		this.defaultOffset=w/2-LINE_WIDTH/2;
		this.maxOffset=(this.maxLineNum-1)*smallStepWidth;
		this.textSize=this.longLineLength*0.2f;
		this.centerData=minData;
		this.isCalculateFinish=true;
		setData(defaultRulerData);
	}
	
	/**
	 * 初始化画布中需要的东西
	 */
	public void init(){
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStrokeWidth(LINE_WIDTH);
		paint.setTextAlign(Align.CENTER);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		drawLines(canvas);
		drawTitle(canvas);
		drawShader(canvas);
	}
	
	/**
	 * 画刻度线
	 * @param canvas
	 */
	public void drawLines(Canvas canvas){
		int scrollX=getScrollX();
		int startIndex,endIndex;
		startIndex=defaultOffset>scrollX?0:(scrollX-defaultOffset)/smallStepWidth+1;
		endIndex=Math.max((maxScreenLineNum+startIndex), maxLineNum);
		if(endIndex>=maxLineNum){
			endIndex=maxLineNum;
		}
		//计算可见的lines 
		canvas.save();
		//始终把画布的零坐标 放置在第一个line上 便于计算
		int zero=defaultOffset-scrollX;
		
		canvas.translate(zero, 0);		
		lineScale=1.0f;
		for(int i=startIndex;i<endIndex;i++){
			int x=i*smallStepWidth+scrollX;
			int sreenX=zero+i*smallStepWidth;
			if(sreenX<getPaddingLeft()||sreenX>(getWidth()-getPaddingRight())){
				paint.setColor(Color.TRANSPARENT);
			}else{
				paint.setColor(mScaleLineColor);
				paint.setTextSize(textSize);
			}
			
			if(i%stepLineNum==0){
				if(i%(stepLineNum*2)!=0){
					lineScale=MEDIUM_LINE_SCALE;
				}else{
					lineScale=LONG_LINE_SCALE;
					canvas.drawText(getText(i), x, this.vHight-longLineLength*1.1f, paint);
				}
			}else{
				lineScale=SHORT_LINE_SCALE;
			}
			canvas.drawLine(x, this.vHight, x, this.vHight-longLineLength*lineScale, paint);
		}
		canvas.restore();
	}
	
	/**
	 * 画文字
	 * @param canvas
	 */
	private void drawTitle(Canvas canvas){
		
		
		//计算中心 画中线
		int x = getWidth() / 2 - LINE_WIDTH / 2 + getScrollX();
		int y=(int) (this.vHight-longLineLength*1.8f);
		paint.setColor(mArrowLineColor);
		canvas.drawLine(x, this.vHight, x, y, paint);
		//画三角形
		float triangleSize = longLineLength * 0.08f;
		Path path=new Path();
		path.moveTo(x, y);
		path.lineTo(x+triangleSize, y);
		path.lineTo(x, y-triangleSize*1.5f);
		path.lineTo(x-triangleSize, y);
		path.close();
		canvas.drawPath(path, paint);
		/*画字体*/
		// 初始化字体大小
		float textSizeSmall = textSize * 1.4f;
		float textSizeBig = textSize * 2.2f;
		y-=longLineLength*0.4f;
		
		if(!unit.equals("")&&!unitBig.equals("")){
			//两种单位
			int dataBig=centerData/stepData;
			int dataSmall=centerData%stepData;
			float padding = longLineLength * 0.1f;
			content=dataBig+unitBig+dataSmall+unit;
			paint.setTextSize(textSizeBig);
			
			
			//计算文字宽度,因为需要文字居中，文字大小不一，故依依算出每个宽度
			//数字与单位的间距
			
			//第一个数字宽度x1     和    第二个数字宽度x3
			
			String dataBigStr=String.format("%02d", dataBig);
			String dataSmallStr=String.format("%02d", dataSmall);
			
			paint.setTextSize(textSizeBig);
			float x1 = getTextRectWidth(paint,dataBigStr);
			float x3 = getTextRectWidth(paint,dataSmallStr);
			//第一个单位的宽度x2  和    第二个单位宽度x4
			paint.setTextSize(textSizeSmall);
			float x2 = getTextRectWidth(paint, unitBig);
			float x4 = getTextRectWidth(paint, unit);
			float totalX = x1 +padding  + x2 + padding + x3 + padding + x4;
			totalX=totalX/2f;
			
			paint.setTextAlign(Align.LEFT);
			paint.setTextSize(textSizeBig);
			
			canvas.drawText(dataBigStr, x - totalX , y, paint);
			canvas.drawText(dataSmallStr, x - totalX  + x1 + padding + x2 + padding, y, paint);
			
			paint.setTextSize(textSizeSmall);
			canvas.drawText(unitBig, x - totalX + x1 + padding, y, paint);
			canvas.drawText(unit, x - totalX + x1 + padding + x2 + padding + x3 + padding, y, paint);
						
			
			
			if(!title.equals("")){
				paint.setTextAlign(Align.CENTER);
				y -= getTextHeight(paint)*1.3f;
				paint.setTextSize(textSizeSmall);
				content=title;
				canvas.drawText(content, x, y, paint);				
			}
		}else{
			if((!unit.equals("")&&unitBig.equals(""))||(unit.equals("")&&!unitBig.equals(""))){
				if(!unit.equals("")){
					content=unit;
				}else{
					content=unitBig;
				}
				paint.setTextSize(textSizeSmall);
				canvas.drawText(content, x, y, paint);
				y -= getTextHeight(paint);
				paint.setTextSize(textSizeBig);
				content=centerData+"";
				canvas.drawText(content, x, y, paint);
				
				if(!title.equals("")){
					y -= getTextHeight(paint);
					paint.setTextSize(textSizeSmall);
					content=title;
					canvas.drawText(content, x, y, paint);
				}
			}else{
				paint.setTextSize(textSizeBig);
				content=centerData+"";
				canvas.drawText(content, x, y, paint);
				if(!title.equals("")){
					y -= getTextHeight(paint);
					paint.setTextSize(textSizeSmall);
					content=title;
					canvas.drawText(content, x, y, paint);
				}
			}
		}
	}
	
	/**
	 * 设置显示的位置
	 * @param data
	 */
	public void setData(int data){
		this.defaultRulerData=data;
		int dataOffset=defaultRulerData-minData;
		if(dataOffset<0){
			dataOffset=0;
		}
		int steps=dataOffset/(stepData/(stepLineNum*2));
		defaultRulerScrollX=steps*smallStepWidth;
		
		if(isCalculateFinish){
			scrollTo(defaultRulerScrollX,0);
		}		
	}
	
	public int getCenterData(){
		return centerData;
	}
	
	/**
	 * 画阴影
	 * @param canvas
	 */
	private void drawShader(Canvas canvas){
		int x = computeHorizontalScrollOffset();
		int [] colors={0xFF272727,0xaa272727, 0x10272727,0xaa272727,0xFF272727};
		float [] positions={0.0f,0.3f,0.5f,0.7f,1.0f};
		Shader shader=new LinearGradient(x, 0,x+getWidth(), 0,
				colors, positions, TileMode.CLAMP);
		paint.setShader(shader);
		canvas.drawRect(x, this.vHight-longLineLength*1.5f,
				x+getWidth(), this.vHight, paint);
		paint.setShader(null);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		centerData=getDataByOffsetX(getRealOffset(getScrollX()));
		if(listener!=null){
			listener.OnDataSelected(centerData);
		}
	}
	
	public int getDataByOffsetX(int offset){
		Log.d("getDataByOffsetX", "offset:"+offset+"  smallStepWidth:"+smallStepWidth+" stepLineNum:"+stepLineNum);
		return (offset/smallStepWidth)*(stepData/(stepLineNum*2))+minData;
	}
	
	public int getRealOffset(int scrollX){
		return Math.min(maxOffset, Math.max(0, scrollX));
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
			getParent().requestDisallowInterceptTouchEvent(true);
			startX=event.getRawX();
			pre=startX;
			drawCount = 1;
			break;
		case MotionEvent.ACTION_MOVE: 
			velocityTracker.addMovement(event);
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
		int left = (scroll % smallStepWidth);
		scroll += left > smallStepWidth * 0.5f ? smallStepWidth - left : -left;
		int MaxScroll=((maxData-minData)/stepData)*stepLineNum*2*smallStepWidth;
		if(scroll<0){
			scroll=0;
		}else{
			if(scroll>=MaxScroll){
				scroll=MaxScroll;
			}
		}
		scrollTo(scroll,0);
		releaseVelocityTracker();
		getParent().requestDisallowInterceptTouchEvent(false);
	 }
	
	public String getText(int index){
		if(!unitBig.equals("")&&!unit.equals("")){
			return (index*(stepData/(stepLineNum*2))+minData)/stepData+"";
		}else{
			return (index*(stepData/(stepLineNum*2))+minData)+"";
		}
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
	
	public interface OnDataSelectedListener{
		public void OnDataSelected(int data);
	}
	
	public void setListener(OnDataSelectedListener listener){
		this.listener=listener;
	}
	

}
