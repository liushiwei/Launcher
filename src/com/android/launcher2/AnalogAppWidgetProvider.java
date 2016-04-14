package com.android.launcher2;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.widget.RemoteViews;

import com.android.launcher.R;

public class AnalogAppWidgetProvider extends AppWidgetProvider {
//	private RemoteViews remoteViews;
//	private ComponentName thisWidget; 
	
    @Override  
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {  
//    	  remoteViews = new RemoteViews(context.getPackageName(), R.layout.analog_appwidget);
    	  Timer timer = new Timer();      
//    	  thisWidget = new ComponentName(context,AnalogAppWidgetProvider.class);  
    	  timer.scheduleAtFixedRate(new MyTime(context, appWidgetManager), 10, 3*60*60); 
          super.onUpdate(context, appWidgetManager, appWidgetIds);  
    }
    
    private class MyTime extends TimerTask{      
        RemoteViews remoteViews;      
        AppWidgetManager appWidgetManager;  
        Context context;
        ComponentName thisWidget; 
              
        public MyTime(Context context, AppWidgetManager appWidgetManager){      
        	this.context = context;
            this.appWidgetManager = appWidgetManager;  
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.analog_appwidget);
            thisWidget = new ComponentName(context, AnalogAppWidgetProvider.class);  
        }    
        
        public void run() {      
            Date date = new Date();      
            remoteViews.setTextViewText(R.id.dateTv, new SimpleDateFormat("yyyy-MM-dd").format(date));
            remoteViews.setTextViewText(R.id.weekTv, DateToWeek(date, context));    
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);      
        }      
    }      
    
    
    /** 
	 * @param date 
	 * @return 
	 */  
	public String DateToWeek(Date date, Context context) {  
		Resources res = context.getResources();
		String[] weekDay = res.getStringArray(R.array.week);
	    Calendar calendar = Calendar.getInstance();  
	    calendar.setTime(date);  
	    int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);  
	    if (dayIndex < 1 || dayIndex > weekDay.length)   return "";  
	    return weekDay[dayIndex - 1];  
	}
    
//        CharSequence dateFormat = Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT);
//        if(dateFormat == null || dateFormat.equals("")) {			
//        	dateFormat = DateFormat.getBestDateTimePattern(Locale.getDefault(),context.getString(R.string.abbrev_wday_month_day_no_year));
//         }
//        Log.d(TAG, "dateFormat------->"+dateFormat);
//        views.setCharSequence(R.id.dateTv, "setFormat12Hour", dateFormat);
//        appWidgetManager.updateAppWidget(appWidgetIds, views);
}

