package com.android.launcher2;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.HashMap;

import com.george.launcher.R;

public class IconCache {
    @SuppressWarnings("unused")
    private static final String TAG = "Launcher.IconCache";
    private static final int INITIAL_ICON_CACHE_CAPACITY = 50;

    private static class CacheEntry {
        public Bitmap icon;
        public String title;
    }

    private final Bitmap mDefaultIcon;
    private final LauncherApplication mContext;
    private final PackageManager mPackageManager;
    private final HashMap<ComponentName, CacheEntry> mCache = new HashMap<ComponentName, CacheEntry>(INITIAL_ICON_CACHE_CAPACITY);
    private int mIconDpi;

    public IconCache(LauncherApplication context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mContext = context;
        mPackageManager = context.getPackageManager();
        mIconDpi = activityManager.getLauncherLargeIconDensity();
        // need to set mIconDpi before getting default icon
        mDefaultIcon = makeDefaultIcon();
    }

    public Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(), android.R.mipmap.sym_def_app_icon);
    }

    public Drawable getFullResIcon(Resources resources, int iconId) {
        Drawable d;
        try {
            d = resources.getDrawableForDensity(iconId, mIconDpi);
        } catch (Resources.NotFoundException e) {
            d = null;
        }
        return (d != null) ? d : getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(String packageName, int iconId) {
        Resources resources;
        try {
            resources = mPackageManager.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(ResolveInfo info) {
        return getFullResIcon(info.activityInfo);
    }

    public Drawable getFullResIcon(ActivityInfo info) {
        Resources resources;
        try {
            resources = mPackageManager.getResourcesForApplication(info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            int iconId = info.getIconResource();
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    private Bitmap makeDefaultIcon() {
        Drawable d = getFullResDefaultActivityIcon();
        Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1), Math.max(d.getIntrinsicHeight(), 1), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        d.setBounds(0, 0, b.getWidth(), b.getHeight());
        d.draw(c);
        c.setBitmap(null);
        return b;
    }

    /*** Remove any records for the supplied ComponentName. */
    public void remove(ComponentName componentName) {
        synchronized (mCache) {
            mCache.remove(componentName);
        }
    }

    /***  Empty out the cache.   */
    public void flush() {
        synchronized (mCache) {
            mCache.clear();
        }
    }

    /** Fill in "application" with the icon and label for "info." */
    public void getTitleAndIcon(ApplicationInfo application, ResolveInfo info, HashMap<Object, CharSequence> labelCache) {
        synchronized (mCache) {
            CacheEntry entry = cacheLocked(application.componentName, info, labelCache);
            application.title = entry.title;
            application.iconBitmap = entry.icon;
        }
    }

    public Bitmap getIcon(Intent intent) {
        synchronized (mCache) {
            final ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
            ComponentName component = intent.getComponent();

            if (resolveInfo == null || component == null) {
                return mDefaultIcon;
            }
            CacheEntry entry = cacheLocked(component, resolveInfo, null);
            return entry.icon;
        }
    }

    public Bitmap getIcon(ComponentName component, ResolveInfo resolveInfo, HashMap<Object, CharSequence> labelCache) {
        synchronized (mCache) {
            if (resolveInfo == null || component == null) {
                return null;
            }
            CacheEntry entry = cacheLocked(component, resolveInfo, labelCache);
            return entry.icon;
        }
    }

    public boolean isDefaultIcon(Bitmap icon) {
        return mDefaultIcon == icon;
    }

    private CacheEntry cacheLocked(ComponentName componentName, ResolveInfo info, HashMap<Object, CharSequence> labelCache) {
        CacheEntry entry = mCache.get(componentName);
        if (entry == null) {
            entry = new CacheEntry();

            mCache.put(componentName, entry);

            ComponentName key = LauncherModel.getComponentNameFromResolveInfo(info);
            if (labelCache != null && labelCache.containsKey(key)) {
                entry.title = labelCache.get(key).toString();
            } else {
                entry.title = info.loadLabel(mPackageManager).toString();
                if (labelCache != null) {
                    labelCache.put(key, entry.title);
                }
            }
            
            // M by zgy / change title
            if (entry.title == null) {
                entry.title = info.activityInfo.name;
                Log.e("Hegel---IC", "is null *"+entry.title+"*");
//                Log.d("zgy", "entry.title -------> " + info.activityInfo.name);
                
            } else if(entry.title.trim().equals("Baidu CarLife")){
            	entry.title = "Baidu CarLife";
            	
            }else if(entry.title.trim().equals("百度 CarLife")){
            	entry.title = "百度 CarLife";
            	
            }else if(entry.title.trim().equals("EasyConnected")){  
            	entry.title = "E-Link";
            	
            } else if(entry.title.trim().equals("亿连手机互联")){
            	entry.title = "手机互联";
            	
            }else if(entry.title.equals(" Amap ")){
            	entry.title = "Navigation";
            }
            else{
            	
            }
            
            
            entry.icon = Utilities.createIconBitmap(getFullResIcon(info), mContext);
        }
        return entry;
    }

    public HashMap<ComponentName,Bitmap> getAllIcons() {
        synchronized (mCache) {
            HashMap<ComponentName,Bitmap> set = new HashMap<ComponentName,Bitmap>();
            for (ComponentName cn : mCache.keySet()) {
                final CacheEntry e = mCache.get(cn);
                set.put(cn, e.icon);
            }
            return set;
        }
    }
    
    // add by zgy
    private Drawable resId2BitMap(int resId){
    	Resources res = mContext.getResources();  
    	return getFullResIcon(res, resId);
    }
    
   private  Bitmap drawable2Bitmap(int resId) {  
	   	   Drawable drawable =  resId2BitMap(resId);
	       int width = (int) (drawable.getIntrinsicWidth() / 1.2);    
	       int height = (int) (drawable.getIntrinsicHeight() / 1.2);    
	       Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);    
	       Canvas canvas = new Canvas(bitmap);    
	       drawable.setBounds(0, 0, width, height);    
	       drawable.draw(canvas);    
	       return bitmap;
//        if (drawable instanceof BitmapDrawable) {  
//            return ((BitmapDrawable) resId2BitMap(resId)).getBitmap();  
//            
//        } else if (resId2BitMap(resId) instanceof NinePatchDrawable) {  
//            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()/2, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);  
//            Canvas canvas = new Canvas(bitmap);  
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());  
//            drawable.draw(canvas);  
//            return bitmap;  
//        } else {  
//            return null;  
//        }  
    } 
   
   //
   private Bitmap drawableId2BitMap(int resId) {  
	   Resources res = mContext.getResources();  
	   return BitmapFactory.decodeResource(res, resId);  
   }
}
