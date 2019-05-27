package com.yannis.android.ui.widget.weelview;

import java.util.ArrayList;

import android.view.View;

import com.yannis.android.ui.R;
import com.yannis.android.ui.activity.BaseActivity;

public class WeelViewActivity extends BaseActivity {
    WheelView mWheelView;

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mWheelView = (WheelView) findViewById(R.id.wheelView);
		mWheelView.setData(getData1());

	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public int setLayoutResID() {
		// TODO Auto-generated method stub
		return R.layout.activity_weelview;
	}
	
	 private boolean mIsData1 = true;

	    /**
	     * ÷ÿ÷√ ˝æ›
	     *
	     * @param view
	     */
	    public void resetData(View view) {
	        if (mIsData1) {
	            mWheelView.resetData(getData2());
	            mWheelView.setDefault(1);
	        } else {
	            mWheelView.resetData(getData1());
	        }
	        mIsData1 = !mIsData1;
	    }

	    private ArrayList<String> getData1() {
	        ArrayList<String> mDatas = new ArrayList<String>();
	        for (int i = 0; i < 26; i++) {
	            mDatas.add(String.valueOf(((char) (97 + i))));
	        }
	        return mDatas;
	    }

	    private ArrayList<String> getData2() {
	        ArrayList<String> mDatas = new ArrayList<String>();
	        mDatas.add("A");
	        mDatas.add("B");
	        mDatas.add("C");
	        return mDatas;
	    }

}
