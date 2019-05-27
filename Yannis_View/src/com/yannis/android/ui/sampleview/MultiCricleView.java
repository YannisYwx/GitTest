package com.yannis.android.ui.sampleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MultiCricleView extends View {
	private static final float STROKE_WIDTH = 1F / 256F, // ��߿��ռ��
			SPACE = 1F / 64F,// ��ԲСԲ�߶����˼��ռ��  
			LINE_LENGTH = 3F / 32F, // �߶γ���ռ��
			CRICLE_LARGER_RADIU = 3F / 32F,// ��Բ�뾶
			CRICLE_SMALL_RADIU = 5F / 64F,// СԲ�뾶
			ARC_RADIU = 1F / 8F,// ���뾶
			ARC_TEXT_RADIU = 5F / 32F;// ��Χ�����ְ뾶
	private Paint strokePaint,textPaint,arcPaint;

	private int size;
	private float strokeWidth;// ��߿��  
    private float ccX, ccY;// ����ԲԲ������  
    private float largeCricleRadiu, smallCricleRadiu;// ��Բ�뾶��СԲ�뾶  
    private float lineLength;// �߶γ���  
    private float space;// ��ԲСԲ�߶����˼�� 
	private float textOffsetY;

	public MultiCricleView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public MultiCricleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public MultiCricleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d("ywx", "onSizeChanged(" + w + "," + h + "," + oldw + "," + oldh
				+ ")");
		Log.d("ywx", "_Width=" + getMeasuredWidth() + ",_Hight:"
				+ getMeasuredHeight());
		Log.d("ywx", "paddingleft=" + getPaddingLeft() + ",paddingright:"
				+ getPaddingRight());
		size = w;
		calculation();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// ǿ�Ƴ���һ��
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawColor(0xFFF29B76);
		canvas.drawCircle(ccX, ccY, largeCricleRadiu, strokePaint);
		canvas.drawText("Center", ccX, ccY-textOffsetY, textPaint);
		drawTopLeft(canvas);
		drawTopRight(canvas);
		drawRight(canvas);
		drawBottom(canvas);
		drawLeft(canvas);
	}
	
	
	public void drawTopLeft(Canvas canvas){
		canvas.save();
		canvas.translate(ccX, ccY);
		canvas.rotate(-30f);
		canvas.drawLine(0, -largeCricleRadiu, 0, -largeCricleRadiu*2, strokePaint);
		canvas.drawCircle(0, -largeCricleRadiu*3, largeCricleRadiu, strokePaint);
		canvas.drawText("Lee", 0,  -largeCricleRadiu*3-textOffsetY, textPaint);
		canvas.drawLine(0, -largeCricleRadiu*4, 0, -largeCricleRadiu*5, strokePaint);
		canvas.drawCircle(0, -largeCricleRadiu*6, largeCricleRadiu, strokePaint);
		canvas.drawText("Nicholas", 0,  -largeCricleRadiu*6-textOffsetY, textPaint);
		canvas.restore();
	}
	
	public void drawLeft(Canvas canvas){
		canvas.save();
		canvas.translate(ccX, ccY);
		canvas.rotate(-105f);
		canvas.drawLine(0, -largeCricleRadiu-space, 0, -largeCricleRadiu-lineLength+space, strokePaint);
		canvas.drawCircle(0, -largeCricleRadiu-lineLength-smallCricleRadiu, smallCricleRadiu, strokePaint);
		canvas.drawText("Kitty", 0, -largeCricleRadiu-lineLength-smallCricleRadiu-textOffsetY, textPaint);
		canvas.restore();
	}
	
	public void drawRight(Canvas canvas){
		canvas.save();
		canvas.translate(ccX, ccY);
		canvas.rotate(105f);
		canvas.drawLine(0, -largeCricleRadiu-space, 0, -largeCricleRadiu-lineLength+space, strokePaint);
		canvas.drawCircle(0, -largeCricleRadiu-lineLength-smallCricleRadiu, smallCricleRadiu, strokePaint);
		canvas.drawText("Tom", 0, -largeCricleRadiu-lineLength-smallCricleRadiu-textOffsetY, textPaint);
		canvas.restore();
	}
	
	public void drawBottom(Canvas canvas){
		canvas.save();
		canvas.translate(ccX, ccY);
		canvas.rotate(180f);
		canvas.drawLine(0, -largeCricleRadiu-space, 0, -largeCricleRadiu-lineLength+space, strokePaint);
		canvas.drawCircle(0, -largeCricleRadiu-lineLength-smallCricleRadiu, smallCricleRadiu, strokePaint);
		canvas.drawText("Jeck", 0, -largeCricleRadiu-lineLength-smallCricleRadiu-textOffsetY, textPaint);
		canvas.restore();
	}
	
	public void drawTopRight(Canvas canvas){
		canvas.save();
		canvas.translate(ccX, ccY);
		canvas.rotate(30f);
		canvas.drawLine(0, -largeCricleRadiu, 0, -largeCricleRadiu*2, strokePaint);
		canvas.drawCircle(0, -largeCricleRadiu*3, largeCricleRadiu, strokePaint);
		canvas.drawText("Yannis", 0, -largeCricleRadiu*3-textOffsetY, textPaint);
		
		drawTopRightArc(canvas, -largeCricleRadiu*3);
		canvas.restore();
	}
	
	/** 
	 * �������Ͻǻ����� 
	 *  
	 * @param canvas 
	 * @param cricleY 
	 */  
	private void drawTopRightArc(Canvas canvas, float cricleY) {  
	    canvas.save();  
	  
	    canvas.translate(0, cricleY);  
	    canvas.rotate(-30);  
	  
	    float arcRadiu = size * ARC_RADIU;  
	    RectF oval = new RectF(-arcRadiu, -arcRadiu, arcRadiu, arcRadiu); 
	    //����仡
	    arcPaint.setStyle(Paint.Style.FILL);  
	    arcPaint.setColor(0x55EC6941);  
	    canvas.drawArc(oval, -22.5F, -135, true, arcPaint); 
	    //������
	    arcPaint.setStyle(Paint.Style.STROKE);  
	    arcPaint.setColor(Color.WHITE);  
	    canvas.drawArc(oval, -22.5F, -135, false, arcPaint);  
	  
	    float arcTextRadiu = size * ARC_TEXT_RADIU;  
	  
	    canvas.save();  
	    // �ѻ�����ת��������˵ķ���  
	    canvas.rotate(-135F / 2F);  
	  
	    /* 
	     * ÿ��33.75�Ƚǻ�һ���ı� 
	     */  
	    for (float i = 0; i < 5 * 33.75F; i += 33.75F) {  
	        canvas.save();  
	        canvas.rotate(i);  
	  
	        canvas.drawText("~~", 0, -arcTextRadiu, textPaint);  
	  
	        canvas.restore();  
	    }  
	  
	    canvas.restore();  
	  
	    canvas.restore();  
	}  

	public void init() {
		strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		strokePaint.setStyle(Paint.Style.STROKE);
		strokePaint.setColor(Color.WHITE);
		strokePaint.setStrokeCap(Paint.Cap.ROUND);
		
		arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		arcPaint.setStyle(Paint.Style.STROKE);
		arcPaint.setColor(Color.WHITE);
		arcPaint.setStrokeCap(Paint.Cap.ROUND);
		
		 /* 
         * ��ʼ�����ֻ��� 
         */  
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);  
        textPaint.setColor(Color.WHITE);  
        textPaint.setTextSize(30);  
        textPaint.setTextAlign(Paint.Align.CENTER);  
  
        textOffsetY = (textPaint.descent() + textPaint.ascent()) / 2; 
	}

	public void calculation() {
		// ������߿��
		strokeWidth = STROKE_WIDTH * size;

		// �����Բ�뾶
		largeCricleRadiu = size * CRICLE_LARGER_RADIU;
		smallCricleRadiu = size * CRICLE_SMALL_RADIU;
		lineLength=size*LINE_LENGTH;
		// �����ԲСԲ�߶����˼��  
        space = size * SPACE; 

		// ��������ԲԲ������
		ccX = size / 2;
		ccY = size / 2 + size * CRICLE_LARGER_RADIU;

		// ���ò���
		setPara();
	}

	/**
	 * ���ò���
	 */
	private void setPara() {
		// ������߿��
		strokePaint.setStrokeWidth(strokeWidth);
	}

}
