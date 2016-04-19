package com.android.launcher2;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;

/*** Represents an app in AllAppsView.  */
class ApplicationInfo extends ItemInfo {
    private static final String TAG = "Launcher2.ApplicationInfo";

    /*** The intent used to start the application.  */
    Intent intent;

    /***  A bitmap version of the application icon. */
    Bitmap iconBitmap;

    /*** The time at which the app was first installed. */
    long firstInstallTime;

    ComponentName componentName;

    static final int DOWNLOADED_FLAG = 1;
    static final int UPDATED_SYSTEM_APP_FLAG = 2;

    int flags = 0;

    ApplicationInfo() {
        itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT;
    }

    /** Must not hold the Context. */
    public ApplicationInfo(PackageManager pm, ResolveInfo info, IconCache iconCache, HashMap<Object, CharSequence> labelCache) {
        final String packageName = info.activityInfo.applicationInfo.packageName;
        this.componentName = new ComponentName(packageName, info.activityInfo.name);
        this.container = ItemInfo.NO_ID;
        this.setActivity(componentName, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        try {
            int appFlags = pm.getApplicationInfo(packageName, 0).flags;
            if ((appFlags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) {
                flags |= DOWNLOADED_FLAG;
                if ((appFlags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    flags |= UPDATED_SYSTEM_APP_FLAG;
                }
            }
            firstInstallTime = pm.getPackageInfo(packageName, 0).firstInstallTime;
        } catch (NameNotFoundException e) {
            Log.d(TAG, "PackageManager.getApplicationInfo failed for " + packageName);
        }
        iconCache.getTitleAndIcon(this, info, labelCache);
    }
    
    public ApplicationInfo(ApplicationInfo info) {
        super(info);
        componentName = info.componentName;
        title = info.title.toString();
        intent = new Intent(info.intent);
        flags = info.flags;
        firstInstallTime = info.firstInstallTime;
    }
    
    // add by zgy
	public int getSortId() {
		String s = componentName.getClassName()/* title.toString() */;
		if (s.equals("com.autonavi.auto.remote.fill.UsbFillActivity")) {
			return 1;
		}else if(s.equals("com.carit.radioplayer.RedAndBlackActivity")){
			return 2;
		}else if(s.equals("com.carit.bluetooth.BluetoothApplication")){
			return 3;
		} else if(s.equals("com.android.music.MusicBrowserActivity")){
			return 4;
			
		}else if(s.equals("com.carit.filemanager.FileManagerActivity")){
			return 5;
		}else if(s.equals("com.android.settings.CaritSettings")){
			 return 6;
		}else if(s.equals("com.baidu.carlifevehicle.CarlifeActivity")){
			return 7;
		}else if(s.equals("net.easyconn.WelcomeActivity")){
			return 8;
		}else if(s.equals("com.carit.auxplayer.FrontAUXPlayer")){ // HDMI
			return 9;
		}else if(s.equals("com.carit.auxplayer.DVRPlayer")){ //行车记录
			return 10;
		}else if(s.equals("")){ //胎压测试
			return 11;
		}else if(s.equals("")){ // 4G
			return 12;
		}else if(s.equals("com.carit.bluetooth.BTMusic")){ // 蓝牙音乐
			return 13;
		}else if(s.equals("com.android.gallery3d.app.Video")){ // 视频
			return 14;
		}else if(s.equals("com.android.gallery3d.app.GalleryActivity")){ // 图库
			return 15;
		}else if(s.equals("com.carit.auxplayer.AUXPlayer")){ // AUX
			return 16;
		}else if(s.equals("com.carit.auxplayer.AVPlayer")){ // AV
			return 17;
		}else if(s.equals("com.android.music.MusicBrowserActivity")){ //浏览器
			return 18;
		}
		return 100;
	}
    //end

    /** Returns the package name that the shortcut's intent will resolve to, or an empty string if none exists. */
    String getPackageName() {
        return super.getPackageName(intent);
    }

    /**
     * Creates the application intent based on a component name and various launch flags.
     * Sets {@link #itemType} to {@link LauncherSettings.BaseLauncherColumns#ITEM_TYPE_APPLICATION}.
     * @param className the class name of the component representing the intent
     * @param launchFlags the launch flags
     */
    final void setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
        itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION;
    }

    @Override
    public String toString() {
        return "ApplicationInfo(title=" + title.toString() + ")";
    }

    public static void dumpApplicationInfoList(String tag, String label,ArrayList<ApplicationInfo> list) {
        Log.d(tag, label + " size=" + list.size());
        for (ApplicationInfo info: list) {
            Log.d(tag, "   title=\"" + info.title + "\" iconBitmap=" + info.iconBitmap + " firstInstallTime=" + info.firstInstallTime);
        }
    }

    public ShortcutInfo makeShortcut() {
        return new ShortcutInfo(this);
    }
}
