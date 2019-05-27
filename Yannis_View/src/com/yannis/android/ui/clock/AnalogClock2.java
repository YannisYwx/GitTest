/*
 *by yangxilin.
 */

package com.yannis.android.ui.clock;

import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

import com.yannis.android.ui.R;

/**
 * This widget display an analogic clock with two hands for hours and minutes.
 */
@RemoteView
public class AnalogClock2 extends View {
	private static final String TAG="CLOCK";

	private Time mCalendar;

	private Drawable mHourHand;
	private Drawable mMinuteHand;
	// 20131022
	private Drawable mSecondHand;
	private Runnable mTicker;
	//
	private Drawable mDial;
	// private Drawable mTopImage;

	private int mDialWidth;
	private int mDialHeight;

	private boolean mAttached;

	private final Handler mHandler = new Handler();
	private float mMinutes;
	// 20131022
	private float mSeconds;
	// end
	private float mHour;
	private boolean mChanged;

	public AnalogClock2(Context context) {
		this(context, null);
	}

	public AnalogClock2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AnalogClock2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Resources r = context.getResources();

		if (mDial == null) {
			mDial = r.getDrawable(R.drawable.clock_bg);
		}

		//mTopImage = r.getDrawable(R.drawable.clock_top);

		if (mHourHand == null) {
			mHourHand = r.getDrawable(R.drawable.clock_hand_hour);
		}

		if (mMinuteHand == null) {
			mMinuteHand = r.getDrawable(R.drawable.clock_hand_minute);
		}
		// 20131022
		mSecondHand = r.getDrawable(R.drawable.clock_hand_second);
		// end
		mCalendar = new Time();

		mDialWidth = mDial.getIntrinsicWidth();
		mDialHeight = mDial.getIntrinsicHeight();
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

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mAttached) {
			getContext().unregisterReceiver(mIntentReceiver);
			mAttached = false;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		

		float hScale = 1.0f;

		float vScale = 1.0f;
		if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
			hScale = (float) widthSize / (float) mDialWidth;
		}

		if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
			vScale = (float) heightSize / (float) mDialHeight;
		}

		float scale = Math.min(hScale, vScale);
		
		Log.d(TAG, "widthMode:"+widthMode+"--heightMode:"+heightMode);
		Log.d(TAG, "widthSize:"+widthSize+"--heightSize:"+heightSize);
		Log.d(TAG, "hScale:"+hScale+"--vScale:"+vScale);
		Log.d(TAG, "w:"+resolveSizeAndState((int) (mDialWidth * scale),
				widthMeasureSpec, 0)+"--h:"+resolveSizeAndState((int) (mDialHeight * scale),
						heightMeasureSpec, 0));

		setMeasuredDimension(
				resolveSizeAndState((int) (mDialWidth * scale),
						widthMeasureSpec, 0),
				resolveSizeAndState((int) (mDialHeight * scale),
						heightMeasureSpec, 0));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mChanged = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

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

		if (changed) {
			dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}
		dial.draw(canvas);

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
		// 20131022
		canvas.save();
		canvas.rotate(mSeconds / 60.0f * 360.0f, x, y);

		final Drawable secondHand = mSecondHand;
		// if (changed) {
		w = secondHand.getIntrinsicWidth();
		h = secondHand.getIntrinsicHeight();
		secondHand
				.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		// }
		secondHand.draw(canvas);
		canvas.restore();
		// end

		{// yangxilin.begain2
		/*
		 * if (changed) { w = mTopImage.getIntrinsicWidth(); h =
		 * mTopImage.getIntrinsicHeight(); mTopImage.setBounds(x - (w / 2), y -
		 * (h / 2), x + (w / 2), y + (h / 2)); } mTopImage.draw(canvas);
		 */
		}// end
		if (scaled) {
			canvas.restore();
		}
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