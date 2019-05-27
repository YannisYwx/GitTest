package com.yannis.android.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yannis.android.ui.R;

public class TitlebarLayout extends RelativeLayout {

	private ImageButton ibLeft,ibRight,ibOtRight;
	private TextView tvTitle;
	private RadioGroup rgGroup;
	private RadioButton rbLeft,rbRight;
	
	private Bitmap leftBitmp,rightBitmp,rightOtBitmap;
	private String title,leftTitle,rightTitle;
	private int centerMode,rightVisiable,rightOtVisiable;
	private TitleBarCheckedListener listener;
	private LRbuttonClickListener lrListener;
	private int bgColor;

	public interface TitleBarCheckedListener {
		public void leftClick();
		public void rightClick();
	}

	public void setTitleBarListener(TitleBarCheckedListener listener) {
		this.listener = listener;
	}


	public interface LRbuttonClickListener {
		public void btnLeftClick();
		public void btnRightClick();
		public void btnRightOtClick();
	}

	public void setLRListener(LRbuttonClickListener listener) {
		this.lrListener = listener;
	}

	public TitlebarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitlebarLayout);
		leftBitmp = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.TitlebarLayout_leftImage, 0));
		rightBitmp = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.TitlebarLayout_rightImage, 0));
		rightOtBitmap = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.TitlebarLayout_rightOtImage, 0));
		title = a.getString(R.styleable.TitlebarLayout_centerTitle);
		leftTitle = a.getString(R.styleable.TitlebarLayout_leftTitle);
		rightTitle = a.getString(R.styleable.TitlebarLayout_rightTitle);
		centerMode = a.getInt(R.styleable.TitlebarLayout_centerMode, 0);
		rightVisiable = a.getInt(R.styleable.TitlebarLayout_rightVisiblity, 0);
		rightOtVisiable = a.getInt(R.styleable.TitlebarLayout_rightOtVisiblity, 0);
		bgColor = a.getColor(R.styleable.TitlebarLayout_bgColor, getResources().getColor(R.color.main_color));
		a.recycle();
		LayoutInflater.from(context).inflate(R.layout.layout_title_bar, this,true);
	}

	public void setLeftVisialbe(boolean visialbe) {
		if(!visialbe) {
			ibLeft.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onFinishInflate() {
		ibLeft = (ImageButton) findViewById(R.id.ib_bar_left);
		ibRight = (ImageButton) findViewById(R.id.ib_bar_right);
		ibOtRight = (ImageButton) findViewById(R.id.ib_bar_right_ot);
		tvTitle = (TextView) findViewById(R.id.tv_bar_title);
		rgGroup = (RadioGroup) findViewById(R.id.rg_bar_group);
		rbLeft = (RadioButton)findViewById(R.id.rb_bar_left);
		rbRight = (RadioButton) findViewById(R.id.rb_bar_right);
		if(ibLeft!=null) {
			ibLeft.setImageBitmap(leftBitmp);
			ibRight.setImageBitmap(rightBitmp);
			ibOtRight.setImageBitmap(rightOtBitmap);
			tvTitle.setText(title);
			rbLeft.setText(leftTitle);
			rbRight.setText(rightTitle);
			if(centerMode == 0) {
				rgGroup.setVisibility(View.GONE);
				tvTitle.setVisibility(View.VISIBLE);
				setBackgroundColor(bgColor);
			}else {
				rgGroup.setVisibility(View.VISIBLE);
				tvTitle.setVisibility(View.GONE);
				setBackgroundColor(Color.parseColor("#F8F8FF"));
			}

			if(rightVisiable == 0) {
				ibRight.setVisibility(View.VISIBLE);
			}else {
				ibRight.setVisibility(View.GONE);
				
			}
			if(rightOtVisiable == 0) {
				ibOtRight.setVisibility(View.VISIBLE);
			}else {
				ibOtRight.setVisibility(View.GONE);
			}
			rgGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@SuppressWarnings("deprecation")
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					switch (checkedId) {
					case R.id.rb_bar_left:
						rbLeft.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_toggle_btn_fg_normal) );
						rbRight.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_toggle_btn_fg_right));
						if(listener!=null) {
							listener.leftClick();
						}

						break;
					case R.id.rb_bar_right:
						rbRight.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_toggle_btn_fg_normal));
						rbLeft.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_toggle_btn_fg_left));
						if(listener!=null) listener.rightClick();
						break;

					default:
						break;
					}
				}
			});


			ibLeft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(lrListener!=null) lrListener.btnLeftClick();
				}
			});

			ibRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(lrListener!=null) lrListener.btnRightClick();
				}
			});
			
			ibOtRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(lrListener!=null) lrListener.btnRightOtClick();
				}
			});



		}
		super.onFinishInflate();
	}
	
	public void setTitle(String title) {
		tvTitle.setText(title);
		invalidate();
	}
	
	public ImageButton getLeftImageButton() {
		return ibLeft;
	}
	public ImageButton getRightImageButton() {
		return ibRight;
	}
	
	public TextView getTitleView() {
		return tvTitle;
	}
	
	public RadioGroup getCenterGroup() {
		return rgGroup;
	}
	
	public void setibOtRightVisiable(boolean visiable){
		if(ibOtRight!=null){
			if(visiable){
				ibOtRight.setVisibility(View.VISIBLE);
			}else{
				ibOtRight.setVisibility(View.INVISIBLE);
			}
		}
	}
}
