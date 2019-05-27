package com.yannis.android.ui.test;

import java.util.ArrayList;
import java.util.Random;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.yannis.android.ui.BuildConfig;
import com.yannis.android.ui.R;

public class SportBarChart extends View {
	/**
	 * 标题与图的竖向间距
	 */
	private static final int TITTLE_SPACE = 30;

	private ArrayList<Integer> datas;

	private Paint barPaint, textPaint;;

	private int barColor;

	private int textColor;

	private float textSize;

	private int w;

	private float yZero, yScale;

	private float yAxisLength;

	// x轴上的数字距离bar底部的距离
	private int textPadding;

	private float barWid, totalWid;

	private int countPerHour;// 每小时的数据个数

	final int ANIM2PROGRESS_MAX = 30;

	private int[] yVelocitys;

	private boolean initDraw;

	private String tittleString = "--";

	/**
	 * 此view包含3个动画，这三个动画依次播放 anim1 = true 表示从顶部中心开始依次朝两边扩散 anim2 = true
	 * 表示正在播放由anim1绘制的点，开始从中心依次随机落下动画 anim3 = true 表示正在播放由anim2落下的点，依次升高到数值的动画
	 */
	private boolean animing1 = true, animing2, animing3;

	private ObjectAnimator anim1, anim2, anim3;

	private int anim1Progress, anim2Progress;

	// private boolean cancleAnim = false;

	/**
	 * 数据的中间index ， isDouble表示数据个数是否为偶数
	 */
	private int middleIndex;
	private boolean isDouble;

	// 运动总数据的baseLine
	private float tittleBaseLineY;

	private Drawable topDrawable;

	// 因为底下数字的原因，第一条bar要偏移一点
	private float xOffset;

	private int maxData;

