package com.android.launcher2;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.android.launcher.R;

public class NaviWidgetProvider extends AppWidgetProvider {
//		private final String TAG = "MyAppWidgetProvider";  
	    private static RemoteViews mRemoteViews;  
		private final String PROPERTIESFILE = "/data/system/.properties_file";
		private final String WIDGET_APP_ACTION = "com.android.launcher.intent.action.NAVIT";
	  
	    @Override  
	    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {  
	        super.onUpdate(context, appWidgetManager, appWidgetIds);  
	        mRemoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_navit);
	        Intent intent = new Intent();
			intent.setAction(WIDGET_APP_ACTION);
			mRemoteViews.setOnClickPendingIntent(R.id.widget_img, PendingIntent.getBroadcast(context, 0, intent, 0));
			appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);
	        
	    }  
	  
	    @Override
		public void onReceive(Context context, Intent intent) {
			super.onReceive(context, intent);
//			Log.d(TAG,"onReceive getAction----------------------------->"+intent.getAction());
			if(intent.getAction().equals(WIDGET_APP_ACTION)){
				startNavitActivityNavi(context);		
			}
		}
	    
	    /**
	     * add by zgy
	     */
		private void startNavitActivityNavi(Context context){
			try {
				Intent intentClick = new Intent();
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
							if (source != null)  {
								/*Log.e(TAG, source);*/
							}	
						} while (source != null);
						buf.close();
						
						if (packageName != null && className != null) {
							ComponentName component = new ComponentName(packageName, className);
							intentClick.setComponent(component);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (packageName == null || className == null) {
//					Log.e(TAG, "packageName or className is null ,launche default");
					intentClick.setClassName("com.autonavi.amapauto", "com.autonavi.auto.remote.fill.UsbFillActivity");
//					intentClick.setClassName("cld.navi.c2025.mainframe", "cld.navi.c2025.mainframe.NaviMainActivity");
				}
				intentClick.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intentClick);
			} catch (Exception e) {
				Toast.makeText(context, R.string.no_navi_app, Toast.LENGTH_LONG).show();
			}
		}
}
