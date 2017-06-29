package com.george.AnimatorPath;


import android.animation.TypeEvaluator;
import android.util.Log;

/**
 * Created by zhengliang on 2016/10/15 0015.
 * 估值器类,实现坐标点的计算
 */

public class FocusIconArgEvaluator implements TypeEvaluator<FocusIconArg> {

    /**
     * @param t          :执行的百分比
     * @param startValue : 起点
     * @param endValue   : 终点
     * @return
     */
    @Override
    public FocusIconArg evaluate(float t, FocusIconArg startValue, FocusIconArg endValue) {
        
         
         FocusIconArg result = new FocusIconArg((int)(startValue.x+(endValue.x-startValue.x)*t),(int)(startValue.y+(endValue.y-startValue.y)*t),(int)(startValue.width+(endValue.width-startValue.width)*t),(int)(startValue.height+(endValue.height-startValue.height)*t),(int)(startValue.color+(endValue.color-startValue.color)*t));
        
        return result;
    }
}