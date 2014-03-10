package com.android.launcher2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.android.launcher.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetNavit extends AppWidgetProvider {
	private static final String TAG="WidgetNavit";
	private static final String WIDGET_APP_ACTION="com.android.launcher.intent.action.NAVIT";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_navit);
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
                context, WidgetNavit.class));
		}
		
		if(appWidgetIds == null){
			appWidgetManager.updateAppWidget(new ComponentName(
					context,WidgetNavit.class),remoteViews);
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
			startNavitActivity(context);		
		}
	}
	
	private static final String PROPERTIESFILE = "/data/system/.properties_file";
	void startNavitActivity(Context context){
		try {
			Intent it = new Intent(Intent.ACTION_VIEW);
			File file = new File(PROPERTIESFILE);
			String packageName = null;
			String className = null;
			if (file.exists()) {
				BufferedReader buf;
				String source = null;

				try {
					buf = new BufferedReader(new FileReader(file));
					do {
						source = buf.readLine();
						if (source != null && source.startsWith("nav_app_class_name=")) {
							className = source.substring(source.indexOf("=") + 1);
						}
						if (source != null && source.startsWith("nav_app_package_name=")) {
							packageName = source.substring(source.indexOf("=") + 1);
						}
						if (source != null)
							Log.e(TAG, source);
					} while (source != null);
					buf.close();
					if (packageName != null && className != null) {
						ComponentName component = new ComponentName(packageName, className);
						it.setComponent(component);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (packageName == null || className == null) {
				Log.e(TAG, "packageName or className is null ,launche default");
				it.setClassName("cld.navi.c2025.mainframe", "cld.navi.c2025.mainframe.NaviMainActivity");
			}
			it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(it);
		} catch (Exception e) {
			Toast.makeText(context, R.string.no_navi_app, Toast.LENGTH_LONG).show();
			Log.e(TAG, e.getMessage());
		}
	}
}