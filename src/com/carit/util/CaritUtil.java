package com.carit.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.util.Log;

/**	version: 0.0.1
 * 	developer:justin
 * 	date:2014.04.23
 * */

public class CaritUtil {
	private static final String TAG ="CaritUtil";
	private static final boolean DEBUG =true;
	
	public static final String APP_FILE = "/system/etc/carit_version";
	public static final String APP_FILE2 = "/system/etc/carit_version2";
	
	public static String getCaritVersion(){
		return readVersionFile(APP_FILE);
	}
	
	public static String getCaritVersion2(){
		return readVersionFile(APP_FILE2);
	}
	
	public static String readVersionFile(String fileName){
		File model = new File(fileName);
		String text = "";
		if (model.exists()) {
			FileInputStream fileInput;
			try {
				fileInput = new FileInputStream(model);
				DataInputStream bInput = new DataInputStream(fileInput);
				text = bInput.readLine();
				if(DEBUG)Log.d(TAG,"buff = "+text);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			if(DEBUG)Log.e(TAG,fileName +" not exist!!!");
		}
		if(text == null){
			text = "";
		}
		return text;
	}
}
