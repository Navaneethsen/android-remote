package com.ceenee.maki.sharekit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import com.ceenee.maki.MyLog;
import com.ceenee.maki.sharekit.ShareKit;

public class ShareEmail implements ShareKit {
	protected Activity parentActivity;
	
	public ShareEmail(Activity a) {
		parentActivity = a;
	}
	
	public void setActivity(Activity a) {
		parentActivity =a;
	}
	
	@Override
	public boolean execute() {
		try {   
			Intent i = new Intent(android.content.Intent.ACTION_SENDTO);
            i.setType("text/plain");
            
            String uriText = "mailto:" + Uri.encode("email@gmail.com") + 
			                    "?subject=" + Uri.encode("the subject") + 
			                    "&body=" + Uri.encode("the body of the message")  
                    ;
            Uri uri = Uri.parse(uriText);

            i.setData(uri);
            this.parentActivity.startActivity(Intent.createChooser(i, "Send mail..."));                

        } catch (Exception e) {   
            MyLog.e("SONGBOOK_EMAIL", e.getMessage(), e);   
        } 
		
		return false;
	}
	
}
