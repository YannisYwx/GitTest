package com.yannis.android.ui.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewHolder {
	protected SparseArray<View> mViews;
	protected View mConvertView;
	public  int position;
	
	public ViewHolder(Context context,ViewGroup parent, int layoutId, int position){
		this.mViews=new SparseArray<View>();
		this.mConvertView=LayoutInflater.from(context).inflate(layoutId, parent,false);
		this.position=position;
	}
	
	public static ViewHolder getViewHolder(Context context, View convertView,
			ViewGroup parent, int layoutId, int position){
		if(convertView==null){
			return new ViewHolder(context, parent, layoutId, position);
		}
		ViewHolder holder=(ViewHolder) convertView.getTag();
		holder.position=position;
		
		return holder;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId){
		View view=mViews.get(viewId);
		if(view==null){
			view=mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}
	
	public View getConvertView(){
		return mConvertView;
	}
	
}
