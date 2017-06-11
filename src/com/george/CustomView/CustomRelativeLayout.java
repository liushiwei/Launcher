package com.george.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.george.AnimatorPath.IconPoint;
import com.george.AnimatorPath.PathPoint;

public class CustomRelativeLayout extends RelativeLayout {
	
	private IconPoint mIconPoint;
	private int car_bg;
	private int color;
	
	public CustomRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public void setPosition(PathPoint newLoc){
		setTranslationX(newLoc.mX);
    	setTranslationY(newLoc.mY);
	}


	public IconPoint getIconPoint() {
		return mIconPoint;
	}


	public void setIconPoint(IconPoint mIconPoint) {
		this.mIconPoint = mIconPoint;
	}


	public int getCarBg() {
		return car_bg;
	}


	public void setCarBg(int car_bg) {
		this.car_bg = car_bg;
	}


	public int getColor() {
		return color;
	}


	public void setColor(int color) {
		this.color = color;
	}
	
	

}
