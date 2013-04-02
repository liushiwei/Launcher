package com.android.launcher2;

import com.android.launcher.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetVideo extends AppWidgetProvider {
	private static final String TAG="WidgetNews";
	private static final String PKG = "com.carit.vedio.media";
	private static final String CLS = "com.carit.vedio.media.Gallery";
	private static final String WIDGET_APP_ACTION="com.android.launcher.intent.action.VIDEO";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_video);
		setOnClickCallback(context,remoteViews);
		updateAppWidget(context,remoteViews,appWidgetIds);
	}
	
	void setOnClickCallback(Context context,RemoteViews remoteViews){
		Intent intent = new Intent();
		intent.setAction(WIDGET_APP_ACTION);
		remoteViews.setOnClickPendingIntent(R.id.widget_img, PendingIntent.getBroadcast(context, 0, intent, 0));
	}
	
	void updateAppWidget(Context context,RemoteViews remoteViews,int[] appWidgetIds){
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context); 
		if(appWidgetIds == null){
			appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(
                context, WidgetVideo.class));
		}
		
		if(appWidgetIds == null){
			appWidgetManager.updateAppWidget(new ComponentName(
					context,WidgetVideo.class),remoteViews);
		}else{
			appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
		}
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
	}
	
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
	}
		
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		Log.d(TAG,"onReceive getAction="+intent.getAction());
		if(intent.getAction().equals(WIDGET_APP_ACTION) == true){
			try{
				Intent it = new Intent();
				it.setClassName(PKG, CLS);
				it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(it);			
			}catch (Exception e) {
				e.printStackTrace();
			}		
		}
	}
}