package com.yannis.android.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;

import com.yannis.android.ui.test.ViewUtil;

public class RebView extends View {
	
	public static final float BOTTOM_LINES_WIDTH_SCALE=0.02f;
	
	public static final float BOTTOM_LINES_VERTICAL_PADDING_SCALE=0.04f;
	
	public static final float VERTICAL_LINES_WIDTH_SCALE=0.025f;
	
	public static final float PADDING_SCALE=0.01f;
	
	private Data[] datas={new Data("0.00公里", "累计距离"),new Data("0.0小时", "累计时间"),new Data("0.00卡路里", "累计消耗")};
	
	private int dataSize=3;
	
	private Paint textPanit;
	
	private Paint linePanit;
	
	private int w,h;
	
	private float centerTextY;
	
	private float textSize;
	
	private float tWidth;
	
	private float lineWidth;
		

	public RebView(Context context) {
		super(context);
		init();
	}

	public RebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		this.w=w;
		this.h=h;
		this.textSize=h*0.25f;
		this.tWidth=(w*(1.0f-PADDING_SCALE*2)-w*VERTICAL_LINES_WIDTH_SCALE*(dataSize-1))/dataSize;
		
		this.centerTextY=h*(1.0f-BOTTOM_LINES_WIDTH_SCALE-VERTICAL_LINES_WIDTH_SCALE*2.0f)/2.0f;
		this.lineWidth=h*BOTTOM_LINES_WIDTH_SCALE;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//画底线
		float bottomLineStartX=w*PADDING_SCALE;
		float bottonLineY=h*(1.0f-BOTTOM_LINES_VERTICAL_PADDING_SCALE-BOTTOM_LINES_WIDTH_SCALE/2.0f);
		float bottonLineEndX=w-bottomLineStartX;
		linePanit.setStrokeWidth(lineWidth);
		canvas.drawLine(bottomLineStartX, bottonLineY, bottonLineEndX, bottonLineY, linePanit);
		//画数据
		if(datas!=null){
			for(int i=0;i<dataSize;i++){
				textPanit.setTextSize(textSize);
				float centerTextX=tWidth/2.0f+w*PADDING_SCALE+(tWidth+w*VERTICAL_LINES_WIDTH_SCALE)*i;
				float titleHight=ViewUtil.getTextHeight(textPanit);
				float centerTitleY=centerTextY-titleHight/2-(textPanit.ascent()+textPanit.descent())/2.0f;
				canvas.drawText(datas[i].value, centerTextX, centerTitleY, textPanit);
				textPanit.setTextSize(textSize*0.8f);
				float centerValueY=centerTextY+ViewUtil.getTextHeight(textPanit)*1.0f+(textPanit.ascent()+textPanit.descent())/2.0f;
				canvas.drawText(datas[i].title, centerTextX, centerValueY, textPanit);
			}
		}
		//画竖线
		linePanit.setStrokeWidth(lineWidth*0.8f);
		for(int i=0;i<dataSize-1;i++){
			float startX=w*PADDING_SCALE+tWidth+w*VERTICAL_LINES_WIDTH_SCALE/2.0f+(tWidth+w*VERTICAL_LINES_WIDTH_SCALE)*i;
			float startY=h*BOTTOM_LINES_VERTICAL_PADDING_SCALE;
			float stopY=bottonLineY-h*BOTTOM_LINES_VERTICAL_PADDING_SCALE-BOTTOM_LINES_WIDTH_SCALE/2.0f;
			canvas.drawLine(startX, startY, startX, stopY, textPanit);
		}
	}
	
	private void init(){
		textPanit=new Paint();
		textPanit.setAntiAlias(true);
		textPanit.setColor(Color.WHITE);
		textPanit.setTextAlign(Align.CENTER);
		
		linePanit=new Paint();
		linePanit.setAntiAlias(true);
		linePanit.setColor(Color.WHITE);
	}
	
	public void setData(Data... datas){
		this.datas=datas;
		invalidate();
	}
	
	public class Data{
		public String title;
		public String value;
		
		public Data(String value,String title){
			this.title=title;
			this.value=value;
		}
	}

}
