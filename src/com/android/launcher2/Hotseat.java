/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.george.launcher.R;

public class Hotseat extends FrameLayout {
    @SuppressWarnings("unused")
    private static final String TAG = "Hotseat";

    private Launcher mLauncher;
    private CellLayout mContent;

    private int mCellCountX;
    private int mCellCountY;
    private int mAllAppsButtonRank;

    private boolean mTransposeLayoutWithOrientation;
    private boolean mIsLandscape;
    
    public Hotseat(Context context) {
        this(context, null);
    }

    public Hotseat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Hotseat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Hotseat, defStyle, 0);
        Resources r = context.getResources();
        mCellCountX = a.getInt(R.styleable.Hotseat_cellCountX, -1);
        mCellCountY = a.getInt(R.styleable.Hotseat_cellCountY, -1);
        mAllAppsButtonRank = r.getInteger(R.integer.hotseat_all_apps_index);
        mTransposeLayoutWithOrientation = 
                r.getBoolean(R.bool.hotseat_transpose_layout_with_orientation);
        mIsLandscape = context.getResources().getConfiguration().orientation ==
            Configuration.ORIENTATION_LANDSCAPE;
        mIsLandscape = false;
    }

    public void setup(Launcher launcher) {
        mLauncher = launcher;
        setOnKeyListener(new HotseatIconKeyEventListener());
        resetLayout();
    }

    CellLayout getLayout() {
        return mContent;
    }
  
    private boolean hasVerticalHotseat() {
        return (mIsLandscape && mTransposeLayoutWithOrientation);
    }

    /* Get the orientation invariant order of the item in the hotseat for persistence. */
    int getOrderInHotseat(int x, int y) {
        return hasVerticalHotseat() ? (mContent.getCountY() - y - 1) : x;
    }
    /* Get the orientation specific coordinates given an invariant order in the hotseat. */
    int getCellXFromOrder(int rank) {
        return hasVerticalHotseat() ? 0 : rank;
    }
    int getCellYFromOrder(int rank) {
        return hasVerticalHotseat() ? (mContent.getCountY() - (rank + 1)) : 0;
    }
    public boolean isAllAppsButtonRank(int rank) {
        return rank == mAllAppsButtonRank;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mCellCountX < 0) mCellCountX = LauncherModel.getCellCountX();
        if (mCellCountY < 0) mCellCountY = LauncherModel.getCellCountY();
        mContent = (CellLayout) findViewById(R.id.layout);
        mContent.setGridSize(mCellCountX, mCellCountY);
        mContent.setIsHotseat(true);

//        resetLayout();
    }

    void resetLayout() {
    	
    }
    
	public static boolean isInstallApp(String appName) {		
		File file = new File("/system/app/"+appName);
		if (!file.exists()) {				
			return false;
		}		
		return true;
	}
    
	private void doRunActivity(String name){
		try {
    		Intent it = new Intent(name);
    		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		getContext().startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    void initButton(int cellX,int icon,OnClickListener l){
    	Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        BubbleTextView button = (BubbleTextView)
        		inflater.inflate(R.layout.application, mContent, false);
        button.setCompoundDrawablesWithIntrinsicBounds(null,
        		context.getResources().getDrawable(icon), null, null);
        button.setOnClickListener(l);
		CellLayout.LayoutParams lp = new CellLayout.LayoutParams(cellX,0,1,1);
		lp.canReorder = false;
		mContent.addViewToCellLayout(button, -1, 0, lp, true);
    }
    
    void initButton(int cellX,Drawable icon,OnClickListener l,OnLongClickListener longClick){
    	Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        BubbleTextView button = (BubbleTextView)
        		inflater.inflate(R.layout.application, mContent, false);
        button.setCompoundDrawablesWithIntrinsicBounds(null,
        		icon, null, null);
        button.setOnClickListener(l);
        button.setOnLongClickListener(longClick);
		CellLayout.LayoutParams lp = new CellLayout.LayoutParams(cellX,0,1,1);
		lp.canReorder = false;
		mContent.addViewToCellLayout(button, -1, 0, lp, true);
    }
    
    private static final String PROPERTIESFILE = "/sdcard/.properties_file";
    private List<ResolveInfo> mApps;
	private List<View> mViews;
	private PackageManager mPackageManager;
	private String mPackageName = "";
	private String mClassName = "";
	private String mAppName = "";
	private IconicAdapter mAdapter;
	private Drawable app3Drawable = null;
	private Intent mApp3Intent;
    class IconicAdapter extends ArrayAdapter<View> {
		private List<View> listItems;
		private int layout;

		IconicAdapter(int layout, List<View> listItems_) {
			super(Hotseat.this.getContext(), layout, listItems_);
			this.listItems = (List<View>) listItems_;
			this.layout = layout;
		}
		
		

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = listItems.get(position);
			ViewWrapper wrapper = (ViewWrapper)row.getTag();
			wrapper.getIcon().setImageDrawable(mApps.get(position).loadIcon(mPackageManager));
			String pkgName =mApps.get(position).activityInfo.applicationInfo.packageName;
			//Log.e(TAG, "position = "+position+"pkgName = "+mApps.get(position).activityInfo.applicationInfo.packageName+ "mPackageName ="+mPackageName);
			if(pkgName.equals(mPackageName)){
				wrapper.getLabel().setChecked(true);
			}else{
				wrapper.getLabel().setChecked(false);
			}
			return (row);
		}
	}

	class ViewWrapper {
		View base;
		CheckedTextView label = null;
		ImageView icon = null;
		String packageName = null;
		String className = null;
		String appName = null;
		int id = -1;

		ViewWrapper(View base) {
			this.base = base;
			id = -1;
		}

		CheckedTextView getLabel() {
			if (label == null) {
				label = (CheckedTextView) base.findViewById(android.R.id.text1);
			}

			return (label);
		}

		ImageView getIcon() {
			if (icon == null) {
				icon = (ImageView) base.findViewById(R.id.icon);
			}

			return (icon);
		}

		int getId() {
			return id;
		}

		void setId(int deviceId) {
			id = deviceId;
		}

		public String getPackageName() {
			return packageName;
		}

		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}
		
		
		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}
		
		
		

	}
	
	private void getNavConfig(){
		File file = new File(PROPERTIESFILE);
		Log.e(TAG, "getNavConfig()");
		if (file.exists()) {
			Log.e(TAG, "file.exists()");
			BufferedReader buf;
			String source = null;

			try {
				buf = new BufferedReader(new FileReader(file));
				do {
					source = buf.readLine();
					if (source != null && source.startsWith("launcher_app3_class_name=")) {
						mClassName = source.substring(source.indexOf("=") + 1);
					}
					if (source != null && source.startsWith("launcher_app3_package_name=")) {
						mPackageName = source.substring(source.indexOf("=") + 1);
					}
					if (source != null && source.startsWith("launcher_app3_app_name=")) {
						mAppName = source.substring(source.indexOf("=") + 1);
					}
					if (source != null)
						Log.e(TAG, source);
				} while (source != null);
				buf.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}