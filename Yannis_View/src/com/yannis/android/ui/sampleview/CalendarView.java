package com.yannis.android.ui.sampleview;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.yannis.android.ui.Logger;
import com.yannis.android.ui.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class CalendarView extends View {
	private Calendar calendar;
	private Date currentDate;
	private Date today;
	private Date downDay;
	private int todayIndex = -1;// 今日的下标
	private int downIndex = -1; // 按下的格子索引
	private int curStartIndex, curEndIndex;
	
	private int width, height;// 日历控件的宽高
	private float weekHeight;// 星期的高度
	private float ymdHeight;// 年月日的高度
	private float cellWidth, cellHeight;// 日期方框宽高
	private float textSize;// 字体大小
	private int nextOrLastMonthDayColor = 0xFFAAAAAA;// 其他日期的顏色
	private int textColor = 0xFFFFFFFF;//日期字体的颜色
	private int choiseTextColor = 0xFF30d8c2;// 选中的字体颜色
	private int backgroundColor = 0xFF30d8c2;// view的背景
	private int todayTextColor = 0xFFFC8C5C;
	
	private Bitmap left, right;
	private int bitmapSize;// bitmap宽高
	private float[] leftPosition = new float[4];
	private float[] rightPosition = new float[4];
	
	private int[] date = new int[42]; // 日历显示数字
	private String[] weeks = { "日", "一", "二", "三", "四", "五", "六" };
	private Paint paint;

	private String todayMonthYear;
	private String curMonthYear;
	private String downYMD;// 按下的年月日
	private SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdf_ym = new SimpleDateFormat("yyyy-MM");

	private boolean isThisMonth;

	public Context context;

	public CalendarView(Context context) {
		super(context);
		init(context);
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
		textSize=ta.getColor(R.styleable.CalendarView_textColor,  0xFFFFFFFF);
		choiseTextColor=ta.getColor(R.styleable.CalendarView_choiseTextColor,  0xA5D448);
		backgroundColor=ta.getColor(R.styleable.CalendarView_backgroundColor,  0xA5D448);
		
		init(context);
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		width = getResources().getDisplayMetrics().widthPixels;
		height = (int) (getResources().getDisplayMetrics().heightPixels * 0.45f);
		widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width,
				View.MeasureSpec.EXACTLY);
		heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height,
				View.MeasureSpec.EXACTLY);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		// 计算日期
		calculateDate();
		drawWeeks(canvas);
		drawCellDate(canvas);
		drawDateCellBg(canvas, downIndex);
	}

	/**
	 * 画星期
	 * 
	 * @param canvas
	 */
	public void drawWeeks(Canvas canvas) {
		// 画背景
		paint.setColor(Color.parseColor("#22000000"));
		canvas.drawRect(0, weekHeight, width,
				weekHeight + ymdHeight, paint);
		// 画星期
		paint.setColor(Color.WHITE);
		paint.setTextSize(textSize * 1.2F);
		for (int i = 0; i < weeks.length; i++) {
			float wx = i * cellWidth + cellWidth / 2f;
			float wy = weekHeight / 2f
					- (paint.ascent() + paint.descent()) / 2
					+ ymdHeight;
			canvas.drawText(weeks[i], wx, wy, paint);
		}
		canvas.drawBitmap(left, bitmapSize * 0.25f,
				ymdHeight / 2f - bitmapSize / 2f, paint);
		canvas.drawBitmap(right, width - bitmapSize
				- bitmapSize * 0.25f, ymdHeight / 2f
				- bitmapSize / 2f, paint);
		
		downYMD = sdf_ym.format(currentDate);
		canvas.drawText(downYMD, width / 2, ymdHeight / 2f
				- (paint.ascent() + paint.descent()) / 2,
				paint);
	}

	/**
	 * 画日期
	 * 
	 * @param canvas
	 */
	public void drawCellDate(Canvas canvas) {
		// 判断当前是不是今天
		int todayNum = -1;
		isThisMonth = todayMonthYear.equals(curMonthYear);
		calendar.setTime(currentDate);
		if (isThisMonth) {
			todayNum = calendar.get(Calendar.DAY_OF_MONTH);// 獲取今天是該月的多少號(一定要初始化一下calendar)
			todayNum = curStartIndex + todayNum - 1;
		}
		Logger.d("todayIndex:" + todayNum);
		// 画数字
		for (int i = 0; i < 42; i++) {
			int color;
			if (isNextMonth(i) || isLastMonth(i)) {
				color = nextOrLastMonthDayColor;
			} else if (i == todayNum && isThisMonth) {
				color = todayTextColor;
			} else {
				color = textColor;
			}
			paint.setColor(color);
			drawDateText(canvas, i);
		}
	}

	/**
	 * 画数字
	 * 
	 * @param canvas
	 * @param index
	 */
	public void drawDateText(Canvas canvas, int index) {
		int[] rowCol = getRowColByIndex(index);
		int col = rowCol[0];// 列
		int row = rowCol[1];// 行
		String text = date[index] + "";
		float cellY = (row - 1) * cellHeight + cellHeight / 2f
				- (paint.ascent() + paint.descent()) / 2
				+ weekHeight + ymdHeight;
		float cellX = (cellWidth * (col - 1)) + cellWidth / 2f;
		/*
		 * float cellX = (cellWidth * (col - 1))+ (cellWidth -
		 * paint.measureText(text))/ 2f;
		 */
		canvas.drawText(text, cellX, cellY, paint);
	}

	/**
	 * 画按下背景
	 * 
	 * @param canvas
	 * @param downIndex
	 */
	public void drawDateCellBg(Canvas canvas, int downIndex) {
		Logger.d("==downindex:" + downIndex + "  todayIndex:" + todayIndex);
		if (todayIndex != -1
				&& (isLastMonth(downIndex) || isNextMonth(downIndex))) {
			return;
		}

		if (todayIndex == -1 && isThisMonth) {
			calendar.setTime(currentDate);
			todayIndex = calendar.get(Calendar.DAY_OF_MONTH);// 獲取今天是該月的多少號(一定要初始化一下calendar)
			todayIndex = curStartIndex + todayIndex - 1;
			downIndex = todayIndex;
		}

		// 画圆
		int[] rowCol = getRowColByIndex(downIndex);
		int col = rowCol[0];// 列
		int row = rowCol[1];// 行
		Logger.d("drawDateCellBg", "按下的index:" + downIndex + " 列：" + col
				+ " 行：" + row);
		float cx = (col - 1) * cellWidth + cellWidth / 2;
		float cy = (row - 1) * cellHeight + cellHeight / 2
				+ weekHeight + ymdHeight;
		float radius = Math.min(cellWidth / 2, cellHeight / 2) * 0.9f;
		paint.setColor(Color.WHITE);
		canvas.drawCircle(cx, cy, radius, paint);

		// 画数字
		paint.setColor(backgroundColor);
		float cellY = (row - 1) * cellHeight + cellHeight / 2f
				- (paint.ascent() + paint.descent()) / 2
				+ weekHeight + ymdHeight;
		float cellX = (cellWidth * (col - 1)) + cellWidth / 2f;
		canvas.drawText(date[downIndex] + "", cellX, cellY, paint);
		// 画title
		paint.setTextSize(textSize * 1.3f);
		paint.setColor(Color.WHITE);

//		downYMD = sdf_ym.format(currentDate) + "-"
//				+ String.format("%02d", date[downIndex]);
//		canvas.drawText(downYMD, width / 2, ymdHeight / 2f
//				- (paint.ascent() + paint.descent()) / 2,
//				paint);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			calculateDateBySelect(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:
//			calculateDateBySelect(event.getX(), event.getY());
			checkIsMonthClick(event.getX(), event.getY());
			break;

		default:
			break;
		}
		return true;
	}

	public void init(Context context) {
		this.context = context;
		calendar = Calendar.getInstance();
		currentDate = new Date();
		today = new Date();
		calendar.setTime(today);
		todayMonthYear = sdf_ym.format(today);

		setBackgroundColor(backgroundColor);
		
		ymdHeight = height / 7f;
		weekHeight = height / 7f;
		cellHeight = (height - weekHeight - ymdHeight) / 6f;
		cellWidth = width / 7f;
		textSize = cellHeight * 0.4F;
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(textSize);

		left = BitmapFactory.decodeResource(getResources(),
				R.drawable.calendar_month_left);
		right = BitmapFactory.decodeResource(getResources(),
				R.drawable.calendar_month_right);
		int lW = left.getWidth();
		int lH = left.getHeight();
		int rW = right.getWidth();
		int rH = right.getHeight();
		bitmapSize = Math.min(Math.min(lH, lW), Math.min(rW, rH));

		left = Bitmap.createScaledBitmap(left, bitmapSize, bitmapSize,
				false);
		right = Bitmap.createScaledBitmap(right, bitmapSize, bitmapSize,
				false);

		float bitmapYmin = ymdHeight / 2f - bitmapSize / 2f;
		float bitmapYmax = bitmapYmin + bitmapSize;
		leftPosition[0] = bitmapSize*0.25f;
		leftPosition[1] = bitmapSize*1.25f;
		leftPosition[2] = bitmapYmin;
		leftPosition[3] = bitmapYmax;

		rightPosition[0] = width - 1.25f * bitmapSize;
		rightPosition[1] = width - 0.25f * bitmapSize;
		rightPosition[2] = bitmapYmin;;
		rightPosition[3] = bitmapYmax;

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public void calculateDate() {
		calendar.setTime(currentDate);
		calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为当前的那个月
		int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);// 这个月的第一天是星期几
		int monthStart = dayInWeek;
		if (monthStart == 1) {
			monthStart = 8;
		}
		monthStart -= 1;// 0-6
		curStartIndex = monthStart;
		// 计算view中上个月的补齐下个月天数
		if (monthStart > 0) {
			calendar.set(Calendar.DAY_OF_MONTH, 0);
			int lastMonthDays = calendar.get(Calendar.DAY_OF_MONTH);
			for (int i = monthStart - 1; i >= 0; i--) {
				date[i] = lastMonthDays;
				lastMonthDays--;
			}
		}
		// 这个月份的日期
		calendar.setTime(currentDate);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 0);
		int dayInCurMonth = calendar.get(Calendar.DAY_OF_MONTH);
		Log.d("ywx", "dayInWeek:" + dayInWeek + "   ----dayInCurMonth:"
				+ dayInCurMonth);

		curEndIndex = dayInCurMonth + monthStart;
		for (int i = 0; i < dayInCurMonth; i++) {
			date[monthStart + i] = i + 1;
		}
		// 下个月的日期
		int nextDays = 42 - curEndIndex;
		if (nextDays > 0) {
			for (int i = 0; i < nextDays; i++) {
				date[curEndIndex + i] = i + 1;
			}
		}
		curMonthYear = sdf_ym.format(currentDate);
		Log.d("ywx", "curStartIndex:" + curStartIndex + "   ----curEndIndex:"
				+ curEndIndex);
	}

	/**
	 * 检查是否是月份点击了
	 * @param x
	 * @param y
	 */
	public void checkIsMonthClick(float x, float y) {
		// 上一个月
		if (y >= leftPosition[2] && y <= leftPosition[3]) {
			if (x >= leftPosition[0] && x <= leftPosition[1]) {
				Toast.makeText(context, "上一个月", Toast.LENGTH_LONG).show();
				clickLastMonth();
				return;
			}
		}

		// 下一个月
		if (y >= rightPosition[2] && y <= rightPosition[3]) {
			if (x >= rightPosition[0] && x <= rightPosition[1]) {
				Toast.makeText(context, "下一个月", Toast.LENGTH_LONG).show();
				clickNextMonth();
				return;
			}
		}
	}

	/**
	 * 计算点击下去的坐标
	 * 
	 * @param x
	 * @param y
	 */
	public void calculateDateBySelect(float x, float y) {
		y -= (weekHeight + ymdHeight);
		if (y > 0) {
			int col = (int) (Math.floor(x / cellWidth) + 1);// 列
			int row = (int) (Math.floor(y / Float.valueOf(cellHeight)) + 1);// 行
			Logger.d("drawDateCellBg", "downX:" + x + "  downY:" + y + "  列:"
					+ col + " 行:" + row);
			int downIndex = (row - 1) * 7 + col - 1;
			calendar.setTime(currentDate);
			if (isLastMonth(downIndex)) {
				calendar.set(Calendar.MONTH, -1);
			} else if (isNextMonth(downIndex)) {
				calendar.set(Calendar.MONTH, 1);
			} else {
				this.downIndex = downIndex;
			}
			calendar.set(Calendar.DAY_OF_MONTH, date[this.downIndex]);
			downDay = calendar.getTime();
			invalidate();
		}
	}

	public void clickNextMonth() {
		calendar.setTime(currentDate);
		calendar.add(Calendar.MONTH, 1);
		currentDate = calendar.getTime();
		invalidate();
	}

	public void clickLastMonth() {
		calendar.setTime(currentDate);
		calendar.add(Calendar.MONTH, -1);
		currentDate = calendar.getTime();
		invalidate();
	}

	/**
	 * 判斷是否是上個月
	 * 
	 * @param index
	 * @return
	 */
	public boolean isNextMonth(int index) {
		boolean isNext = false;
		if (index < curStartIndex) {
			isNext = true;
		}
		return isNext;
	}

	/**
	 * 判斷是否是下個月
	 * 
	 * @param index
	 * @return
	 */
	public boolean isLastMonth(int index) {
		boolean isLast = false;
		if (index >= curEndIndex) {
			isLast = true;
		}
		return isLast;
	}

	/**
	 * 根据index获得列行
	 * 
	 * @param index
	 * @return
	 */
	public int[] getRowColByIndex(int index) {
		int rowCol[] = new int[2];
		rowCol[0] = index % 7 + 1;// 列
		rowCol[1] = index / 7 + 1;// 行
		return rowCol;
	}

	public interface OnDateChoiseListener {
		public void onDateChoise();
	}

}
