package com.android.launcher2;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;


public class AnalogAppWidgetProvider extends AppWidgetProvider {
	
    @Override  
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {  
    	  super.onUpdate(context, appWidgetManager, appWidgetIds);  
    }
    
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}
	
}