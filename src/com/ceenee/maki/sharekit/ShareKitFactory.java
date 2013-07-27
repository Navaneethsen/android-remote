package com.ceenee.maki.sharekit;

import android.app.Activity;

import com.ceenee.maki.sharekit.ShareEmail;
import com.ceenee.maki.sharekit.ShareKit;

public class ShareKitFactory {
	public static ShareKit getInstance(Activity parent, String _type) throws Exception{
		ShareKit transporter;
		
		if (_type.equalsIgnoreCase("email")) {
			transporter = new ShareEmail(parent);
		} else {
			throw new Exception("We have not suppoted this transport method");
		}
		
		if (!(transporter instanceof ShareKit)) {
			throw new Exception("The transported object does not has transporting capibilty. It should be an instance of SBTransporter");
		}
		return transporter;
	}
	
}