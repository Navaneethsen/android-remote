package com.axcoto.shinjuku.sushi;

/**
 * Common Utilities (For GCM)
 */

import android.content.Context;
import android.content.Intent;
 
public final class CommonUtilities {
	public final static String REGID_FILENAME = "regid";
	public final static String DEVICE_FILENAME = "device";
	final static int VIRGIN = 18;	
	final static String VERSION = "0.5.3-dev-121213-b1"; //major.minor.patcher-[dev|prod]-date-b{build number in day}
	final static String RELEASE_VERSION = "1.0"; //major.minor
		
    // give your server registration url here
    static final String SERVER_URL = "http://sinatra-gcm.herokuapp.com/";
 
    // Google project id
    static final String SENDER_ID = "217582217558"; 
 
    /**
     * Tag used on log messages.
     */
    static final String TAG = "iCeeNee";
 
    static final String DISPLAY_MESSAGE_ACTION =
            "com.axcoto.shinjuku.sushi.DISPLAY_MESSAGE";
 
    static final String EXTRA_MESSAGE = "message";
 
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);        
        context.sendBroadcast(intent);
    }
}