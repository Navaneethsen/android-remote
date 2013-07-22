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


abstract public class ShareKit {
	protected String type;
	protected Activity parentActivity;
	
	public ShareKit(String _type) throws Exception {
		type = _type;
	}
	
	public static ShareKit getInstance(String _type) throws Exception{
		ShareKit transporter;
		
		if (_type.equalsIgnoreCase("email")) {
			transporter = ShareKit.getInstance(_type);
		} else {
			throw new Exception("We have not suppoted this transport method");
		}
		
		if (!(transporter instanceof ShareKit)) {
			throw new Exception("The transported object does not has transporting capibilty. It should be an instance of SBTransporter");
		}
		return transporter;
	}
	
	public void setActivity(Activity a) {
		parentActivity =a;
	}
	
	public boolean send() throws Exception{
		return this.execute();
	}
	
	/**
	 * Sharing actually happens here.
	 */
	abstract public boolean execute();
}
