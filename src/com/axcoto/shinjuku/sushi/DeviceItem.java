package com.axcoto.shinjuku.sushi;

import java.io.IOException;

import android.util.Log;

import com.axcoto.shinjuku.maki.Remote;

public class DeviceItem {
	String ip;
	public DeviceItem(String _ip) {
		ip = _ip;
	}
	
	public String getIp() {
		return ip;
	}
	
	/**
	 * Connect to a device
	 * @return Remote object represent the connection between phone and device
	 * @throws IOException
	 */
	public Remote connect() {
		Remote r = Remote.getInstance();		
		r.connect(this.ip);
//		Log.i("Yea", "connected");
		return r;
	}
}
