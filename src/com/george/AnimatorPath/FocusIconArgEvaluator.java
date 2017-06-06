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
        
        Log.e("time", " time +"+t);
        float oneMiunsT = 1 - t;
//        int x = 
//        FocusIconArg tArg = new FocusIconArg();
        return endValue;
    }
}
