package com.axcoto.shinjuku.maki;

import java.io.IOException;
import java.net.Socket;

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
		
		this._from = 2;
		this._to = 253;
		this._maskIp = "192.168.0.";
		for (int i = _from; i<=_to; i++) {
			try {
				Socket S = new Socket(_maskIp + _from.toString(), 30000);
				Log.e("MAKI::FINDER", "Horay. We found out the board at: " + _maskIp +  i);				
				S.close();
			} catch (IOException e) {
				Log.e("MAKI::FINDER", "Horay. IO. No board at " + _maskIp +  i);				
			} catch (Exception e) {
				Log.e("MAKI::FINDER", "Horay. No board at " + _maskIp +  i);
				
			}
		}
		
	}

	public void finder() {

	}

}
