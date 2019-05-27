package com.yannis.android.ui.test;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yannis.android.ui.R;

public class SportPieView extends RelativeLayout {
	
	private static final float SPACE_SCALE = 3 / 460f;
	
	private static final float PIE_RADIUS_SCALE = 400 / 2 / 460f; 
	
	private float space , pie_radius , ring_width;//环和圆的间隔，圆的半径，环的宽度

	private int pie_color;
	
	private int bg_color;
		
	private Paint paint;
	
	private Paint ringPaint;//画环
	
	private Paint redPaint;//画圆点
	
	private SweepGradient gradient;
	
	private int[] SWEEP_COLOR = {0x00F55641 , 0xFFF55641, 0x00F55641 };
	
	private int progress;
	
	private int w , h ; 
	
	private RectF rectF;
	
	private TextView stepView , goalView , percentView; 
	private ImageView imageview;
	
	private int goal = 12000;
	
	private int steps;
	
	public SportPieView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.sport_pie_view, this);
		stepView = (TextView) findViewById(R.id.data);
		goalView = (TextView) findViewById(R.id.goal);
		percentView = (TextView) findViewById(R.id.percent);
		imageview = (ImageView) findViewById(R.id.stateImg);
		
		
		
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SportPieView);
		pie_color = a.getColor(R.styleable.SportPieView_pieColor, 0xFFF55641);
		bg_color = a.getColor(R.styleable.SportPieView_bgColor, 0x22000000);
		SWEEP_COLOR[1] = pie_color;
		a.recycle();
		setWillNotDraw(false);
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Style.FILL);
		rectF = new RectF();
		
		ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		ringPaint.setStyle(Style.STROKE);
		ringPaint.setStrokeCap(Cap.BUTT);
		
		redPaint =  new Paint(Paint.ANTI_ALIAS_FLAG);
		redPaint.setStyle(Style.STROKE);
		redPaint.setColor(pie_color);
		redPaint.setStrokeCap(Cap.ROUND);
	}
		
	public void setGoal(int goal){
		this.goal = goal;
		goalView.setText(getResources().getString(R.string.sport_goal, goal));
		updateProgress(false);
	}
	
	public void setSteps(int steps , boolean anim){
		this.steps = steps;
		setSteps2View(steps);
		updateProgress(anim);
	}
	
	private void updateProgress(boolean anim){
		
		progress = goal == 0 ? 0 : Math.min(100, Math.round(steps * 100f / goal));
		percentView.setText(getResources().getString(R.string.sport_percent, progress));
		
		if(anim)	startAnim(progress);
		else		invalidate();
	}
	
	/**
	 * 生成SpannableString，设置给Steps
	 * @param steps
	 */
	private void setSteps2View(int steps){
		String format = steps +  getResources().getString(R.string.unit_step);
		SpannableString span = new SpannableString(format);
		int start = format.indexOf(steps + "");
		int end = start + (steps + "").length();
		span.setSpan(new RelativeSizeSpan(5/3f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		stepView.setText(span);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.w = w;
		this.h = h;
		setShader(progress);
		
		space = w * SPACE_SCALE;
		pie_radius = w * PIE_RADIUS_SCALE;
		
		ring_width = w / 2 - space * 2 - pie_radius;
		ringPaint.setStrokeWidth(ring_width);
		redPaint.setStrokeWidth(ring_width);
		
		float ring_center = space + ring_width / 2;
		
		rectF.set(ring_center , ring_center,w - ring_center , h - ring_center);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//画背景
		paint.setColor(bg_color);
		canvas.drawCircle(w / 2, h / 2, w / 2, paint);
		//画圆盘
		paint.setColor(pie_color);
		canvas.drawCircle(w / 2, h / 2, pie_radius, paint);
		//笔触如果是圆的，那会在起始时，在-90的左边画一个半圆，当progress大于90的时候，-90的位置不是全透明，会有一点颜色，故将圆弧分为2段绘制
		canvas.drawArc(rectF, -90 ,  progress / 100f * 360 - 1, false, ringPaint);
		//画圆点
		if(progress > 0)
		canvas.drawArc(rectF, progress / 100f * 360 -2 - 90, 0.5f, false, redPaint);
	}

	public void setProgress(int progress) {
		//fix progress
		progress = Math.min(100, Math.max(0, progress));
		if(this.progress != progress){
			this.progress = progress;
			setShader(progress);
			invalidate();
		}
	}
	
	public void startAnim(int progress){
		progress = Math.min(100, progress);
		int duration = Math.min(20 * progress, 1500);
		ObjectAnimator anim = ObjectAnimator.ofInt(this, "progress", 0 , progress).setDuration(duration);
//		anim.addUpdateListener(new )
		anim.start();
	}
	
	/**
	 * Matrix SweepGradient.setLocalMatri(matrix)
	 * @param progress
	 */
	private void setShader(int progress){
		gradient = new SweepGradient(w / 2, h / 2, SWEEP_COLOR, new float[]{0 , progress / 100f , 1});
		Matrix matrix = new Matrix();
		matrix.setRotate(-90 , w / 2, h / 2);
		gradient.setLocalMatrix(matrix);
		ringPaint.setShader(gradient);
	}
}
