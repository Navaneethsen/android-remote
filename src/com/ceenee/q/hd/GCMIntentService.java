package com.ceenee.q.hd;

/**
 * GCM Intent Service (For GCM)
 */

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
 
import com.google.android.gcm.GCMBaseIntentService;
 
import static com.ceenee.q.hd.CommonUtilities.SENDER_ID;
import static com.ceenee.q.hd.CommonUtilities.displayMessage;

import com.ceenee.maki.IdRegister;
import com.ceenee.maki.MyLog;
import com.ceenee.q.R;

public class GCMIntentService extends GCMBaseIntentService {
 
    private static final String TAG = "GCMIntentService";
 
    public GCMIntentService() {
        super(SENDER_ID);
    }
 
    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        MyLog.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
        MyLog.d("ID", registrationId);
        
        IdRegister r = new IdRegister(registrationId);
        r.setContext(context);
        r.perform();
        
        ServerUtilities.register(context,registrationId);
    }
 
    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        MyLog.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }
 
    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        MyLog.i(TAG, "Received message");
        String message = intent.getExtras().getString("msg");
        String url = intent.getExtras().getString("url");
        if (url==null) {
        	url = "http://ceenee.com";
        }
        if (!url.startsWith("http://") && !url.startsWith("https://"))
        	   url = "http://" + url;
        
        displayMessage(context, message);        
        // notifies user
        generateNotification(context, message,url);
    }
 
    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        MyLog.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message,"");
    }
 
    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        MyLog.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }
 
    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        MyLog.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }
 
    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message, String url) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
 
        String title = context.getString(R.string.app_name);
 
//        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Intent openWebBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent intent =
        		PendingIntent.getActivity(context, 0, openWebBrowser, 0);
//                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
 
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
 
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);      
 
    }
 
}