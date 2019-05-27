package com.yannis.android.ui.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * �����ؼ� ���ܣ���õ�ѡ����������
 * 
 */
public class CalendarView extends View implements View.OnTouchListener {
	private final static String TAG = "anCalendar";
	private Date selectedStartDate;
	private Date selectedEndDate;
	private Date curDate; // ��ǰ������ʾ����
	private Date today; // ���������������ʾ��ɫ
	private Date downDate; // ��ָ����״̬ʱ��ʱ����
	private Date showFirstDate, showLastDate; // ������ʾ�ĵ�һ�����ں����һ������
	private int downIndex; // ���µĸ�������
	private Calendar calendar;
	private Surface surface;
	private int[] date = new int[42]; // ������ʾ����
	private int curStartIndex, curEndIndex; // ��ǰ��ʾ��������ʼ������
	private boolean completed = false; // Ϊfalse��ʾֻѡ���˿�ʼ���ڣ�true��ʾ��������Ҳѡ����
	private boolean isSelectMore = false;
	private int canlendarViewBgColor= Color.parseColor("#30d8c2");
	private SimpleDateFormat format;
	
	private int currentMonth;

	//���ؼ����ü����¼�
	private OnItemClickListener onItemClickListener;
	
	public CalendarView(Context context) {
		super(context);
		init();
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@SuppressLint("ClickableViewAccessibility")
	private void init() {
		curDate = selectedStartDate = selectedEndDate = today = new Date();
		calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		surface = new Surface();
		setBackgroundColor(canlendarViewBgColor);
		setOnTouchListener(this);
		format = new SimpleDateFormat("yyyy-MM-dd");

		currentMonth=Integer.parseInt(format.format(today).split("-")[1]);
//		Logger.d("currentMonth��"+currentMonth);
	}
	
//	public void setCurrentMonth(int month){
//		currentMonth=month;
//		downIndex=-1;
//		this.invalidate();
//	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		surface.width = getResources().getDisplayMetrics().widthPixels;
		surface.height = (int) (getResources().getDisplayMetrics().heightPixels*3/8);
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
		Log.d(TAG, "[onLayout] changed:"
				+ (changed ? "new size" : "not change") + " left:" + left
				+ " top:" + top + " right:" + right + " bottom:" + bottom);
		if (changed) {
			surface.init();
		}
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(TAG, "onDraw");
		// ��������
		calculateDate();
//		// ����״̬��ѡ��״̬����ɫ
//		drawDownOrSelectedBg(canvas);
		// write date number
		// today index
		int todayIndex = -1;
		calendar.setTime(curDate);
		String curYearAndMonth = calendar.get(Calendar.YEAR) + ""
				+ calendar.get(Calendar.MONTH);
		calendar.setTime(today);
		String todayYearAndMonth = calendar.get(Calendar.YEAR) + ""
				+ calendar.get(Calendar.MONTH);
		if (curYearAndMonth.equals(todayYearAndMonth)) {
			int todayNumber = calendar.get(Calendar.DAY_OF_MONTH);
			todayIndex = curStartIndex + todayNumber - 1;
		}
		for (int i = 0; i < 42; i++) {
			int color = surface.textColor;
			if (isLastMonth(i)) {
				color = surface.borderColor;
			} else if (isNextMonth(i)) {
				color = surface.borderColor;
			}
			if (todayIndex != -1 && i == todayIndex) {
				color = surface.todayNumberColor;
			}
			drawCellText(canvas, i, date[i] + "", color);
		}
		// ����״̬��ѡ��״̬����ɫ
		drawDownOrSelectedBg(canvas);
		super.onDraw(canvas);
	}

	public String calculateWeek(){
		int weekIndex=-1;
		if(downIndex<=6){
			weekIndex=downIndex;
		}else{
			weekIndex=downIndex%7;
		}
		
		String week="";
		switch (weekIndex) {
		case 0:
			week="������";
			break;
		case 1:
			week="����һ";
			break;
		case 2:
			week="���ڶ�";
			break;
		case 3:
			week="������";
			break;
		case 4:
			week="������";
			break;
		case 5:
			week="������";
			break;
		case 6:
			week="������";
			break;
			
		default:
			break;
		}
		return week;
	}
	
	
	
	
	
	private void calculateDate() {
		calendar.setTime(curDate);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
		Log.d(TAG, "day in week:" + dayInWeek);
		int monthStart = dayInWeek;
		if (monthStart == 1) {
			monthStart = 8;
		}
		monthStart -= 1;  //����Ϊ��ͷ-1��������һΪ��ͷ-2
		curStartIndex = monthStart;
		date[monthStart] = 1;
		// last month
		if (monthStart > 0) {
			calendar.set(Calendar.DAY_OF_MONTH, 0);
			int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
			for (int i = monthStart - 1; i >= 0; i--) {
				date[i] = dayInmonth;
				dayInmonth--;
			}
			calendar.set(Calendar.DAY_OF_MONTH, date[0]);
		}
		showFirstDate = calendar.getTime();
		// this month
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 0);
		int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
		for (int i = 1; i < monthDay; i++) {
			date[monthStart + i] = i + 1;
		}
		curEndIndex = monthStart + monthDay;
		// next month
		for (int i = monthStart + monthDay; i < 42; i++) {
			date[i] = i - (monthStart + monthDay) + 1;
		}
		if (curEndIndex < 42) {
			// ��ʾ����һ�µ�
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		calendar.set(Calendar.DAY_OF_MONTH, date[41]);
		showLastDate = calendar.getTime();
	}

