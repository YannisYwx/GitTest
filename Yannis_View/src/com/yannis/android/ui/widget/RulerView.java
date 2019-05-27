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
	private static final float LONG_LINE_SCALE=1.0F;//���ߵĳ��ȱ�
	private static final float SHORT_LINE_SCALE=0.4F;//���ߵĳ��ȱ�
	private static final float MEDIUM_LINE_SCALE=0.7f;//���ߵĳ��ȱ�
	private static final float LINE_SCALE_FOR_HIGHT=1/4F;//���View�ߵĳ��߳���
	
	
	private static final int LINE_WIDTH=2;
	
//	private int cX;//View��X������
	private int vWidth,vHight;//View �Ŀ��
	private int stepLineNum;//һС����������
	private int maxLineNum;//����������
	private int maxScreenLineNum;//�����ߵ�����
	private int spaceWidth;//�ո�Ŀ��
	private int smallStepWidth;//һС����
	private int defaultOffset;//Ĭ��ƫ����
	private int maxOffset;//����ƽ����
	private int centerData;//�м��ֵ�Ĵ�С
	private int halfScreenLines;//һ����������������
	private float longLineLength;//���ߵĳ���
	private float textSize;//����Ĵ�С
//	private int stepLines;//һ������������
	
	private float lineScale;//�߳���
	private String content;
	
	private int maxData=1000;
	private int minData=0;
	private int stepData=10;
	private int mScaleLineColor = 0xFFFFFFFF;//�̶�����ɫ
	private int mArrowLineColor = 0xA5D448;//��ͷ��ɫ
	private String title;//����
	private String unit="";
	private String unitBig="";
	private Paint paint;
	
	private boolean isCalculateFinish=false;//�ж��Ƿ�������
	
	//Ĭ�ϵ�λ��
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
	 * ��ʼ����������Ҫ�Ķ���
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
	 * ���̶���
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
		//����ɼ���lines 
		canvas.save();
		//ʼ�հѻ����������� �����ڵ�һ��line�� ���ڼ���
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
	 * ������
	 * @param canvas
	 */
	private void drawTitle(Canvas canvas){
		
		
		//�������� ������
		int x = getWidth() / 2 - LINE_WIDTH / 2 + getScrollX();
		int y=(int) (this.vHight-longLineLength*1.8f);
		paint.setColor(mArrowLineColor);
		canvas.drawLine(x, this.vHight, x, y, paint);
		//��������
		float triangleSize = longLineLength * 0.08f;
		Path path=new Path();
		path.moveTo(x, y);
		path.lineTo(x+triangleSize, y);
		path.lineTo(x, y-triangleSize*1.5f);
		path.lineTo(x-triangleSize, y);
		path.close();
		canvas.drawPath(path, paint);
		/*������*/
		// ��ʼ�������С
		float textSizeSmall = textSize * 1.4f;
		float textSizeBig = textSize * 2.2f;
		y-=longLineLength*0.4f;
		
		if(!unit.equals("")&&!unitBig.equals("")){
			//���ֵ�λ
			int dataBig=centerData/stepData;
			int dataSmall=centerData%stepData;
			float padding = longLineLength * 0.1f;
			content=dataBig+unitBig+dataSmall+unit;
			paint.setTextSize(textSizeBig);
			
			
			//�������ֿ��,��Ϊ��Ҫ���־��У����ִ�С��һ�����������ÿ�����
			//�����뵥λ�ļ��
			
			//��һ�����ֿ��x1     ��    �ڶ������ֿ��x3
			
			String dataBigStr=String.format("%02d", dataBig);
			String dataSmallStr=String.format("%02d", dataSmall);
			
			paint.setTextSize(textSizeBig);
			float x1 = getTextRectWidth(paint,dataBigStr);
			float x3 = getTextRectWidth(paint,dataSmallStr);
			//��һ����λ�Ŀ��x2  ��    �ڶ�����λ���x4
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
	 * ������ʾ��λ��
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
	 * ����Ӱ
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
			//��ָ���¼�¼λ�� �Լ���ʼ�� velocityTracker
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
			 * scrollBy ���Ŀǰ���ƶ�����px
			 * scrollTo �ƶ���(X, Y)����
			 * event.getRawX() �������Ļ��x����
			 * event.getX();
			 */
			scrollBy((int)(pre-event.getRawX()), 0);
			pre=event.getRawX();
			break;
		case MotionEvent.ACTION_UP:
			velocityTracker.addMovement(event);
			velocityTracker.computeCurrentVelocity(1000,ViewConfiguration.get(getContext()).
					getScaledMaximumFlingVelocity());//�������ִ��һ��fling���ƶ���������ٶ�ֵ
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
	 * ��ʼ���ٶȼ����
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
	 * �ͷ��ٶȼ������Դ
	 */
	public void releaseVelocityTracker(){
		if(velocityTracker!=null){
			velocityTracker.clear();
			velocityTracker.recycle();
			velocityTracker=null;
		}
	}
	
	/**
	 * �˴����������һ�μ���  ��ͼ--���� Ȼ��ʮ�� ÿ�εĻ���ʱ��������ͬ
	 * ����������Ч����
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
	 * ���������������߾�����ʾ�����㻬����
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
	 * @param paint ����ǰ�����뱣֤paint�Ѿ����ù�textSize , Typeface;
	 * @return ��paint�������ֵ���Ӧ�߶�
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
