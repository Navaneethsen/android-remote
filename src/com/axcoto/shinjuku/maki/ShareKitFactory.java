package com.axcoto.shinjuku.maki;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.provider.MailSender;
import com.provider.JSSEProvider;
import com.axcoto.shinjuku.maki.ShareEmail;
import com.axcoto.shinjuku.maki.ShareKit;

public class ShareKitFactory {
	public static ShareKit getInstance(Activity parent, String _type) throws Exception{
		ShareKit transporter;
		
		if (_type.equalsIgnoreCase("email")) {
			transporter = new ShareEmail();
		} else {
			throw new Exception("We have not suppoted this transport method");
		}
		
		if (!(transporter instanceof ShareKit)) {
			throw new Exception("The transported object does not has transporting capibilty. It should be an instance of SBTransporter");
		}
		return transporter;
	}
	
}
