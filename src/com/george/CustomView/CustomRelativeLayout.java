package com.george.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.george.AnimatorPath.IconPoint;
import com.george.AnimatorPath.PathPoint;

public class CustomRelativeLayout extends RelativeLayout {
	
	private IconPoint mIconPoint;
	
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
	
	

}
