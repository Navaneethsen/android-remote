package com.axcoto.shinjuku.maki;

import java.io.File;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import com.axcoto.shinjuku.maki.ShareKit;

public class ShareEmail implements ShareKit {
	protected Activity parentActivity;
	
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
	
	/**
	 * This method tries to send email with an built-in email app.
	 * If it cannot find any, it uses CeeNee smtp to send email
	 * @return
	 */
	public AlertDialog insideMailer() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this.parentActivity);
//        builder.setTitle("Sharing song book by email");
//        builder.setMessage("Please input email address:");
// 
//         // Use an EditText view to get user input.
//         final EditText input = new EditText(this.parentActivity);
//         input.setId(TEXT_ID);
//         builder.setView(input);
// 
//        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
// 
//            @SuppressLint("NewApi")
//			@Override
//            public void onClick(DialogInterface dialog, int whichButton) {
//                aEmailList = input.getText().toString();
//                Log.d(TAG, "email address: " + aEmailList);
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("message/rfc822");
////                i.setType("application/pdf");
//                i.putExtra(Intent.EXTRA_EMAIL, new String[]{aEmailList});
//                i.putExtra(Intent.EXTRA_SUBJECT, "Android remote share song book");
//                i.putExtra(Intent.EXTRA_TEXT   , "Song book file is in attachment");
//                
//                String fname = "";
//                String filenamepdf = "";
//                if (karaoke.equals("hd"))
//                {
//                	fname = "KaraokeDB.xml";
//                }
//                else if (karaoke.equals("mp3"))
//                {
//                	fname = "MP3KaraokeDB.xml";
//        		}
//                String[] part = fname.split("\\.");
//                String s = part[0];
//                filenamepdf = s + ".pdf";
//                
//                Uri uri;
//                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/iCeeNee" + "/" + filenamepdf);
//                Log.i(TAG, "file path pdf: " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/iCeeNee" + "/" + filenamepdf);
//                uri = Uri.fromFile(f);
//                i.putExtra(Intent.EXTRA_STREAM, uri);
//                
//                try {
//                    startActivityForResult(Intent.createChooser(i, "Send mail..."),REQUEST_SEND_EMAIL);
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(t, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
// 
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
// 
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                return;
//            }
//        });
// 
//        return builder.create();
		return null;
	}

	
}
