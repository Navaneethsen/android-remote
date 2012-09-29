package com.axcoto.shinjuku.maki;

public class Remote {
	private boolean connected = false;
	
	
	public void execute(String command) {
		
	}
	
	public void connect(String ip) {
		connected = true;
	}
	public void close() {
		connected = false;	
	}
}
