package org.acra.log;


import android.util.Log;

/**
 * Responsible for delegating calls to the Android logging system.
 * <p/>
 * @author William Ferguson
 * @since 4.3.0
 */
public final class AndroidLogDelegate implements ACRALog {
    public int v(String tag, String msg) {
        return MyLog.v(tag, msg);
    }
    public int v(String tag, String msg, Throwable tr) {
        return MyLog.v(tag, msg, tr);
    }
    public int d(String tag, String msg) {
        return MyLog.d(tag, msg);
    }
    public int d(String tag, String msg, Throwable tr) {
        return MyLog.d(tag, msg, tr);
    }
    public int i(String tag, String msg) {
        return MyLog.i(tag, msg);
    }
    public int i(String tag, String msg, Throwable tr) {
        return MyLog.i(tag, msg, tr);
    }
    public int w(String tag, String msg) {
        return MyLog.w(tag, msg);
    }
    public int w(String tag, String msg, Throwable tr) {
        return MyLog.w(tag, msg, tr);
    }
    //public native  boolean isLoggable(java.lang.String tag, int level);
    public int w(String tag, Throwable tr) {
        return MyLog.w(tag, tr);
    }
    public int e(String tag, String msg) {
        return MyLog.e(tag, msg);
    }
    public int e(String tag, String msg, Throwable tr) {
        return MyLog.e(tag, msg, tr);
    }
    public String getStackTraceString(Throwable tr) {
        return MyLog.getStackTraceString(tr);
    }
    //public native  int println(int priority, java.lang.String tag, java.lang.String msg);
}