	/**
	 * 
	 * @param canvas
	 * @param index
	 * @param text
	 */
	private void drawCellText(Canvas canvas, int index, String text, int color) {
		int x = getXByIndex(index);
		int y = getYByIndex(index);
		surface.datePaint.setColor(color);
		float cellY = surface.monthHeight + surface.weekHeight + (y - 1)
				* surface.cellHeight + surface.cellHeight * 2 / 4f - (surface.datePaint.ascent() + surface.datePaint.descent())/2;
		float cellX = (surface.cellWidth * (x - 1))
				+ (surface.cellWidth - surface.datePaint.measureText(text))
				/ 2f;
		canvas.drawText(text, cellX, cellY, surface.datePaint);
	}

	/**
	 * 
	 * @param canvas
	 * @param index
	 * @param color
	 */
	private void drawCellBg(Canvas canvas, int index, int color) {
//		Logger.d("---------------------------");
//		Logger.d("drawCellBg index:"+index);
//		Logger.d("drawCellBg currentStartIndex:"+curStartIndex+"  currentEndIndex:"+curEndIndex);
//		Logger.d("---------------------------");
		
		if(isLastMonth(index)||isNextMonth(index)){
			return;
		}
		
		int x = getXByIndex(index);
		int y = getYByIndex(index);
		surface.cellBgPaint.setColor(Color.GRAY);
		float left = surface.cellWidth * (x - 1) + surface.borderWidth;
		float top = surface.monthHeight + surface.weekHeight + (y - 1)
				* surface.cellHeight + surface.borderWidth;
		
		float cx=(left*2+surface.cellWidth)/2;
		float cy=(2*top+surface.cellHeight)/2;
		float r=surface.cellHeight/2;
		surface.cellBgPaint.setColor(color);
		canvas.drawCircle(cx,  
				 (float)(cy*1.0),  (float)(r*0.8),
				 surface.cellBgPaint);
		if((curDate.equals(downDate)||downDate==null)&& (downIndex == index || downIndex == 0)){
			drawCellText(canvas, index, date[index]+"", Color.parseColor("#30d8c2"));
		}else{
			drawCellText(canvas, index, date[index]+"", Color.parseColor("#30d8c2"));
		}
	}

	private void drawDownOrSelectedBg(Canvas canvas) {
		// down and not up
		if (downDate != null) {
			drawCellBg(canvas, downIndex, surface.cellDownColor);
		}
		// selected bg color
		if (!selectedEndDate.before(showFirstDate)
				&& !selectedStartDate.after(showLastDate)) {
			int[] section = new int[] { -1, -1 };
			calendar.setTime(curDate);
			calendar.add(Calendar.MONTH, -1);
			findSelectedIndex(0, curStartIndex, calendar, section);
			if (section[1] == -1) {
				calendar.setTime(curDate);
				findSelectedIndex(curStartIndex, curEndIndex, calendar, section);
			}
			if (section[1] == -1) {
				calendar.setTime(curDate);
				calendar.add(Calendar.MONTH, 1);
				findSelectedIndex(curEndIndex, 42, calendar, section);
			}
			if (section[0] == -1) {
				section[0] = 0;
			}
			if (section[1] == -1) {
				section[1] = 41;
			}
			for (int i = section[0]; i <= section[1]; i++) {
				drawCellBg(canvas, i, surface.cellSelectedColor);
			}
		}
	}

	private void findSelectedIndex(int startIndex, int endIndex,
			Calendar calendar, int[] section) {
		for (int i = startIndex; i < endIndex; i++) {
			calendar.set(Calendar.DAY_OF_MONTH, date[i]);
			Date temp = calendar.getTime();
			if (temp.compareTo(selectedStartDate) == 0) {
				section[0] = i;
			}
			if (temp.compareTo(selectedEndDate) == 0) {
				section[1] = i;
				return;
			}
		}
	}

	public Date getSelectedStartDate() {
		return selectedStartDate;
	}

	public Date getSelectedEndDate() {
		return selectedEndDate;
	}

	private boolean isLastMonth(int i) {
		if (i < curStartIndex) {
			return true;
		}
		return false;
	}

	private boolean isNextMonth(int i) {
		if (i >= curEndIndex) {
			return true;
		}
		return false;
	}

	private int getXByIndex(int i) {
		return i % 7 + 1; // 1 2 3 4 5 6 7
	}

	private int getYByIndex(int i) {
		return i / 7 + 1; // 1 2 3 4 5 6
	}
	
	
	

