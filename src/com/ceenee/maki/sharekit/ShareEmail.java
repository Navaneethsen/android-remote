package com.ceenee.maki.sharekit;

import java.io.File;

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
//			Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
			Intent intent = new Intent(android.content.Intent.ACTION_SEND);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setType("application/pdf");
            
//            String uriText = "mailto:" + Uri.encode("") 
//            					+  "?subject=" + Uri.encode("CeeNee Song Book Sharing") 
//            					+  "&body=" + Uri.encode("This is the songbook. You may need an PDF reader to read this file.")  
//                    ;
//			Uri uri = Uri.parse(uriText);			
//			intent.setData(uri);
			
			intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"email@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing CeeNee Karaoke Song Book");
            intent.putExtra(Intent.EXTRA_TEXT, "Check out the PDf for the song book.\nSent from QCeeNee on Android.\nYou may need a PDF reader to view this song book");
            
            File f = new File( parentActivity.getFilesDir() + "/export_ceenee_songbook.pdf");
            MyLog.i("PDF_ATTACH", "File path is " + f.getAbsolutePath());
            Uri uri = Uri.fromFile(f);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            this.parentActivity.startActivity(Intent.createChooser(intent, "Share song book..."));                

        } catch (Exception e) {   
            MyLog.e("SONGBOOK_EMAIL", e.getMessage(), e);   
        } 
		
		return false;
	}
	
}
