package com.yannis.android.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public abstract class BaseActivity extends Activity {
	public static final String ARGS_BUNDLE = "Bundle_key";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(setLayoutResID());
		init();
		initView();
		initData();
		initEvent();
	}
	/**
	 * 初始化
	 */
	public abstract void init();
	/**
	 * 初始化UI
	 */
	public abstract void initView();
	/**
	 * 注册监听
	 */
	public abstract void initEvent();
	/**
	 * 初始化数据
	 */
	public abstract void initData();
	/**
	 * 设置布局文件
	 */
	public abstract int setLayoutResID();
	
	/**
	 * 跳转页面
	 * @param context 跳转页面的Context
	 * @param clzz    待跳转页面的class
	 * @param args    跳转页面传递给待跳转页面的参数 参数key ARGS_BUNDLE
	 */
	public static void startSlef(Context context,Class<?> clzz ,Bundle args) {
		Intent intent = new Intent(context,clzz);
		if(args != null) {
			intent.putExtra(ARGS_BUNDLE, args);
		}
		context.startActivity(intent);
		intent = null;
		context = null;
	}
}
