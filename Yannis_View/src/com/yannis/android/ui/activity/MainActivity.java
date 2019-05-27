package com.yannis.android.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yannis.android.ui.R;
import com.yannis.android.ui.clock.ClockActivity;
import com.yannis.android.ui.widget.weelview.WeelViewActivity;

public class MainActivity extends BaseActivity implements OnClickListener {
	public Button btn_toRulerView;
	public Button btn_toPointLineView;
	public Button btn_toCalendarView;
	public Button btn_toTextSwichView;
	public Button btn_toCircleImageView;
	public Button btn_toScanView;
	public Button btn_toProgressView;
	public Button btn_toProgressView1;
	public Button btn_toRebView;
	public Button btn_toDataScrollView;
	public Button btn_toClock;
	public Button btn_toWeelView;

	public static final String VIEW_INDEX = "_view_index";
	private static final String TAG = "fuck";

	@Override
	public void init() {
		// TODO Auto-generated method stub
		// px--dp
		float d = getResources().getDisplayMetrics().density;
		// px--sp
		float s = getResources().getDisplayMetrics().scaledDensity;
		Log.e(TAG, "dp:" + d + "---sp:" + s);
		getDisplayInfomation();
		getDensity();
	}

	
	@SuppressLint("NewApi")
	private void getDisplayInfomation() {  
	    Point point = new Point();  
	    getWindowManager().getDefaultDisplay().getSize(point);  
	    Log.d(TAG,"the screen size is "+point.toString());  
	    getWindowManager().getDefaultDisplay().getRealSize(point);  
	    Log.d(TAG,"the screen real size is "+point.toString());  
	}  
	
	private void getDensity() {  
	    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();  
	    Log.d(TAG,"Density is "+displayMetrics.density+" densityDpi is "+displayMetrics.densityDpi+" height: "+displayMetrics.heightPixels+  
	        " width: "+displayMetrics.widthPixels);  
	}  

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		btn_toRulerView = (Button) findViewById(R.id.toRulerView);
		btn_toPointLineView = (Button) findViewById(R.id.toPointLineView);
		btn_toCalendarView = (Button) findViewById(R.id.toCalendarView);
		btn_toTextSwichView = (Button) findViewById(R.id.toTextSwichView);
		btn_toCircleImageView = (Button) findViewById(R.id.toCircleImageView);
		btn_toScanView = (Button) findViewById(R.id.toScanView);
		btn_toProgressView = (Button) findViewById(R.id.toProgressView);
		btn_toProgressView1 = (Button) findViewById(R.id.toProgressView1);
		btn_toRebView = (Button) findViewById(R.id.toRebView);
		btn_toDataScrollView = (Button) findViewById(R.id.toDataScrollView);
		btn_toClock = (Button) findViewById(R.id.toClock);
		btn_toWeelView= (Button) findViewById(R.id.toWeelView);
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
		btn_toRulerView.setOnClickListener(this);
		btn_toPointLineView.setOnClickListener(this);
		btn_toCalendarView.setOnClickListener(this);
		btn_toTextSwichView.setOnClickListener(this);
		btn_toCircleImageView.setOnClickListener(this);
		btn_toScanView.setOnClickListener(this);
		btn_toProgressView.setOnClickListener(this);
		btn_toProgressView1.setOnClickListener(this);
		btn_toRebView.setOnClickListener(this);
		btn_toDataScrollView.setOnClickListener(this);
		btn_toClock.setOnClickListener(this);
		btn_toWeelView.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public int setLayoutResID() {
		// TODO Auto-generated method stub
		return R.layout.activity_main;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		if(v.getId()==R.id.toWeelView){
			startActivity(new Intent(this, WeelViewActivity.class));
			return;
		}
		switch (v.getId()) {
		case R.id.toRulerView:
			bundle.putInt(VIEW_INDEX, 1);
			break;
		case R.id.toPointLineView:
			bundle.putInt(VIEW_INDEX, 2);
			break;
		case R.id.toCalendarView:
			bundle.putInt(VIEW_INDEX, 3);
			break;
		case R.id.toTextSwichView:
			bundle.putInt(VIEW_INDEX, 4);
			break;
		case R.id.toCircleImageView:
			bundle.putInt(VIEW_INDEX, 5);
			break;
		case R.id.toScanView:
			bundle.putInt(VIEW_INDEX, 6);
			break;
		case R.id.toProgressView:
			bundle.putInt(VIEW_INDEX, 7);
			break;
		case R.id.toProgressView1:
			bundle.putInt(VIEW_INDEX, 8);
			break;
		case R.id.toRebView:
			bundle.putInt(VIEW_INDEX, 9);
			break;
		case R.id.toDataScrollView:
			bundle.putInt(VIEW_INDEX, 10);
			break;
		case R.id.toClock:
			bundle.putInt(VIEW_INDEX, 11);
		default:
			break;
		}
		startSlef(this, UIActivity.class, bundle);
	}

}
