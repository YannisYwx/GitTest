package com.yannis.android.ui.widget;

import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yannis.android.ui.R;
import com.yannis.android.ui.test.ViewUtil;

public class ClockView extends View {
	private static final String TAG="CLOCK";
	private static final float H_SCALE=0.4f;
	private static final float W_ICON_CIRLC_SCALE=50/400f;	
	private static final float CIRCLE_SIZE_SCALE=122/400f;
	private static final float DATE_H_BOTTOM_SCALE=330/400f;
	
	private static final float TEXT_SIZE_DATA_SCALE=18/400f;
	private static final float TEXT_SIZE_UNIT_SCALE=14/400f;
	private static final float TEXT_SIZE_DATE_SCALE=24/400f;
	private int textColor=Color.parseColor("#828282");
	
	private Paint paint;
	
	private Drawable bg;//背景
	private Drawable mDial;//表盘
	private Drawable mIconProgress;//步数icon
	private Drawable mIconRun;//跑步icon
	private Drawable mHourHand;//时
	private Drawable mMinuteHand;//分
	private Drawable mSecondHand;//秒
	private Drawable mScrew;//点

	
	private int mDialWidth;//表盘的宽
	private int mDialHeight;//表盘的高
	private int size;
	private int centerX;
	private int centerY;
	
	private String runUnit="步";
	private String progressUnit="%";
	private int data_Run=10367;
	private int data_Progress=63;
	private int date_day=20;
	
	
	//clock
	private Time mCalendar;

	private Runnable mTicker;

	private final Handler mHandler = new Handler();
	private float mMinutes;
	private float mSeconds;
	private float mHour;
	
	
	private boolean mAttached;
	private boolean mChanged;

	public ClockView(Context context) {
		super(context);
		init(context);
	}

