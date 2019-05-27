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
	 * ��ʼ��
	 */
	public abstract void init();
	/**
	 * ��ʼ��UI
	 */
	public abstract void initView();
	/**
	 * ע�����
	 */
	public abstract void initEvent();
	/**
	 * ��ʼ������
	 */
	public abstract void initData();
	/**
	 * ���ò����ļ�
	 */
	public abstract int setLayoutResID();
	
	/**
	 * ��תҳ��
	 * @param context ��תҳ���Context
	 * @param clzz    ����תҳ���class
	 * @param args    ��תҳ�洫�ݸ�����תҳ��Ĳ��� ����key ARGS_BUNDLE
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
