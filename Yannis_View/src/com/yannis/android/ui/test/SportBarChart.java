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
	 * ������ͼ��������
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

	// x���ϵ����־���bar�ײ��ľ���
	private int textPadding;

	private float barWid, totalWid;

	private int countPerHour;// ÿСʱ�����ݸ���

	final int ANIM2PROGRESS_MAX = 30;

	private int[] yVelocitys;

	private boolean initDraw;

	private String tittleString = "--";

	/**
	 * ��view����3���������������������β��� anim1 = true ��ʾ�Ӷ������Ŀ�ʼ���γ�������ɢ anim2 = true
	 * ��ʾ���ڲ�����anim1���Ƶĵ㣬��ʼ����������������¶��� anim3 = true ��ʾ���ڲ�����anim2���µĵ㣬�������ߵ���ֵ�Ķ���
	 */
	private boolean animing1 = true, animing2, animing3;

	private ObjectAnimator anim1, anim2, anim3;

	private int anim1Progress, anim2Progress;

	// private boolean cancleAnim = false;

	/**
	 * ���ݵ��м�index �� isDouble��ʾ���ݸ����Ƿ�Ϊż��
	 */
	private int middleIndex;
	private boolean isDouble;

	// �˶������ݵ�baseLine
	private float tittleBaseLineY;

	private Drawable topDrawable;

	// ��Ϊ�������ֵ�ԭ�򣬵�һ��barҪƫ��һ��
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

		// ����������
		calculateX(w);
		calculateY(h);
		initYscale();
		barPaint.setStrokeWidth(barWid);
	}
	
	/**
	 * ����ÿ��С���ӵĿ��
	 * @param w
	 */
	private void calculateX(int w) {
		if (datas != null && datas.size() != 0) {
			if (xOffset == 0) {
				// �ó���һ�����ֵĿ��
				xOffset = ViewUtil.getTextRectWidth(textPaint, "24");
			}
			// һ��bar��һ�� ( bar֮��space)�Ŀ��
			totalWid = (w - getPaddingLeft() - getPaddingRight() - xOffset * 2) / datas.size();
			// bar�Ŀ��
			barWid = totalWid * 0.5f;
		}
	}
	
	private void calculateY(int h) {
		// ����������ָ߶ȣ��˸߶ȵ����·�x��������ֵ���ָ߶ȣ�
		float textH = ViewUtil.getTextHeight(textPaint);
		// �����drawable��bottomλ��
		int drawableBottom = getPaddingTop();
		if (topDrawable != null) {
			drawableBottom += topDrawable.getIntrinsicHeight();
		}
		// �������ֵĵ���
		float tittleBottom = drawableBottom + textH;
		// �������ֵ�baseLine
		tittleBaseLineY = tittleBottom + (textPaint.ascent() + textPaint.descent()) / 2;

		// bar����ֵΪ0ʱ����Ӧ��y������
		yZero = h - textH - textPadding - getPaddingBottom() - barWid;
		// bar�����߶�
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
		// ���Ʊ��ⲿ��
		textPaint.setTextAlign(Align.CENTER);
		canvas.drawText(tittleString, w / 2, tittleBaseLineY, textPaint);
		// ���ƶ���ͼƬ
		if (topDrawable != null) {
			int offset = topDrawable.getIntrinsicWidth() / 2;
			int top = (int) (getPaddingTop());// + yAxisLength - yScale *
												// maxData
			int bottom = top + topDrawable.getIntrinsicHeight();
			topDrawable.setBounds(w / 2 - offset, top, w / 2 + offset, bottom);
			topDrawable.draw(canvas);
		}

		// ���Ƶײ�x����������
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
	 * ������������Ч��
	 * 
	 * @param canvas
	 */
	private void drawAnim2(Canvas canvas) {
		barPaint.setStrokeWidth(barWid);
		barPaint.setColor(barColor);
		// Log.d(VIEW_LOG_TAG, "count1 = " + drawCount );
		// ��ʼ��ÿ�������µ��ٶ�
		if (yVelocitys == null) {
			initYvelocity();
		}
		boolean allFallen = true;
		// �������
		// for (int i = 0; i < datas.size(); i++) {
		// float y = yVelocitys[i] * drawCount + barWid;
		// y = Math.min(y, yZero);
		// if(allFallen){
		// allFallen = y == yZero;
		// }
		// float x = i * totalWid + barWid / 2 + xOffset;
		// canvas.drawLine(x, y - barWid, x, y, paint);
		// }

		// �����߼���ԭ����������¸�Ϊ������λ�ó�������������
		// �����������µ�����Լ��Ѿ����µ����
		int start = middleIndex - anim2Progress;
		int end = middleIndex + anim2Progress;
		start = Math.max(0, start);
		end = Math.min(end, datas.size() - 1);
		// DebugLog.d("start = " + start + "*******end = " + end +
		// " ************pro = " + anim1Progress);
		for (int j = start; j <= end; j++) {
			// �����ٶ�Ϊԭ����0.2��
			float y = yVelocitys[j] * 0.2f * (anim2Progress - Math.abs(j - middleIndex)) + barWid;
			y = Math.min(y, yZero);
			if (allFallen) {
				allFallen = y == yZero;
			}
			float x = j * totalWid + barWid / 2 + xOffset;
			canvas.drawLine(x, y - barWid, x, y, barPaint);
		}
		// ���ƻ�δ�������
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
				// ��ʱ����λ�δȫ�����£����Ѿ����������ˣ����Ը�anim2progress����һ���Ƚϴ��ֵ������ǿ�ƽ�����
				// ��bug�޸��й����ݣ�anim2�Ķ����������ֵ����ϵ����ε���С�ٶȣ��Լ��ٶȵĵĵ������ٶȵ����ڴ˷����ĵ�һ��forѭ���У�
				// ��bug���޸�����Ϊ�˰�ȫ����ȥ���˴��루ԭ��֮ǰ��ʼ���ٶ�ʱ�õ��ܾ���ΪyAxisLength,ʵ����ľ����Ǵ�0��yZero�ľ��룬�ʴ�������bug��
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
			// ��ε�����ٶ�ΪyAxisLength / (ANIM2PROGRESS_MAX / 5)
			while (yVelocitys[i] < ANIM2PROGRESS_MAX / 5) {
				// ��ε���С�ٶ�ΪyAxisLength / (ANIM2PROGRESS_MAX / 2)
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
