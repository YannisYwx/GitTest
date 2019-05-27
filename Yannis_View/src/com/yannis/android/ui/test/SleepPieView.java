package com.yannis.android.ui.test;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yannis.android.ui.R;

public class SleepPieView extends RelativeLayout {
	
	private TextView dataView , fallSleepTimeView , awakeTimeView; 
	
	private ImageView imageview;
	
	private int colorBg;
	
	private Paint paint;
	
	private String unitHour,unitMin;

	public SleepPieView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.sport_pie_view, this);
		dataView = (TextView) findViewById(R.id.data);
		fallSleepTimeView = (TextView) findViewById(R.id.goal);
		awakeTimeView = (TextView) findViewById(R.id.percent);
		imageview = (ImageView) findViewById(R.id.stateImg);
		
		Resources res = getResources();
		TypedArray a = context.obtainStyledAttributes(attrs , R.styleable.SleepPieView);
		colorBg = a.getColor(R.styleable.SleepPieView_bgColor, res.getColor(R.color.sleep_pie_bg));
		a.recycle();
		
		unitHour = res.getString(R.string.unit_hour_en);
		unitMin = res.getString(R.string.unit_minute_en);
		
		
		imageview.setImageResource(R.drawable.sleep_pie_top);
		
		setWillNotDraw(false);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(colorBg);
		paint.setStyle(Style.FILL);
	}
	
	
	
//	public void setDatas(SleepData data){
//		dataView.setText(data.getDuration()[0] + unitHour + data.getDuration()[1] + unitMin);
//		fallSleepTimeView.setText(getResources().getString(R.string.fallSleepTime, data.getStartTimeStr()));
//		awakeTimeView.setText(getResources().getString(R.string.awakeTime, data.getEndTimeStr()));
//	}
	
//	/**
//	 * 生成SpannableString，设置给Data
//	 * @param steps
//	 */
//	private void setSteps(int steps){
//		String format = steps +  getResources().getString(R.string.unit_step);
//		SpannableString span = new SpannableString(format);
//		int start = format.indexOf(steps + "");
//		int end = start + (steps + "").length();
//		span.setSpan(new RelativeSizeSpan(5/3f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//		dataView.setText(span);
//	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, paint);
	}

}
