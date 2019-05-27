package com.yannis.android.ui.widget;

import java.util.Random;

import com.yannis.android.ui.test.ViewUtil;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ProgressView extends View {
	
	public static final float CIRCLE_RADIUS_SCALE=0.68f;
	
	public static final float LINE_WIDTH_SCALE=0.08f;
	
	public static final float LINE_LENGHT_SCALE=0.18f;
	
	public static final int ARC_FULL_DEGREE =300;
	
	private static final int[] SWEEP_COLORS = {0xFF00ffff , 0xFF46ABDD , 0xFFC04276 , 0xFFEBC93D};

	
	private int color_line_progress=Color.GREEN;//当前进度的线的颜色
	private int color_line=Color.GRAY;//正常的线的颜色
	private int color_circle=0x55FFFFFF;//中间球的颜色
	private int color_text=Color.WHITE;//字体的颜色
	
	private String title="点击优化";//下标题
	private String unit="分";
	
	private int lines_total=100;
	
	private int w;
	private int cX,cY;
	private float lineWidth;
	private float lineLength;
	private float circleRadius;
	private float textSize;
	
	private Paint paint;
	private Paint textPaint;
	
	private int progress=60;
	
	
	
	public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public ProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ProgressView(Context context) {
		super(context);
		init();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d("ywx", "onSizeChanged("+w+","+h+","+oldw+","+oldh+")");
		Log.d("ywx", "pLeft:"+getPaddingLeft()+" pRight:"+getPaddingRight()+" pBottom:"+getPaddingBottom()+" pTop:"+getPaddingTop());
		Log.d("ywx", "getWidth:"+getWidth()+" getHeight:"+getHeight());
		this.w=w;
		this.cX=w/2;
		this.cY=w/2;
		this.lineLength=cX*LINE_LENGHT_SCALE;
		this.lineWidth=lineLength*LINE_WIDTH_SCALE;
		this.circleRadius=cX*CIRCLE_RADIUS_SCALE;
		this.textSize=circleRadius*0.65f;
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
		setMeasuredDimension(size, size);//必须调用 setMeasuredDimension(int,int)来存储这个View经过测量得到的measured width and height
	}
	
	public void init(){
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color_circle);
		paint.setStyle(Style.FILL);
		
		textPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(color_text);
		textPaint.setTextAlign(Align.CENTER);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawCircle(cX, cY, circleRadius, paint);
		drawLines(canvas);
		drawText(canvas);
	}
	
	public void drawLines(Canvas canvas){
		paint.setStrokeWidth(lineWidth);
		for(int i=0;i<lines_total;i++){
			if(i<progress){
				if(i < 25)//从0x00ffff -->0x46ABDD
				{
					paint.setColor(ViewUtil.getColorBetweenAB(SWEEP_COLORS[0], SWEEP_COLORS[1], 25, i));
				}
				else if(i < 50)//从0x46ABDD-->0xC04276
				{
					paint.setColor(ViewUtil.getColorBetweenAB(SWEEP_COLORS[1], SWEEP_COLORS[2], 25, i - 25));
				}
				else if(i < 75)//从0xC04276-->0xEBC93D
				{
					paint.setColor(ViewUtil.getColorBetweenAB(SWEEP_COLORS[2], SWEEP_COLORS[3], 25, i - 50));
				}
				else//0xEBC93D --> 0x00ffff
				{
					paint.setColor(ViewUtil.getColorBetweenAB(SWEEP_COLORS[3], SWEEP_COLORS[0], 25, i - 75));
				}
			}else{
				paint.setColor(color_line);
			}
			/*-------------竖直方向 为标准的计算------------------*/
			//上边 90度竖直线为准
			//+degress 逆时针210=180+(360-300)/2
//			float degrees=ARC_FULL_DEGREE/100f*i+210;
			//-degress 顺时针
			float degrees=ARC_FULL_DEGREE/100f*i-150;
			canvas.save();
			canvas.rotate(degrees, w/2, w/2);
			float startX=w/2;
			float startY=0;
			float stopX=w/2;
			float stopY=lineLength;
			canvas.drawLine(startX, startY, stopX, stopY, paint);
			canvas.restore();
			/*-------------水平方向 为标准的计算------------------*/
			//右边的水平线为准 0
			/*float degrees=ARC_FULL_DEGREE/100f*i+120;
			canvas.save();
			canvas.rotate(degrees, w/2, w/2);
			float startX=w-lineLength;
			float startY=w/2;
			float stopX=w;
			float stopY=w/2;
			canvas.drawLine(startX, startY, stopX, stopY, paint);
			canvas.restore();*/
			
			//底部 270竖直线为准
			/*float degrees=ARC_FULL_DEGREE/100f*i+30;
			canvas.save();
			canvas.rotate(degrees, w/2, w/2);
			float startX=w/2;
			float startY=w-lineLength;
			float stopX=w/2;
			float stopY=w;
			canvas.drawLine(startX, startY, stopX, stopY, paint);
			canvas.restore();*/
			
			//左边的水平线为准 180
			/*float degrees=ARC_FULL_DEGREE/100f*i-60;
			canvas.save();
			canvas.rotate(degrees, w/2, w/2);
			float startX=0;
			float startY=w/2;
			float stopX=lineLength;
			float stopY=w/2;
			canvas.drawLine(startX, startY, stopX, stopY, paint);
			canvas.restore();*/
		}
	}
	
	public void drawText(Canvas canvas){
		textPaint.setTextSize(textSize);
		float progressTextH=ViewUtil.getTextHeight(textPaint);
		float progressTextW=ViewUtil.getTextRectWidth(textPaint, progress+"");
		float progressTextB=((textPaint.ascent()+textPaint.descent())/2);
		float progressTextY=(cY-progressTextB)*0.95f;
		canvas.drawText(progress+"", cX, progressTextY, textPaint);
		textPaint.setTextSize(textSize*0.2f);
		float unitY=progressTextY-progressTextH/2-((textPaint.ascent()+textPaint.descent())/2);
		float unitX=cX+(progressTextW/2)*1.35f;
		canvas.drawText(unit, unitX, unitY, textPaint);
		textPaint.setTextSize(textSize*0.28f);
		canvas.drawText(title, cX, cY+(progressTextH/2f)*1.5f+((textPaint.ascent()+textPaint.descent())/2), textPaint);
	}
	
	float x,y;
	boolean isDownInCircle=false;
	boolean isUpInCircle=false;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d("yannis", "MotionEvent.ACTION_DOWN:");
			isDownInCircle=false;
			isUpInCircle=false;
			x=event.getX();
			y=event.getY();
			if(x>=cX-circleRadius&&x<=cX+circleRadius&&y>=cY-circleRadius&&y<=cY+circleRadius){
				isDownInCircle=true;
				Log.d("yannis", "isDownInCircle=true");
			}
			break;
		case MotionEvent.ACTION_MOVE:
			
			break;
		case MotionEvent.ACTION_UP:
			Log.d("yannis", "MotionEvent.ACTION_UP:");
			isUpInCircle=false;
			x=event.getX();
			y=event.getY();
			if(x>=cX-circleRadius&&x<=cX+circleRadius&&y>=cY-circleRadius&&y<=cY+circleRadius){
				if(isDownInCircle){
					isUpInCircle=true;	
					Log.d("yannis", "isUpInCircle=true:");
				}
			}
			startProgressByRandom(isUpInCircle);
			break;

		default:
			break;
		}
		
		return true;
	}
	
	public void startProgressByRandom(boolean isStart){
		if(isStart){
			int randomProgress=new Random().nextInt(101);
			ObjectAnimator.ofInt(this, "progress", progress,randomProgress).setDuration(Math.abs((randomProgress-progress))*30).start();
			progress=randomProgress;			
		}
	}
	
	public void setProgress(int p){
		Log.d("yannis", "progress:"+p);
		this.progress=p;
		invalidate();
	}
	
	public void startProgress(int progress){
		ObjectAnimator.ofInt(this, "progress", this.progress,progress).setDuration(Math.abs((progress-this.progress))*30).start();
		this.progress=progress;
	}
	
}
