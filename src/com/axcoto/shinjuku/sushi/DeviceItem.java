package com.axcoto.shinjuku.sushi;

import java.io.IOException;

import android.util.Log;

import com.axcoto.shinjuku.maki.Remote;

public class DeviceItem {
	String ip;
	public DeviceItem(String _ip) {
		ip = _ip;
	}
	
	/**
	 * Check connection status between the app and this device.
	 * @return
	 */
	public Boolean isConnected() {
		if (!Remote.getInstance().getConnected()) {
			return false;
		}
		
		Log.i("SUSHI: COMPARE ", this.ip);
		Log.i("SUSHI: COMPARE", Remote.getInstance().getIp());
		return this.ip.equals(Remote.getInstance().getIp());
	}
	
	public String getIp() {
		return ip;
	}
	
	/**
	 * Connect to a device
	 * @return Remote object represent the connection between phone and device
	 * @throws IOException
	 */
	public Remote connect() throws IOException {
		Remote r = Remote.getInstance();
		
		try {
			r.connect(this.ip);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return r;
	}
}
