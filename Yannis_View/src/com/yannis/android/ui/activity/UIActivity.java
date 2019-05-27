package com.yannis.android.ui.activity;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.yannis.android.ui.R;
import com.yannis.android.ui.test.SportPieView;
import com.yannis.android.ui.widget.CalendarView;
import com.yannis.android.ui.widget.CircleImageView;
import com.yannis.android.ui.widget.ClockView;
import com.yannis.android.ui.widget.DataScrollView;
import com.yannis.android.ui.widget.PointLineView;
import com.yannis.android.ui.widget.ProgressView;
import com.yannis.android.ui.widget.ProgressView1;
import com.yannis.android.ui.widget.RebView;
import com.yannis.android.ui.widget.RulerView;
import com.yannis.android.ui.widget.ScanView;
import com.yannis.android.ui.widget.TextSwithView;

public class UIActivity extends BaseActivity {
	private RulerView rulerView;
	private PointLineView pointLineView;
	private CalendarView calendarView;
	private TextSwithView textSwithView;
	private CircleImageView circleImageView;
	private ScanView scanView;
	private ImageView radar;
	private ProgressView progressView;
	private ProgressView1 progressView1;
	private RebView rebView;
	
	private SportPieView sportPieView;
	
	private DataScrollView dataScrollView;
	
	private ClockView clockView;
	
	private int _index;
	public UIActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		_index=getIntent().getBundleExtra(ARGS_BUNDLE).getInt(MainActivity.VIEW_INDEX);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		radar=(ImageView) findViewById(R.id.radar);
		rulerView=(RulerView) findViewById(R.id.ui_rulerView);
		pointLineView=(PointLineView) findViewById(R.id.ui_pointLineView);
		calendarView=(CalendarView) findViewById(R.id.ui_calendarView);
		textSwithView=(TextSwithView) findViewById(R.id.ui_textSwithView);
		circleImageView=(CircleImageView) findViewById(R.id.ui_circleImageView);
		scanView=(ScanView) findViewById(R.id.ui_scanView);
		progressView=(ProgressView) findViewById(R.id.progressView);
		progressView1=(ProgressView1) findViewById(R.id.progressView1);
		dataScrollView=(DataScrollView) findViewById(R.id.dataScrollView);
//		rulerView.setData((int)(60*7.5));
		rulerView.setData(5000);
		
		sportPieView=(SportPieView) findViewById(R.id.ui_sportPieView);
//		sportPieView.setGoal(20000);
//		sportPieView.setSteps(19000, true);
		sportPieView.startAnim(85);
		Animation rotate=AnimationUtils.loadAnimation(this, R.anim.restart_rotate);
		radar.setAnimation(rotate);
		
		rebView=(RebView) findViewById(R.id.rebView);
		clockView=(ClockView) findViewById(R.id.clockView);
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		switch (_index) {
		case 1:
			rulerView.setVisibility(View.VISIBLE);
			break;
		case 2:
			pointLineView.setVisibility(View.VISIBLE);
			break;
		case 3:
			calendarView.setVisibility(View.VISIBLE);
			break;
		case 4:
			textSwithView.setVisibility(View.VISIBLE);
			break;
		case 5:
			circleImageView.setVisibility(View.VISIBLE);
			break;
		case 6:
			scanView.setVisibility(View.VISIBLE);
			scanView.showRetryView();
			break;
		case 7:
			progressView.setVisibility(View.VISIBLE);
			break;
		case 8:
			progressView1.setVisibility(View.VISIBLE);
			break;
		case 9:
			rebView.setVisibility(View.VISIBLE);
			break;
		case 10:
			dataScrollView.setVisibility(View.VISIBLE);
			break;
		case 11:
			clockView.setVisibility(View.VISIBLE);
			break;
			
		default:
			break;
		}
	}

	@Override
	public int setLayoutResID() {
		// TODO Auto-generated method stub
		return R.layout.activity_ui;
	}

}
