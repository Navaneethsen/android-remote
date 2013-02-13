package com.axcoto.shinjuku.maki;

import android.util.Log;
import com.axcoto.shinjuku.sushi.MainActivity;

/**
 * Custom Log for iCeeNee 
 * Disable Log functions in PHASE_PRODUCTION
 */
public class MyLog {

	/**
	 * Calling Log.e
	 * @param tag tag String
	 * @param content content String
	 */
	public static void e(String tag, String content) {
		if (MainActivity.ENVIRONMENT != MainActivity.PHASE_PRODUCTION) {
			Log.e(tag, content);
		}
	}
	
	/**
	 * Calling Log.i
	 * @param tag tag String
	 * @param content content String
	 */
	public static void i(String tag, String content) {
		if (MainActivity.ENVIRONMENT != MainActivity.PHASE_PRODUCTION) {
			Log.i(tag, content);
		}
	}
	
	/**
	 * Calling Log.w
	 * @param tag tag String
	 * @param content content String
	 */
	public static void w(String tag, String content) {
		if (MainActivity.ENVIRONMENT != MainActivity.PHASE_PRODUCTION) {
			Log.w(tag, content);
		}	
	}

	/**
	 * Calling Log.d
	 * @param tag tag String
	 * @param content content String
	 */
	public static void d(String tag, String content) {
		if (MainActivity.ENVIRONMENT != MainActivity.PHASE_PRODUCTION) {
			Log.d(tag, content);
		}		
	}
	
	/**
	 * Calling Log.v
	 * @param tag tag String
	 * @param content content String
	 */
	public static void v(String tag, String content) {
		if (MainActivity.ENVIRONMENT != MainActivity.PHASE_PRODUCTION) {
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
		if (MainActivity.ENVIRONMENT != MainActivity.PHASE_PRODUCTION) {
			Log.e(tag, content,e);
		}		
	}
}
