package com.ceenee.maki;

import android.util.Log;

import com.ceenee.q.MainActivity;

/**
 * Custom Log for iCeeNee 
 * Disable Log functions in PHASE_PRODUCTION
 */
public class MyLog {

	/**
	 * In Production mode, we cannot output the log due to Google Play Store term.
	 * This function check whether the app is running in production mode.
	 *  
	 * @return true if we should output the log
	 */
	public static Boolean isLogVisible() {
		return AppInfo.ENVIRONMENT != AppInfo.PHASE_PRODUCTION;
	}
	
	/**
	 * Calling Log.e
	 * @param tag tag String
	 * @param content content String
	 */
	public static void e(String tag, String content) {
		if (isLogVisible()) {
			Log.e(tag, content);
		}
	}
	
	/**
	 * Calling Log.i
	 * @param tag tag String
	 * @param content content String
	 */
	public static void i(String tag, String content) {
		if (isLogVisible()) {
			Log.i(tag, content);
		}
	}
	
	/**
	 * Calling Log.w
	 * @param tag tag String
	 * @param content content String
	 */
	public static void w(String tag, String content) {
		if (isLogVisible()) {
			Log.w(tag, content);
		}	
	}

	/**
	 * Calling Log.d
	 * @param tag tag String
	 * @param content content String
	 */
	public static void d(String tag, String content) {
		if (isLogVisible()) {
			Log.d(tag, content);
		}		
	}
	
	/**
	 * Calling Log.v
	 * @param tag tag String
	 * @param content content String
	 */
	public static void v(String tag, String content) {
		if (isLogVisible()) {
			Log.v(tag, content);
		}		
	}
	
	/**
	 * Calling Log.e
	 * @param tag tag String
	 * @param content content String
	 * @param e exception
	 */
	public static void e(String tag, String content, Exception e) {
		if (isLogVisible()) {
			Log.e(tag, content,e);
		}		
	}
}
