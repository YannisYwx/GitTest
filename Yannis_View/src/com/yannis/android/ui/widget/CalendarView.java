package com.yannis.android.ui.widget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.yannis.android.ui.Logger;
import com.yannis.android.ui.R;

import android.content.Context;
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
	private Surface surface;

	private Date currentDate;
	private Date today;
	private Date downDay;
	private int todayIndex = -1;// 今日的下标
	private int downIndex = -1; // 按下的格子索引
	private int curStartIndex, curEndIndex;
	private int canlendarViewBgColor = Color.parseColor("#30d8c2");// view的背景
	private int[] date = new int[42]; // 日历显示数字

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
		init(context);
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		surface.width = getResources().getDisplayMetrics().widthPixels;
		surface.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.45f);
		widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.width,
				View.MeasureSpec.EXACTLY);
		heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.height,
				View.MeasureSpec.EXACTLY);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		if (changed) {
			surface.init();
		}
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
		surface.paint.setColor(Color.parseColor("#22000000"));
		canvas.drawRect(0, surface.weekHeight, surface.width,
				surface.weekHeight + surface.ymdHeight, surface.paint);
		// 画星期
		surface.paint.setColor(Color.WHITE);
		surface.paint.setTextSize(surface.textSize * 1.2F);
		for (int i = 0; i < surface.weeks.length; i++) {
			float wx = i * surface.cellWidth + surface.cellWidth / 2f;
			float wy = surface.weekHeight / 2f
					- (surface.paint.ascent() + surface.paint.descent()) / 2
					+ surface.ymdHeight;
			canvas.drawText(surface.weeks[i], wx, wy, surface.paint);
		}
		canvas.drawBitmap(surface.left, surface.bitmapSize * 0.25f,
				surface.ymdHeight / 2f - surface.bitmapSize / 2f, surface.paint);
		canvas.drawBitmap(surface.right, surface.width - surface.bitmapSize
				- surface.bitmapSize * 0.25f, surface.ymdHeight / 2f
				- surface.bitmapSize / 2f, surface.paint);
		
		downYMD = sdf_ym.format(currentDate);
		canvas.drawText(downYMD, surface.width / 2, surface.ymdHeight / 2f
				- (surface.paint.ascent() + surface.paint.descent()) / 2,
				surface.paint);
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
				color = surface.nextOrLastMonthDayColor;
			} else if (i == todayNum && isThisMonth) {
				color = surface.todayTextColor;
			} else {
				color = surface.normalTextColor;
			}
			surface.paint.setColor(color);
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
		float cellY = (row - 1) * surface.cellHeight + surface.cellHeight / 2f
				- (surface.paint.ascent() + surface.paint.descent()) / 2
				+ surface.weekHeight + surface.ymdHeight;
		float cellX = (surface.cellWidth * (col - 1)) + surface.cellWidth / 2f;
		/*
		 * float cellX = (surface.cellWidth * (col - 1))+ (surface.cellWidth -
		 * surface.paint.measureText(text))/ 2f;
		 */
		canvas.drawText(text, cellX, cellY, surface.paint);
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
		float cx = (col - 1) * surface.cellWidth + surface.cellWidth / 2;
		float cy = (row - 1) * surface.cellHeight + surface.cellHeight / 2
				+ surface.weekHeight + surface.ymdHeight;
		float radius = Math.min(surface.cellWidth / 2, surface.cellHeight / 2) * 0.9f;
		surface.paint.setColor(Color.WHITE);
		canvas.drawCircle(cx, cy, radius, surface.paint);

		// 画数字
		surface.paint.setColor(canlendarViewBgColor);
		float cellY = (row - 1) * surface.cellHeight + surface.cellHeight / 2f
				- (surface.paint.ascent() + surface.paint.descent()) / 2
				+ surface.weekHeight + surface.ymdHeight;
		float cellX = (surface.cellWidth * (col - 1)) + surface.cellWidth / 2f;
		canvas.drawText(date[downIndex] + "", cellX, cellY, surface.paint);
		// 画title
		surface.paint.setTextSize(surface.textSize * 1.3f);
		surface.paint.setColor(Color.WHITE);

//		downYMD = sdf_ym.format(currentDate) + "-"
//				+ String.format("%02d", date[downIndex]);
//		canvas.drawText(downYMD, surface.width / 2, surface.ymdHeight / 2f
//				- (surface.paint.ascent() + surface.paint.descent()) / 2,
//				surface.paint);

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
		surface = new Surface();
		calendar.setTime(today);
		todayMonthYear = sdf_ym.format(today);

		setBackgroundColor(canlendarViewBgColor);

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
		if (y >= surface.leftPosition[2] && y <= surface.leftPosition[3]) {
			if (x >= surface.leftPosition[0] && x <= surface.leftPosition[1]) {
				Toast.makeText(context, "上一个月", Toast.LENGTH_LONG).show();
				clickLastMonth();
				return;
			}
		}

		// 下一个月
		if (y >= surface.rightPosition[2] && y <= surface.rightPosition[3]) {
			if (x >= surface.rightPosition[0] && x <= surface.rightPosition[1]) {
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
		y -= (surface.weekHeight + surface.ymdHeight);
		if (y > 0) {
			int col = (int) (Math.floor(x / surface.cellWidth) + 1);// 列
			int row = (int) (Math.floor(y / Float.valueOf(surface.cellHeight)) + 1);// 行
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

	public class Surface {
		public int width, height;// 日历控件的宽高
		public float weekHeight;// 星期的高度
		public float ymdHeight;// 年月日的高度
		public float cellWidth, cellHeight;// 日期方框宽高
		public float textSize;// 字体大小
		public int nextOrLastMonthDayColor = 0xFFAAAAAA;// 其他日期的顏色
		public int normalTextColor = Color.WHITE;// 未选中时日期字体的颜色
		public int choiseTextColor = 0xffffffff;// 选中的字体颜色
		public int viewBgColor = Color.parseColor("#30d8c2");// view的背景色
		public int todayTextColor = 0xFFFC8C5C;
		public int bitmapSize;// bitmap宽高
		public Bitmap left, right;
		public float[] leftPosition = new float[4];
		public float[] rightPosition = new float[4];
		public String[] weeks = { "日", "一", "二", "三", "四", "五", "六" };
		public Paint paint;

		public void init() {
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

			float bitmapYmin = surface.ymdHeight / 2f - bitmapSize / 2f;
			float bitmapYmax = bitmapYmin + bitmapSize;
			leftPosition[0] = bitmapSize*0.25f;
			leftPosition[1] = bitmapSize*1.25f;
			leftPosition[2] = bitmapYmin;
			leftPosition[3] = bitmapYmax;

			rightPosition[0] = surface.width - 1.25f * bitmapSize;
			rightPosition[1] = surface.width - 0.25f * bitmapSize;
			rightPosition[2] = bitmapYmin;;
			rightPosition[3] = bitmapYmax;
		}
	}

	public interface OnDateChoiseListener {
		public void onDateChoise();
	}

}