	// ��õ�ǰӦ����ʾ������
	public String getYearAndmonth() {
		calendar.setTime(curDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		return year + "-" + month;
	}
	
	//��һ��
	public String clickLeftMonth(){
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, -1);
		curDate = calendar.getTime();
		invalidate();
		return getYearAndmonth();
	}
	//��һ��
	public String clickRightMonth(){
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, 1);
		curDate = calendar.getTime();
		invalidate();
		return getYearAndmonth();
	}
	
	//��������ʱ��
	public void setCalendarData(Date date){
		calendar.setTime(date);
		invalidate();
	}
	
	//��ȡ����ʱ��
	public void getCalendatData(){
		calendar.getTime();	
	}
	
	//�����Ƿ��ѡ
	public boolean isSelectMore() {
		return isSelectMore;
	}

	public void setSelectMore(boolean isSelectMore) {
		this.isSelectMore = isSelectMore;
	}

	private void setSelectedDateByCoor(float x, float y) {
		// cell click down
		if (y > surface.monthHeight + surface.weekHeight) {
			int m = (int) (Math.floor(x / surface.cellWidth) + 1);
			int n = (int) (Math.floor((y - (surface.monthHeight + surface.weekHeight))
							/ Float.valueOf(surface.cellHeight)) + 1);
			downIndex = (n - 1) * 7 + m - 1;
			Log.d(TAG, "downIndex:" + downIndex);
			calendar.setTime(curDate);
			if (isLastMonth(downIndex)) {
				calendar.add(Calendar.MONTH, -1);
			} else if (isNextMonth(downIndex)) {
				calendar.add(Calendar.MONTH, 1);
			}
			calendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
			downDate = calendar.getTime();
		}
		invalidate();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setSelectedDateByCoor(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:
			if (downDate != null) {
				if(isSelectMore){
					if (!completed) {
						if (downDate.before(selectedStartDate)) {
							selectedEndDate = selectedStartDate;
							selectedStartDate = downDate;
						} else {
							selectedEndDate = downDate;
						}
						completed = true;
						//��Ӧ�����¼�
//						Logger.d("���-----!completed");
						onItemClickListener.OnItemClick(selectedStartDate,selectedEndDate,downDate,calculateWeek());
					} else {
						selectedStartDate = selectedEndDate = downDate;
						completed = false;
					}
				}
				
				else{
					selectedStartDate = selectedEndDate = downDate;
					//��Ӧ�����¼�
//					Logger.d("���-----");
					if(onItemClickListener!=null){
						if(isLastMonth(downIndex)||isNextMonth(downIndex)){
							return true;
						}
						onItemClickListener.OnItemClick(selectedStartDate,selectedEndDate,downDate,calculateWeek());						
					}
				}
				
				
				invalidate();
			}
			
			break;
		}
		return true;
	}
	
	//���ؼ����ü����¼�
	public void setOnItemClickListener(OnItemClickListener onItemClickListener){
		this.onItemClickListener =  onItemClickListener;
	}
	//�����ӿ�
	public interface OnItemClickListener {
		void OnItemClick(Date selectedStartDate,Date selectedEndDate, Date downDate,String week);
	}

	/**
	 * 
	 * 1. ���ֳߴ� 2. ������ɫ����С 3. ��ǰ���ڵ���ɫ��ѡ���������ɫ
	 */
	private class Surface {
		public int width; // �����ؼ��Ŀ��
		public int height; // �����ؼ��ĸ߶�
		public float monthHeight; // ��ʾ�µĸ߶�
		//public float monthChangeWidth; // ��һ�¡���һ�°�ť���
		public float weekHeight; // ��ʾ���ڵĸ߶�
		public float cellWidth; // ���ڷ�����
		public float cellHeight; // ���ڷ���߶�	
		public float borderWidth;
		private int textColor = Color.WHITE;
		//private int textColorUnimportant = Color.parseColor("#666666");
		private int borderColor = Color.parseColor("#CCCCCC");
		public int todayNumberColor = Color.parseColor("#FE4B2A");
		public int cellDownColor = Color.parseColor("#FFFFFF");
		public int cellSelectedColor = Color.parseColor("#FFFFFF");

		public Paint datePaint;
		public Paint cellBgPaint;
		public void init() {
			monthHeight = 0;//(float) ((temp + temp * 0.3f) * 0.6);
			//monthChangeWidth = monthHeight * 1.5f;
			weekHeight = (float) (0);
			cellHeight = (height - monthHeight - weekHeight) / 6f;
			cellWidth = width / 7f;

			datePaint = new Paint();
			datePaint.setColor(textColor);
			datePaint.setAntiAlias(true);
			
			
			
			float cellTextSize = cellHeight * 0.4f;
			datePaint.setTextSize(cellTextSize);
			datePaint.setTypeface(Typeface.DEFAULT);

			cellBgPaint = new Paint();
			cellBgPaint.setAntiAlias(true);
			cellBgPaint.setStyle(Paint.Style.FILL);
			cellBgPaint.setColor(cellSelectedColor);
		}
	}
}
