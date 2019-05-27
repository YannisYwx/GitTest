package com.yannis.android.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class CommonAdatper<T> extends BaseAdapter {
	private Context mContext;
	private int mLayoutId;
	private List<T> mDatas;
	
	public CommonAdatper(Context context,int layoutId,List<T> datas){
		this.mContext=context;
		this.mLayoutId=layoutId;
		this.mDatas=datas;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder=ViewHolder.getViewHolder(mContext, convertView, parent, mLayoutId, position);
		convert(holder, mDatas.get(position));
		return holder.getConvertView();
	}
	
	public abstract void convert(ViewHolder holder,T object);

}
