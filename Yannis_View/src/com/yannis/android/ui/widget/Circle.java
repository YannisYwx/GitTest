package com.yannis.android.ui.widget;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import com.yannis.android.ui.R;

@SuppressLint("NewApi")
public class Circle extends FrameLayout {
	
	
	private static final int LABLE_TEXT_SIZE = 30;//�������������������С
	
	public static  String UNIT_TEXT = "��";
	
	private static int text_size = 80;//���µ������С
	
	private static  int unit_text_size = 40;//���µ�λ�������С
	
	public static  float MAX_TEMPERATURE = 75f; //���·�Χ����
	
	private static final float MIN_TEMPERATURE = 0F;//���·�Χ����
	
	private static final float ARC_SMALL_WIDTH = 3;//С��Ȧ���
	
	private static final float TEXT_SIZE_SCALE = 1 / 6F;//����Ĵ�С��
	
	private static final float ARC_BIG_WIDTH_SCALE = 1 / 12F;//���Ȧ��ȱ�
	
	private static final float CIRCLE_GREY_WIDTH_SCALE = 1 / 8F;//��ɫ����ȱ�
	
	private static final float ARC_SMALL_RADIUS_SCALE = 8 / 12f;//С��Ȧ�İ뾶��
	
	private int[] COLORS = {0xFF69E1B5 , 0xFFC5FB7F,  0xFFFF9557};//��Ȧ����ɫ
	
	private int GREY_CIRCLE_COLOR = Color.LTGRAY;//��ɫȦ��ɫ

	private static final int TEXT_COLOR = 0xFFB5D271;//����ֵ��������ɫ
	
	private static final int LABLE_TEXT_COLOR = 0xFFFFFFFF;//��������������������ɫ
	
	private static final int LABLE_TEXT_BG = 0xFF30D8C1;//�������������ı���ɫ
	
	private float START_DEGREE = 135;//��Ȧ��ʼ�Ƕ�
	
	private float SWEEP_DEGREE = 270;//��Ȧɨ���ĽǶ�
	
	private float arc_small_radius;
	
	private float circle_grey_radius;
	
	private float arc_big_width;
	
	private float circle_grey_width;
	
	private int size;
	
	private Paint paint;
	
	private RectF rectF;
	
	private Bitmap thumb ; 
	
	private Matrix matrix;//
	
	private SweepGradient gradient;
	
	public float temperature = MIN_TEMPERATURE;//�ж�������Ҫ��˽��
	
	private String lableText = "��������";
	
	private int lableTextPadding = 10;
	
	public Circle(Context context) {
		super(context);
		init();
	}
	
	public Circle(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public Circle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init()
	{
		setWillNotDraw(false);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.STROKE);
		
		thumb = BitmapFactory.decodeResource(getResources(), R.drawable.thumb);
		rectF = new RectF();
		
		matrix = new Matrix();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
		setMeasuredDimension(size, size);//������� setMeasuredDimension(int,int)���洢���View���������õ���measured width and height
	}
		
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		size = w;
		text_size=(int) (TEXT_SIZE_SCALE*size);
		unit_text_size=(int) (TEXT_SIZE_SCALE*size)/2;
		gradient = new SweepGradient(size/2, size / 2, COLORS, new float[]{0.0f  , (1 - (START_DEGREE - 90) / 180f) / 2 , 1 - (START_DEGREE - 90) / 180f});
		
		
		arc_big_width = ARC_BIG_WIDTH_SCALE * size;
		circle_grey_width = CIRCLE_GREY_WIDTH_SCALE * size;
		
		arc_small_radius = ARC_SMALL_RADIUS_SCALE * size / 2;
		circle_grey_radius = size / 2 - circle_grey_width / 2;
		
