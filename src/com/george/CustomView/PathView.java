package com.george.CustomView;

import com.george.AnimatorPath.FocusIconArg;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.BlurMaskFilter.Blur;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 时 间: 2016/11/8 0008
 * 作 者: 郑亮
 * Q  Q : 1023007219
 */

public class PathView extends View {

    private Paint paint;
    
    public FocusIconArg mFocusIconArg;
    
    private int x;
    private int y;
    private int width;
    private int height;
    public boolean isShowFocus() {
		return showFocus;
	}

	public void setShowFocus(boolean showFocus) {
		this.showFocus = showFocus;
	}

	boolean showFocus;

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
    	setLayerType(LAYER_TYPE_SOFTWARE,null);
        paint = new Paint();
//        //抗锯齿
//        paint.setAntiAlias(true);
//        //防抖动
//        paint.setDither(true);
//        //设置画笔未实心
//        paint.setStyle(Paint.Style.STROKE);
//        //设置颜色
//        paint.setColor(Color.GREEN);
//        //设置画笔宽度
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setMaskFilter(new BlurMaskFilter(5, Blur.SOLID));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

		//        Path path = new Path();
//        path.moveTo(80, 40);
//        //path.quadTo(360, 160, 460, 360); //订单
//        path.cubicTo(900,00,600,330,80,260);
//        canvas.drawPath(path,paint);
        if(showFocus){
        	canvas.drawRoundRect(x, y, x+width, y+height, 5, 5, paint);
        }else{
        	paint.setColor(Color.TRANSPARENT);
        	canvas.drawRoundRect(x, y, x+width, y+height, 5, 5, paint);
        }
//        canvas.drawRect(x, y, x+width, y+height, paint);
    }
    
    public void setPosition(int x,int y,int width,int height){
    	this.x = x;
    	this.y = y;
    	this.width = width;
    	this.height = height;
    	Log.e("PathView", "  get height = "+height);
    }
    
    public void setFocusIconArg(FocusIconArg arg){
    	this.x = arg.x;
    	this.y = arg.y;
    	this.width = arg.width;
    	this.height =arg.height;
    	Log.e("PathView", "arg.color = "+arg.color);
    	 paint.setColor(arg.color);
    	 mFocusIconArg = arg;
    	 postInvalidate();
    }
}
