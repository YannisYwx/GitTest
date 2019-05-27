package com.yannis.android.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.yannis.android.ui.R;

public class TextSwithView extends View {
	private Paint paint;
	
	private float textPadding;
	private float vHight,vWidth;
	private float w_unit_left;
	private float w_unit_right;
	private float w_parting_line;
	private float w_padding;
	
	private String unit_left;
	private String unit_right;
	private String parting_line;
	private float textSize;
	private int selectTextColor;
	private int textColor;
	
	private int leftTextColor;
	private int rightTextColor;
	
	private  float textH;
	private OnTextSeclectListener listener;
	public TextSwithView(Context context) {
		super(context);
		init();
	}

	public TextSwithView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.TextSelectView);
		selectTextColor=ta.getColor(R.styleable.TextSelectView_tsSelectTextColor,  0xffef5440);
		textColor=ta.getColor(R.styleable.TextSelectView_tsTextColor,  0xffffffff);
		unit_left=ta.getString(R.styleable.TextSelectView_tsTextLeft);
		unit_right=ta.getString(R.styleable.TextSelectView_tsTextRight);
		parting_line=ta.getString(R.styleable.TextSelectView_tsTextCenter);
		textSize=ta.getDimension(R.styleable.TextSelectView_tsTextSize, 48);
		ta.recycle();
		init();
	}

	public TextSwithView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		Log.d("ywx", "onSizeChanged("+w+","+h+","+oldw+","+oldh+")");
		Log.d("ywx", "pLeft:"+getPaddingLeft()+" pRight:"+getPaddingRight()+" pBottom:"+getPaddingBottom()+" pTop:"+getPaddingTop());
		Log.d("ywx", "getWidth:"+getWidth()+" getHeight:"+getHeight());
		String content=unit_left+parting_line+unit_right;
		this.vHight=(float) (getTextHeight(paint)*1.5);
		this.textPadding=textSize*0.2f;
		this.w_padding=textPadding*2;
		
		this.vWidth=getTextRectWidth(paint, content);
		this.vWidth+=textPadding*2+w_padding*2;
		this.leftTextColor=selectTextColor;
		this.rightTextColor=textColor;
		this.w_unit_left=getTextRectWidth(paint, unit_left);
		this.w_unit_right=getTextRectWidth(paint, unit_right);
		this.w_parting_line=getTextRectWidth(paint, parting_line);
		this.textH=getTextHeight(paint);
		
		Log.d("ywx", "tsWidth:"+vWidth+" tsHeight:"+vHight);
		super.onSizeChanged((int)vWidth, (int)vHight, oldw, oldh);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		Log.d("ywx", "widthMeasureSpec:"+widthMeasureSpec+" heightMeasureSpec:"+heightMeasureSpec);
		this.textPadding=textSize*0.2f;
		this.w_padding=textPadding*2;
		String content=unit_left+parting_line+unit_right;
		this.vWidth=getTextRectWidth(paint, content);
		this.vWidth+=textPadding*2+w_padding*2;
		this.vHight=(float) (getTextHeight(paint)*1.5);

		setMeasuredDimension((int)vWidth, (int)vHight);
	}
	
	public void init(){
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(textSize);
		paint.setTextAlign(Align.CENTER);
//		setBackgroundColor(Color.CYAN);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		paint.setColor(leftTextColor);
		canvas.drawText(unit_left, w_unit_left/2+w_padding, vHight/2-(paint.ascent()+paint.descent())/2, paint);
		paint.setColor(textColor);
		canvas.drawText(parting_line, textPadding+w_unit_left+w_parting_line/2+w_padding, vHight/2-(paint.ascent()+paint.descent())/2, paint);
		paint.setColor(rightTextColor);
		canvas.drawText(unit_right, w_parting_line*2+w_unit_left+w_unit_right/2+w_padding, vHight/2-(paint.ascent()+paint.descent())/2, paint);
//		canvas.drawLine(0,  vHight/2, getMeasuredWidth(),  vHight/2, paint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			checkXY(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:
			checkXY(event.getX(), event.getY());
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	
	public void checkXY(float eventX,float eventY){
		
		float a = (paint.ascent()+paint.descent())/2;
		float textT=(vHight-textH)/2f-a;
		float textB=(vHight+textH)/2f-a;
		//left
		if(eventX>0&&eventX<=w_unit_left){
			leftTextColor=selectTextColor;
			rightTextColor=textColor;
			Toast.makeText(getContext(),unit_left, Toast.LENGTH_SHORT).show();
			if(listener!=null){
				listener.onTextSeclect(unit_left);
			}
			invalidate();
		}
		
		//right
		if(eventX>=(w_unit_left+w_parting_line+textPadding*2)&&eventX<=vWidth){
			leftTextColor=textColor;
			rightTextColor=selectTextColor;
			Toast.makeText(getContext(),unit_right, Toast.LENGTH_SHORT).show();
			if(listener!=null){
				listener.onTextSeclect(unit_right);
			}
			invalidate();
		}			
		if(eventY>=textT&&eventY<=textB){
			
		}

	}
	
	public void setListener(OnTextSeclectListener listener){
		this.listener=listener;
	}
	
	public interface OnTextSeclectListener{
		public void onTextSeclect(String text);
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
	 * @param paint
	 * @return
	 */
	public static float getTextHeight(Paint paint){
		FontMetrics m = paint.getFontMetrics();
		return m.bottom - m.top;
	}
}
