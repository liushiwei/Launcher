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
import android.os.FileUtils;
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

import com.android.launcher.R;

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

        resetLayout();
    }

    void resetLayout() {
        mContent.removeAllViewsInLayout();

        // Add the Apps button
        
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        BubbleTextView allAppsButton = (BubbleTextView)
                inflater.inflate(R.layout.application, mContent, false);
        allAppsButton.setCompoundDrawablesWithIntrinsicBounds(null,
                context.getResources().getDrawable(R.drawable.all_apps_button_icon), null, null);
        allAppsButton.setContentDescription(context.getString(R.string.all_apps_button_label));
        allAppsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mLauncher != null &&
                    (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    mLauncher.onTouchDownAllAppsButton(v);
                }
                return false;
            }
        });

        allAppsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (mLauncher != null) {
                    mLauncher.onClickAllAppsButton(v);
                }
            }
        });

        // Note: We do this to ensure that the hotseat is always laid out in the orientation of
        // the hotseat in order regardless of which orientation they were added
//      int x = getCellXFromOrder(mAllAppsButtonRank);
//      int y = getCellYFromOrder(mAllAppsButtonRank);
//      CellLayout.LayoutParams lp = new CellLayout.LayoutParams(x,y,1,1);
        CellLayout.LayoutParams lp = new CellLayout.LayoutParams(2,0,1,1);

        lp.canReorder = false;
        mContent.addViewToCellLayout(allAppsButton, -1, 0, lp, true);
        
        initButton(0,R.drawable.navi_button_icon,new View.OnClickListener() {
		      @Override
		      public void onClick(android.view.View v) {
		    	  Intent intent = new Intent("com.carit.key.navi");
		    	  getContext().sendBroadcast(intent);    	  
		      }
		});
        initButton(1,R.drawable.radio_button_icon,new View.OnClickListener() {
		      @Override
		      public void onClick(android.view.View v) {
		    	  doRunActivity(getContext().getString(R.string.app_radio));	
		      }
		});
        
//        initButton(3,R.drawable.bt_button_icon,new View.OnClickListener() {
//		      @Override
//		      public void onClick(android.view.View v) {
//		    	  doRunActivity(getContext().getString(R.string.app_bt));	
//		      }
//		});
        getNavConfig();
        if(mAppName.length()>0){
        	TextView app2Name = (TextView) mLauncher.findViewById(R.id.app3_name);
        	app2Name.setText(mAppName);
        }
        mPackageManager = getContext().getPackageManager();
        if(mPackageName.length()>0){
        	try {
				app3Drawable = mPackageManager.getActivityIcon(new ComponentName(mPackageName, mClassName));
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	mApp3Intent = new Intent();
        	mApp3Intent.setClassName(mPackageName, mClassName);
        	mApp3Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	
        }else{
        	app3Drawable = context.getResources().getDrawable(R.drawable.bt_button_icon);
        	mApp3Intent = new Intent(getContext().getString(R.string.app_bt));
        	mApp3Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        initButton(3,app3Drawable,new View.OnClickListener() {
		      @Override
		      public void onClick(android.view.View v) {
		    	  getContext().startActivity(mApp3Intent);
		      }
		},new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
						
						mViews = new ArrayList<View>();
						final Intent mainIntent = new Intent(
								Intent.ACTION_MAIN, null);
						mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
						final List<ResolveInfo> apps = mPackageManager
								.queryIntentActivities(mainIntent, 0);
						mApps = new ArrayList<ResolveInfo>();
						for (ResolveInfo appInfo : apps) {
								mApps.add(appInfo);
						}
						LayoutInflater inflater = LayoutInflater
								.from(getContext());
						for (ResolveInfo app : mApps) {
							View row = inflater.inflate(R.layout.app_list_view,
									null, false);
							ViewWrapper wrapper = new ViewWrapper(row);
							wrapper.getIcon().setImageDrawable(
									app.loadIcon(mPackageManager));
							String pkgName = app.activityInfo.applicationInfo.packageName;
							// Log.e(TAG,
							// "position = "+position+"pkgName = "+mApps.get(position).activityInfo.applicationInfo.packageName+
							// "mPackageName ="+mPackageName);
							wrapper.setPackageName(pkgName);
							wrapper.setClassName(app.activityInfo.name);
							wrapper.setAppName(app.loadLabel(mPackageManager).toString());
							wrapper.getLabel().setText(
									app.loadLabel(mPackageManager).toString());
							
							row.setTag(wrapper);
							mViews.add(row);
						}
						mAdapter = new IconicAdapter(R.layout.app_list_view,mViews);
						new AlertDialog.Builder(getContext())
								.setIcon(android.R.drawable.ic_dialog_info)
								.setSingleChoiceItems(mAdapter, 1,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												ViewWrapper tag = (ViewWrapper) mViews.get(which).getTag();
												String temp = "";
												try {
													File file = new File(PROPERTIESFILE);
													if (!file.exists()) {
														file.createNewFile();
														FileUtils.setPermissions(PROPERTIESFILE, FileUtils.S_IRWXU | FileUtils.S_IRWXG | FileUtils.S_IRWXO, -1, -1);
													}
													FileInputStream fis = new FileInputStream(file);
													InputStreamReader isr = new InputStreamReader(fis);
													BufferedReader br = new BufferedReader(isr);
													StringBuffer buf = new StringBuffer();

													do {
														temp = br.readLine();
														if (temp!=null&&temp.indexOf("launcher_app3_class_name") < 0&&temp.indexOf("launcher_app3_package_name") < 0&&temp.indexOf("launcher_app3_app_name") < 0) {
															buf = buf.append(temp);
															buf = buf.append("\n");
														}
													} while (temp != null);

													br.close();
													buf.append("launcher_app3_class_name=" + tag.getClassName() + "\n");
													buf.append("launcher_app3_package_name=" + tag.getPackageName() + "\n");
													buf.append("launcher_app3_app_name=" + tag.getAppName() + "\n");
													temp = buf.toString();
													Log.e(TAG,temp);
													FileOutputStream fos = new FileOutputStream(file);
													PrintWriter pw = new PrintWriter(fos);
													pw.write(temp);
													pw.flush();
													pw.close();
												} catch (IOException e) {
													e.printStackTrace();
												}
												resetLayout();
												dialog.dismiss();
											}
										})
								.setNegativeButton(
										getContext()
												.getString(android.R.string.cancel), null).show();
				return false;
			}
		});
         
        if(isInstallApp("globalmain.apk")){
        	 initButton(4,R.drawable.parking_button_icon,new View.OnClickListener() {
	   		      @Override
	   		      public void onClick(android.view.View v) {	
	   		    	Intent intent = new Intent(Intent.ACTION_MAIN);  
	   		    	intent.addCategory(Intent.CATEGORY_LAUNCHER);              
	   		    	ComponentName cn = new ComponentName("tianshuang.globalmain", "tianshuang.globalmain.MainActivity");              
	   		    	intent.setComponent(cn);  
	   		    	getContext().startActivity(intent);  	
	   		      }
        	 });
		}else{       
	        initButton(4,R.drawable.music_button_icon,new View.OnClickListener() {
			      @Override
			      public void onClick(android.view.View v) {
			    	  doRunActivity(getContext().getString(R.string.app_music));	
			      }
			});
		}
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