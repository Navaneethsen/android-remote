package com.axcoto.shinjuku.maki;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.util.Log;

public class Finder {

	protected Integer _from;
	protected Integer _to;
	protected String _maskIp;

	public void execute() {
//		int startPortRange = 0;
//		int stopPortRange = 0;
//
//		startPortRange = Integer.parseInt("2");
//		stopPortRange = Integer.parseInt("253");
//
//		for (int i = startPortRange; i <= stopPortRange; i++) {
//			try {
//				Socket ServerSok = new Socket("127.0.0.1", i);
//
//				System.out.println("Port in use: " + i);
//
//				ServerSok.close();
//			} catch (Exception e) {
//			}
//			System.out.println("Port not in use: " + i);
//		}
		
		this._from = 179;
		this._to = 200;
		this._maskIp = "192.168.0.";
		String myIp=this.myIp();
		Log.e("MAKI:", "My IP is" + myIp);
		
		for (int i = _from; i<=_to; i++) {
			try {
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(_maskIp + i, 80), 10000);
		        socket.close();
				Log.e("MAKI::FINDER", "Horay. We found out the board at: " + _maskIp +  i);				
				socket.close();
			} catch (IOException e) {
				Log.e("MAKI::FINDER", "Badly. No board at " + _maskIp +  i);				
			} catch (Exception e) {
				Log.e("MAKI::FINDER", "Horay. No board at " + _maskIp +  i);
				
			}
		}
		
	}

	public String myIp() {
		String ip = "";
		try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                   ip = inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e("MAKI: FIND IP", ex.toString());
	    }
		return ip;
	}
	

//	public class GetIPAddress {
//		InetAddress thisIp =InetAddress.getLocalHost();
//		System.out.println("IP:"+thisIp.getHostAddress());
//	}

}
