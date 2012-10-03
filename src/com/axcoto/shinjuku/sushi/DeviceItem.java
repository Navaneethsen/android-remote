package com.axcoto.shinjuku.sushi;

import java.io.IOException;

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