		//���Ű�ť����ť�ߴ� = (���Ȧ�ڱ�Ե - С��Ȧ������) * 2 - n --------(����ť������Ȧ�ڱ�Ե��϶Ϊ  n / 2)
		int dstW = (int) (size/2 - circle_grey_width  - arc_small_radius) * 2 - 6;
		Log.v(VIEW_LOG_TAG, "dstW = " + dstW + "---arc_big_radius = " + (size/2 - circle_grey_width) + "---arc_big_width / 2 = " + (arc_big_width / 2) + "----arc_small_radius = " + arc_small_radius);
		thumb = Bitmap.createScaledBitmap(thumb, dstW, dstW, false);
		matrix.setRotate(90);
		thumb = Bitmap.createBitmap(thumb, 0, 0, thumb.getWidth(), thumb.getHeight(), matrix, false);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(0xFFFFFFFF);//������ɫ
		drawGreyCircle(canvas);
		drawColorArc(canvas);
		drawTemperatuerText(canvas);
//		drawLableText(canvas);
	}
	
	

	private void drawLableText(Canvas canvas) {
		//�������ʾ����  -_-!û������壬��������
		paint.setTextSize(LABLE_TEXT_SIZE);
		paint.setColor(LABLE_TEXT_BG);
		float textWidth = paint.measureText(lableText);
		float textHeight = (paint.descent() - paint.ascent())  + 2 * lableTextPadding;//�������µ�padding
		rectF.bottom = size / 2 + arc_small_radius - textHeight * 2 / 3;//���λ���Ƕ����弰����������λ�õģ����������޸�
		rectF.top = rectF.bottom - textHeight;
		rectF.left = size / 2 - textWidth / 2 - lableTextPadding;
		rectF.right = size / 2 + textWidth / 2 + lableTextPadding;
		canvas.drawRoundRect(rectF, rectF.height() / 2, rectF.height() / 2, paint);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(LABLE_TEXT_COLOR);
		canvas.drawText(lableText, size / 2, rectF.centerY() - (paint.descent() + paint.ascent())/2, paint);
	}

	private void drawTemperatuerText(Canvas canvas) 
	{
		//д�� , ����λ��������
		paint.setTextSize(unit_text_size);//����x��ƫ��������ݵ�λ�������С
		float offsetCenterX = paint.measureText(UNIT_TEXT) / 2;
		
		paint.setColor(TEXT_COLOR);
		paint.setShader(null);
		paint.setStyle(Style.FILL);
		paint.setTypeface(Typeface.SANS_SERIF);
		paint.setTextSize(text_size);
		paint.setTextAlign(Align.CENTER);
		String tempText = String.format("%.1f", temperature);
		FontMetrics  metrics = paint.getFontMetrics();
		float offsetCenterY = Math.abs(metrics.descent + metrics.ascent) / 2;//����y��ƫ�����������������С
//		canvas.drawLine(0, size / 2, size, size / 2, paint);//�ο���
		canvas.drawText(tempText, size / 2 - offsetCenterX , size / 2 + offsetCenterY, paint);
		
		//д��λ
		paint.setTextSize(unit_text_size);
		paint.setTextAlign(Align.LEFT);
		canvas.drawText(UNIT_TEXT, size / 2 + paint.measureText(tempText) / 2 + offsetCenterX, size /2 + offsetCenterY, paint);

	}

	/**
	 * ���ʻ�
	 * @param canvas
	 */
	private void drawColorArc(Canvas canvas) 
	{
		canvas.save();
		canvas.rotate(START_DEGREE , size / 2 , size / 2);
		
		//�����Ȧ
		float dis = circle_grey_width  -  arc_big_width / 2;
		rectF.bottom = size  - dis;
		rectF.left = dis;
		rectF.right = size - dis;
		rectF.top = dis;
		paint.setShader(gradient);
		paint.setStrokeWidth(arc_big_width);
		canvas.drawArc(rectF, 1, SWEEP_DEGREE, false, paint);
		
		//��С�ʻ�
		dis = size / 2  -  arc_small_radius;
		rectF.bottom = size  - dis;
		rectF.left = dis;
		rectF.right = size - dis;
		rectF.top = dis;
		paint.setStrokeWidth(ARC_SMALL_WIDTH);
		canvas.drawArc(rectF, 1, SWEEP_DEGREE, false, paint);
		canvas.restore();
		
		//����ť
		canvas.save();
		float degree = START_DEGREE  + (temperature - MIN_TEMPERATURE)/(MAX_TEMPERATURE - MIN_TEMPERATURE) * SWEEP_DEGREE;
		canvas.rotate(  degree , size / 2 , size / 2);
		canvas.translate(size / 2 + arc_small_radius, size / 2 );
		canvas.drawBitmap(thumb, -thumb.getWidth() / 2, -thumb.getHeight() / 2, paint);
		canvas.restore();
		
	}

	/**
	 * ����ɫȦ
	 * @param canvas
	 */
	private void drawGreyCircle(Canvas canvas) 
	{
		paint.setShader(null);
		paint.setStyle(Style.STROKE);
		paint.setColor(GREY_CIRCLE_COLOR);
		paint.setStrokeWidth(circle_grey_width);
		canvas.drawCircle(size / 2,  size / 2, circle_grey_radius, paint);
	}

	
	
	/* ***********************************************************
	 * 						getter & setter
	 * ***********************************************************/
	
	
	public float getTemperature() 
	{
		return temperature;
	}

	public void setTemperature(float temperature) 
	{
		float temp = Math.min(Math.max(temperature, MIN_TEMPERATURE), MAX_TEMPERATURE);
		if(temp != this.temperature)
		{
			this.temperature = temperature;
			invalidate();
		}
	}

	public String getLableText() 
	{
		return lableText;
	}

	public void setLableText(String lableText) 
	{
		this.lableText = lableText;
		invalidate();
	}
	
	public void startAnim(float temperature)
	{
//		ValueAnimator.ofFloat(MIN_TEMPERATURE , temperature).setDuration(1000).start();
		ObjectAnimator.ofFloat(this, "temperature", MIN_TEMPERATURE , temperature).setDuration(1000).start();
	}

	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,int bottom) {
		View v = getChildAt(0);
		if(v==null){
			return;
		}
		int b1 = (int) (size / 2 + arc_small_radius - v.getMeasuredHeight() * 2 / 3);//���λ���Ƕ����弰����������λ�õģ����������޸�
		int t1 = b1 - v.getMeasuredHeight();
		int l1 = size / 2 - v.getMeasuredWidth() / 2 - lableTextPadding;
		int r1 = size / 2 + v.getMeasuredWidth() / 2 + lableTextPadding;
		
		v.layout(l1, t1, r1, b1);
		
//		v.layout(50, 50, v.getMeasuredWidth() + 50, v.getMeasuredHeight() + 50);
	}
	
	
	

}
