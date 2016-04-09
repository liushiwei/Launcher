package com.android.launcher2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.launcher.R;

public class NaviWidgetProvider extends AppWidgetProvider {
	 public static final String TAG = "MyAppWidgetProvider";  
	    public static final String CLICK_ACTION = "";  
	    private static RemoteViews mRemoteViews;  
	  
	    /** 
	     * 每删除一次窗口小部件就调用一次 
	     */  
	    @Override  
	    public void onDeleted(Context context, int[] appWidgetIds) {  
	        super.onDeleted(context, appWidgetIds);  
	        Log.i(TAG, "onDeleted");  
	    }  
	  
	    /** 
	     * 当最后一个该窗口小部件删除时调用该方法，注意是最后一个 
	     */  
	    @Override  
	    public void onDisabled(Context context) {  
	        super.onDisabled(context);  
	        Log.i(TAG, "onDisabled");  
	    }  
	  
	    /** 
	     * 当该窗口小部件第一次添加到桌面时调用该方法，可添加多次但只第一次调用 
	     */  
	    @Override  
	    public void onEnabled(Context context) {  
	        super.onEnabled(context);  
	        Log.i(TAG, "onEnabled");  
	    }  
	  
	    /** 
	     * 接收窗口小部件点击时发送的广播 
	     */  
	    @Override  
	    public void onReceive(final Context context, Intent intent) {  
	        super.onReceive(context, intent);  
	        Log.i(TAG, "onReceive : action = " + intent.getAction());  
	    }  
	  
	    /** 
	     * 每次窗口小部件被点击更新都调用一次该方法 
	     */  
	    @Override  
	    public void onUpdate(Context context, AppWidgetManager appWidgetManager,  
	            int[] appWidgetIds) {  
	        super.onUpdate(context, appWidgetManager, appWidgetIds);  
	        Log.i(TAG, "onUpdate");  
	  
	        final int counter = appWidgetIds.length;  
	        Log.i(TAG, "counter = " + counter);  
	        for (int i = 0; i < counter; i++) {  
	            int appWidgetId = appWidgetIds[i];  
	            onWidgetUpdate(context, appWidgetManager, appWidgetId);  
	        }  
	  
	    }  
	  
	    /** 
	     * 窗口小部件更新 
	     *  
	     * @param context 
	     * @param appWidgeManger 
	     * @param appWidgetId 
	     */  
	    private void onWidgetUpdate(Context context,  
	            AppWidgetManager appWidgeManger, int appWidgetId) {  
	  
	        Log.i(TAG, "appWidgetId = " + appWidgetId);  
	        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_navit);  
	  
	        // "窗口小部件"点击事件发送的Intent广播  
	        Intent intentClick = new Intent("com.carit.key.navi");  
	        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentClick, 0);  
	        mRemoteViews.setOnClickPendingIntent(R.id.widget_img, pendingIntent);  
	        appWidgeManger.updateAppWidget(appWidgetId, mRemoteViews);  
	    }  
	  
	  
}