	public ClockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
	public void init(Context context){
		Resources r = context.getResources();
		mDial=r.getDrawable(R.drawable.watch_face);
		mIconProgress=r.getDrawable(R.drawable.icon_progress);
		mIconRun=r.getDrawable(R.drawable.icon_run);
		mHourHand=r.getDrawable(R.drawable.pointer_hour);
		mMinuteHand=r.getDrawable(R.drawable.pointer_minute);
		mSecondHand=r.getDrawable(R.drawable.pointer_second);
		mScrew=r.getDrawable(R.drawable.pointer_screw);
		bg=r.getDrawable(R.drawable.watch_bg);
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextAlign(Align.CENTER);
		
		
		
		
		mDialWidth = mDial.getIntrinsicWidth();
		mDialHeight = mDial.getIntrinsicHeight();
		
		mCalendar = new Time();

		Log.d(TAG, "mDialWidth:"+mDialWidth+"--mDialHeight:"+mDialHeight);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		size=Math.min(mDialHeight, mDialWidth);
		centerX=size/2;
		centerY=size/2;
		setMeasuredDimension(size, size);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mChanged = true;
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mAttached) {
			getContext().unregisterReceiver(mIntentReceiver);
			mAttached = false;
		}
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		drawWatchFace(canvas);
		drawWatch(canvas);
	}
	
	/**
	 * 画表盘的icon以及数值
	 * @param canvas
	 */
	public void drawWatchFace(Canvas canvas){
		bg.setBounds(centerX-mDialWidth/2, centerY-mDialHeight/2, centerX+mDialWidth/2, centerY+mDialHeight/2);
		bg.draw(canvas);
		//表盘
		mDial.setBounds(centerX-mDialWidth/2, centerY-mDialHeight/2, centerX+mDialWidth/2, centerY+mDialHeight/2);
		mDial.draw(canvas);
		
		//icon
		//run
		int iconRunW=mIconRun.getIntrinsicWidth();
		int iconRunH=mIconRun.getIntrinsicHeight();
		int runX=(int)(size*W_ICON_CIRLC_SCALE+size*CIRCLE_SIZE_SCALE/2);
		mIconRun.setBounds(runX-iconRunW/2, 
				(int)(centerY*2*H_SCALE), runX+iconRunW/2, (int)(centerY*2*H_SCALE)+iconRunH);
		mIconRun.draw(canvas);
		//progress
		int iconProgressW=mIconProgress.getIntrinsicWidth();
		int iconProgressH=mIconProgress.getIntrinsicHeight();
		int proX=(int) (size-size*W_ICON_CIRLC_SCALE-size*CIRCLE_SIZE_SCALE/2);
		mIconProgress.setBounds(proX-iconProgressW/2, (int)(centerY*2*H_SCALE),proX+iconProgressW/2, (int)(centerY*2*H_SCALE)+iconProgressH);
		mIconProgress.draw(canvas);
		Log.d(TAG, "ICON-RUN:("+iconRunW+","+iconRunH+")  Progress:("+iconProgressW+","+iconProgressH+")");
		//画数字 -步数
		paint.setColor(textColor);
		paint.setStyle(Style.FILL);
		paint.setTypeface(Typeface.SANS_SERIF);
		//run-unit
		paint.setTextSize(size*TEXT_SIZE_UNIT_SCALE);
		float runUnitW=ViewUtil.getTextRectWidth(paint, runUnit);
		float progressUnitW=ViewUtil.getTextRectWidth(paint, progressUnit);
//		//run-text
		paint.setTextSize(size*TEXT_SIZE_DATA_SCALE);
		String dataRun=data_Run+"";
		float runW=ViewUtil.getTextRectWidth(paint, dataRun);
		float dataH=ViewUtil.getTextHeight(paint);
		float dataY=dataH/2-(paint.ascent()+paint.descent())/2f+(int)(centerY*2*H_SCALE)+iconRunH;
		canvas.drawText(dataRun, runX-runUnitW/2f-runUnitW/4f, dataY, paint);
		//run-unit
		paint.setTextSize(size*TEXT_SIZE_UNIT_SCALE);
		canvas.drawText(runUnit, runX+runW/2f-runUnitW/4f, dataY, paint);
		//画数字-百分比
		//run-text
		paint.setTextSize(size*TEXT_SIZE_DATA_SCALE);
		String dataProgress=data_Progress+"";
		float progressW=ViewUtil.getTextRectWidth(paint, dataProgress);
		float dataProgressH=ViewUtil.getTextHeight(paint);
		float dataProgressY=dataProgressH/2-(paint.ascent()+paint.descent())/2f+(int)(centerY*2*H_SCALE)+iconProgressH;
		canvas.drawText(dataProgress, proX-progressUnitW/2f-progressUnitW/4f, dataProgressY, paint);
		//run-unit
		paint.setTextSize(size*TEXT_SIZE_UNIT_SCALE);
		canvas.drawText(progressUnit, proX+progressW/2f-progressUnitW/4f, dataProgressY, paint);
		//画日期
		paint.setTextSize(size*TEXT_SIZE_DATE_SCALE);
		canvas.drawText(mCalendar.monthDay+"", centerX, size*DATE_H_BOTTOM_SCALE+(paint.ascent()+paint.descent())/2f, paint);
		
		
	}
	
	public void drawWatch(Canvas canvas){

		boolean changed = mChanged;
		if (changed) {
			mChanged = false;
		}

		int availableWidth = getWidth();
		int availableHeight = getHeight();

		int x = availableWidth / 2;
		int y = availableHeight / 2;

		final Drawable dial = mDial;
		int w = dial.getIntrinsicWidth();
		int h = dial.getIntrinsicHeight();

		boolean scaled = false;

		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			canvas.save();
			canvas.scale(scale, scale, x, y);
		}
		
		//画时钟
		canvas.save();
		canvas.rotate(mHour / 12.0f * 360.0f, x, y);
		final Drawable hourHand = mHourHand;
		if (changed) {
			w = hourHand.getIntrinsicWidth();
			h = hourHand.getIntrinsicHeight();
			hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		hourHand.draw(canvas);
		canvas.restore();
		
		//画分钟
		canvas.save();
		canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);

		final Drawable minuteHand = mMinuteHand;
		if (changed) {
			w = minuteHand.getIntrinsicWidth();
			h = minuteHand.getIntrinsicHeight();
			minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		minuteHand.draw(canvas);
		canvas.restore();
		//画秒钟
		canvas.save();
		canvas.rotate(mSeconds / 60.0f * 360.0f, x, y);
		final Drawable secondHand = mSecondHand;
		w = secondHand.getIntrinsicWidth();
		h = secondHand.getIntrinsicHeight();
		secondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		secondHand.draw(canvas);
		canvas.restore();
		// end
		if (scaled) {
			canvas.restore();
		}
		
		w = mScrew.getIntrinsicWidth();
		h = mScrew.getIntrinsicHeight();
		mScrew.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		mScrew.draw(canvas);
	}
	
	public void setDate(int step,int progress){
		this.data_Run=step;
		this.data_Progress=progress;
		invalidate();
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (!mAttached) {
			mAttached = true;
			IntentFilter filter = new IntentFilter();

			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);
			filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

			getContext().registerReceiver(mIntentReceiver, filter, null,
					mHandler);
		}

		// NOTE: It's safe to do these after registering the receiver since the
		// receiver always runs
		// in the main thread, therefore the receiver can't run before this
		// method returns.

		// The time zone may have changed while the receiver wasn't registered,
		// so update the Time
		mCalendar = new Time();

		// Make sure we update to the current time
		onTimeChanged();
		// 20131022

		mTicker = new Runnable() {
			public void run() {

				// mCalendar.setTimeInMillis(System.currentTimeMillis());
				// setText(DateFormat.format(mFormat, mCalendar));
				onTimeChanged();
				invalidate();
				long now = SystemClock.uptimeMillis();
				long next = now + (1000 - now % 1000);
				mHandler.postAtTime(mTicker, next);
			}
		};
		mTicker.run();

		// end
	}
	
	private void onTimeChanged() {
		mCalendar.setToNow();

		int hour = mCalendar.hour;
		int minute = mCalendar.minute;
		int second = mCalendar.second;

		mMinutes = minute + second / 60.0f;
		mHour = hour + mMinutes / 60.0f;
		// 20131022
		mSeconds = (float) second;
		// end
		mChanged = true;

	}

	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				String tz = intent.getStringExtra("time-zone");
				mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
			}

			onTimeChanged();

			invalidate();
		}
	};

}