	public SportBarChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SportBarChart);
		textSize = a.getDimension(R.styleable.SportBarChart_android_textSize, 28);
		textColor = a.getColor(R.styleable.SportBarChart_android_textColor, 0xFFFFFFFF);
		barColor = a.getColor(R.styleable.SportBarChart_barColor, 0xffef5440);
		topDrawable = a.getDrawable(R.styleable.SportBarChart_drawable);
		a.recycle();
		init();
	}

	private void init() {
		barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		barPaint.setColor(barColor);

		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(textColor);
		textPaint.setTextSize(textSize);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.w = w;

		// 竖向距离计算
		calculateX(w);
		calculateY(h);
		initYscale();
		barPaint.setStrokeWidth(barWid);
	}
	
	/**
	 * 计算每个小格子的宽度
	 * @param w
	 */
	private void calculateX(int w) {
		if (datas != null && datas.size() != 0) {
			if (xOffset == 0) {
				// 让出第一个文字的宽度
				xOffset = ViewUtil.getTextRectWidth(textPaint, "24");
			}
			// 一个bar加一个 ( bar之间space)的宽度
			totalWid = (w - getPaddingLeft() - getPaddingRight() - xOffset * 2) / datas.size();
			// bar的宽度
			barWid = totalWid * 0.5f;
		}
	}
	
	private void calculateY(int h) {
		// 计算标题文字高度（此高度等于下方x轴坐标数值文字高度）
		float textH = ViewUtil.getTextHeight(textPaint);
		// 计算出drawable的bottom位置
		int drawableBottom = getPaddingTop();
		if (topDrawable != null) {
			drawableBottom += topDrawable.getIntrinsicHeight();
		}
		// 标题文字的底线
		float tittleBottom = drawableBottom + textH;
		// 标题文字的baseLine
		tittleBaseLineY = tittleBottom + (textPaint.ascent() + textPaint.descent()) / 2;

		// bar的数值为0时，对应的y向坐标
		yZero = h - textH - textPadding - getPaddingBottom() - barWid;
		// bar的最大高度
		yAxisLength = yZero - tittleBottom - TITTLE_SPACE;
	}

	private void initYscale() {
		// yScale = yAxisLength / barWid;
		if (datas != null) {
			maxData = 0;
			for (Integer i : datas) {
				maxData = Math.max(maxData, i);
			}
			yScale = maxData == 0 ? yAxisLength : yAxisLength / maxData;
		}
	}

	private void drawNoData(Canvas canvas) {
		float step = (w - getPaddingLeft() - getPaddingRight() - xOffset * 2) / 8;
		float x = xOffset + getPaddingLeft();
		textPaint.setAlpha(50);
		for (int i = 0; i < 9; i++) {
			canvas.drawLine(x, yZero, x, yZero - yAxisLength, textPaint);
			x += step;
		}
		textPaint.setAlpha(255);
		textPaint.setTextAlign(Align.CENTER);
		canvas.drawText(getContext().getString(R.string.noData), w / 2, yZero - yAxisLength / 2, textPaint);
	}

	private void drawYValue(Canvas canvas) {
		textPaint.setTextAlign(Align.LEFT);
		canvas.drawText(0 + "", xOffset, yZero, textPaint);
		canvas.drawLine(0, yZero , w, yZero, textPaint);
		canvas.drawText(String.format("%.0f", maxData / 2f), xOffset, yZero - maxData * yScale / 2, textPaint);
		canvas.drawLine(0, yZero - maxData * yScale / 2, w, yZero - maxData * yScale / 2, textPaint);
		canvas.drawText(maxData + "", xOffset, yZero - maxData * yScale, textPaint);
		canvas.drawLine(0, yZero - maxData * yScale, w, yZero - maxData * yScale, textPaint);
		
	}

	private void drawText(Canvas canvas) {
		// 绘制标题部分
		textPaint.setTextAlign(Align.CENTER);
		canvas.drawText(tittleString, w / 2, tittleBaseLineY, textPaint);
		// 绘制顶部图片
		if (topDrawable != null) {
			int offset = topDrawable.getIntrinsicWidth() / 2;
			int top = (int) (getPaddingTop());// + yAxisLength - yScale *
												// maxData
			int bottom = top + topDrawable.getIntrinsicHeight();
			topDrawable.setBounds(w / 2 - offset, top, w / 2 + offset, bottom);
			topDrawable.draw(canvas);
		}

		// 绘制底部x轴坐标文字
		textPaint.setTextAlign(Align.RIGHT);
		float step = totalWid * countPerHour * 2;
		float x = getPaddingLeft() + xOffset;
		float y = getMeasuredHeight() - getPaddingBottom() + (textPaint.ascent() + textPaint.descent()) / 2;
		for (int i = 0; i < 13; i++) {
			canvas.drawText(i * 2 + "", x, y, textPaint);
			x += step;
		}
	}

	private void drawBars(Canvas canvas) {
		barPaint.setColor(barColor);
		for (int i = 0; i < datas.size(); i++) {
			float x = i * totalWid + barWid / 2 + xOffset;
			canvas.drawLine(x, yZero - datas.get(i) * yScale, x, yZero, barPaint);
			canvas.drawLine(x, yZero, x, yZero + barWid, barPaint);
			if(BuildConfig.DEBUG && i % 8 == 0){
				canvas.drawLine(x, yZero, x, yZero - yAxisLength, textPaint);
			}
		}
	}

	private void drawAnim1(Canvas canvas) {
		int start = middleIndex - anim1Progress;
		int end = middleIndex + anim1Progress;
		start = Math.max(0, start);
		end = Math.min(end, datas.size() - 1);
		// DebugLog.d("start = " + start + "*******end = " + end +
		// " ************pro = " + anim1Progress);
		for (int i = start; i <= end; i++) {
			float x = i * totalWid + barWid / 2 + xOffset;
			canvas.drawLine(x, barWid, x, 0, barPaint);
		}
		if (start == 0 && end == datas.size() - 1) {
			animing1 = false;
			anim1.cancel();
			startAnim2();
		}
	}

	/**
	 * 绘制雨滴下落的效果
	 * 
	 * @param canvas
	 */
	private void drawAnim2(Canvas canvas) {
		barPaint.setStrokeWidth(barWid);
		barPaint.setColor(barColor);
		// Log.d(VIEW_LOG_TAG, "count1 = " + drawCount );
		// 初始化每个点落下的速度
		if (yVelocitys == null) {
			initYvelocity();
		}
		boolean allFallen = true;
		// 随机落下
		// for (int i = 0; i < datas.size(); i++) {
		// float y = yVelocitys[i] * drawCount + barWid;
		// y = Math.min(y, yZero);
		// if(allFallen){
		// allFallen = y == yZero;
		// }
		// float x = i * totalWid + barWid / 2 + xOffset;
		// canvas.drawLine(x, y - barWid, x, y, paint);
		// }

		// 落下逻辑有原来的随机落下改为从中心位置朝两边依次落下
		// 绘制正在落下的雨滴以及已经落下的雨滴
		int start = middleIndex - anim2Progress;
		int end = middleIndex + anim2Progress;
		start = Math.max(0, start);
		end = Math.min(end, datas.size() - 1);
		// DebugLog.d("start = " + start + "*******end = " + end +
		// " ************pro = " + anim1Progress);
		for (int j = start; j <= end; j++) {
			// 调整速度为原来的0.2倍
			float y = yVelocitys[j] * 0.2f * (anim2Progress - Math.abs(j - middleIndex)) + barWid;
			y = Math.min(y, yZero);
			if (allFallen) {
				allFallen = y == yZero;
			}
			float x = j * totalWid + barWid / 2 + xOffset;
			canvas.drawLine(x, y - barWid, x, y, barPaint);
		}
		// 绘制还未动的雨滴
		for (int i = 0, j = datas.size() - 1; i < start || j > end; i++, j--) {
			float x1 = i * totalWid + barWid / 2 + xOffset;
			float x2 = j * totalWid + barWid / 2 + xOffset;
			canvas.drawLine(x1, barWid, x1, 0, barPaint);
			canvas.drawLine(x2, barWid, x2, 0, barPaint);
		}
		// DebugLog.d("allFallen = " + allFallen + "****anim2Progress = " +
		// anim2Progress);
		if (start == 0 && end == datas.size() - 1) {
			if (allFallen) {
				anim2Progress = 0;
				animing2 = false;
				anim2.cancel();
				if(datas == null || maxData == 0){
					invalidate();
				}else{
					startAnim3();
				}
				// 有时候雨滴还未全部落下，就已经动画结束了，所以给anim2progress设置一个比较大的值，让他强制结束掉
				// 此bug修改有关内容：anim2的动画结束最大值（关系到雨滴的最小速度，以及速度的的调整，速度调整在此方法的第一个for循环中）
				// 此bug已修复，但为了安全，不去掉此代码（原因：之前初始化速度时用的总距离为yAxisLength,实际落的距离是从0到yZero的距离，故此引发此bug）
			} else {
				anim2Progress = 100000;
				invalidate();
			}
		}

	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// if(!cancleAnim){
		if (!initDraw) {
			initDraw = true;
			startAnim1();
		} else {
			if (animing1) {
				drawAnim1(canvas);
			} else if (animing2) {
				drawAnim2(canvas);
			} else {
				if(datas == null || maxData == 0){
					drawNoData(canvas);
				}else{
					drawBars(canvas);
					if(BuildConfig.DEBUG){
						drawYValue(canvas);
					}
				}
				drawText(canvas);
			}
		}
	}

	public void startAnim2() {
		animing2 = true;
		anim2 = ObjectAnimator.ofInt(this, "anim2Progress", 1, (int) (datas.size() / 2 + ANIM2PROGRESS_MAX / 2f * 6)).setDuration(2000);
		// anim.setInterpolator(new BounceInterpolator());
		// anim.setInterpolator(new AccelerateDecelerateInterpolator());
		// anim2.setInterpolator(new AccelerateInterpolator());
		// anim.setInterpolator(new AnticipateInterpolator());
		// anim.setInterpolator(new OvershootInterpolator());
		// anim.setInterpolator(new CycleInterpolator(0.4f));
		// anim.setInterpolator(new DecelerateInterpolator());
		anim2.start();
	}

	private void startAnim3() {
		anim3 = ObjectAnimator.ofFloat(this, "yScale", 0, yScale).setDuration(500);
		anim3.start();
	}

	public void startAnimSet() {
		setVisibility(View.VISIBLE);
		initDraw = false;
		invalidate();
	}

	public void startAnim1() {
		animing1 = true;
		anim1 = ObjectAnimator.ofInt(this, "anim1Progress", 0, datas.size() / 2 + 1).setDuration(500);
		anim1.start();
	}

	public void setAnim2Progress(int anim2Progress) {
		this.anim2Progress = anim2Progress;
		invalidate();
	}

	public void setAnim1Progress(int anim1Progress) {
		this.anim1Progress = anim1Progress;
		invalidate();
	}

	public void setYScale(float yScale) {
		// yScale = Math.min(1, Math.max(0, yScale));
		this.yScale = yScale;
		invalidate();
	}

	private void initYvelocity() {
		yVelocitys = new int[datas.size()];
		Random random = new Random();
		for (int i = 0; i < yVelocitys.length; i++) {
			// 雨滴的最大速度为yAxisLength / (ANIM2PROGRESS_MAX / 5)
			while (yVelocitys[i] < ANIM2PROGRESS_MAX / 5) {
				// 雨滴的最小速度为yAxisLength / (ANIM2PROGRESS_MAX / 2)
				yVelocitys[i] = random.nextInt(ANIM2PROGRESS_MAX / 2);
			}
			yVelocitys[i] = (int) (yZero / yVelocitys[i]);
		}
	}

	public void setDatas(ArrayList<Integer> datas) {
		this.datas = datas;
		countPerHour = datas.size() / 24;
		isDouble = (datas.size() % 2 == 0);
		middleIndex = datas.size() / 2 - (isDouble ? 1 : 0);
		initYscale();
		initDraw = false;
		invalidate();
	}

	public void setTittleString(String tittleString) {
		this.tittleString = tittleString;
	}

	// private OnClickListener onClick = new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	//
	// }
	// };
}
