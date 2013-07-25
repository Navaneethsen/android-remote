package com.axcoto.shinjuku.maki;

import android.app.Activity;
import com.axcoto.shinjuku.maki.ShareEmail;
import com.axcoto.shinjuku.maki.ShareKit;

public class ShareKitFactory {
	public static ShareKit getInstance(Activity parent, String _type) throws Exception{
		ShareKit transporter;
		
		if (_type.equalsIgnoreCase("email")) {
			transporter = new ShareEmail();
//			transporter.set
		} else {
			throw new Exception("We have not suppoted this transport method");
		}
		
		if (!(transporter instanceof ShareKit)) {
			throw new Exception("The transported object does not has transporting capibilty. It should be an instance of SBTransporter");
		}
		return transporter;
	}
	
}