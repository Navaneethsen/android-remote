package com.axcoto.shinjuku.maki;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;


/*
 * @" ",
                     @"0", @"1", @"2", @"3", @"4", @"5", @"6", @"7", @"8", @"9", 
                     
                     @"~",  @"!", @"@", @"#", @"$", 
                     @"%",  @"^", @"&", @"*", @"(", 
                     @")",  @"-", @"_", @"+", @"=", 
                     @"[",  @"]", @"{", @"}", @"|", 
                     @"\\", @":", @";", @"\"", @"\'", 
                     @"<",  @",", @">", @".", @"?", 
                     @"/",  @"`",
                     
                     @"a", @"b", @"c", @"d", @"e", 
                     @"f", @"g", @"h", @"i", @"j", 
                     @"k", @"l", @"m", @"n", @"o", 
                     @"p", @"q", @"r", @"s", @"t", 
                     @"u", @"v", @"w", @"x", @"y", @"z", 
                     
                     @"power",  @"setup",   @"eject",  @"tvmode",  @"mute", 
                     @"delete", @"cap_num", @"return",  @"source", 
                     @"up",    @"left",    @"enter",  @"down",   @"right", 
                     @"info",   @"home",   @"menu",
                     
                     @"prev",  @"play",  @"next",   @"title",    @"rewind", @"stop",     @"forward", @"repeat",
                     @"angle", @"pause", @"slow",   @"timeseek",  @"audio",  @"subtitle", @"zoom",
                     @"red",   @"green", @"yellow", @"blue",
                     
                     @"volup", @"voldown",
                     
                     @"sync_hd", @"sync_mp3",
                     
     
                     
                     
    if ([platform isEqualToString:@"orchid"]) {
        objects = [NSArray arrayWithObjects: 
                  @"2,0", 
                  @"1,0", @"4,1" ,@"4,2" ,@"4,3" ,@"4,4" ,@"4,5" ,@"4,6" ,@"5,7" ,@"4,8" ,@"5,9",
                  
                  //!@#$%%^^^& chars
                   @"31,1", @"6,1", @"11,1", @"15,1", @"16,1", 
                   @"17,1", @"18,1", @"19,1", @"20,1", @"9,1", 
                   @"10,1", @"8,1", @"14,1", @"21,1", @"22,1", 
                   @"25,1", @"26,1", @"23,1", @"24,1", @"27,1", 
                   @"32,1", @"13,1", @"33,1", @"7,1", @"4,1", 
                   @"28,1", @"3,1", @"29,1", @"2,1", @"5,1", 
                   @"12,1", @"30,1",                          
                  
                  @"1,2", @"2,2", @"3,2", @"1,3", @"2,3", 
                  @"3,3", @"1,4", @"2,4", @"3,4", @"1,5", 
                  @"2,5", @"3,5", @"1,6", @"2,6", @"3,6", 
                  @"1,7", @"2,7", @"3,7", @"4,7", @"1,8", 
                  @"2,8", @"3,8", @"1,.119", @"2,9", @"3,9", @"4,9", 
                  
                  @"1,x", @"50,e", @"1,j", @"1,T", @"1,u", 
                  @"1,c", @"1,l",  @"1,E", @"1,B", // E->o 
                  @"1,U", @"1,L", @"1,\n", @"1,D", @"1,R",
                  @"1,i", @"1,O", @"1,m",
                  
                  @"1,v", @"1,y", @"1,n", @"1,t", @"1,w", @"1,s", @"1,f", @"1,r", 
                  @"1,N", @"1,p", @"1,d", @"1,H", @"1,a", @"1,b", @"1,z",
                  @"1,P", @"1,G", @"1,Y", @"1,K",
                  
                  @"1,+", @"1,-",
                  
                  @"1,I", @"1,M",
                   
                  nil
                  ]; 
    } else {
        objects = [NSArray arrayWithObjects: 
               
                  @"2,0",                   
                   @"1,0", @"1,1" ,@"1,2" ,@"1,3" ,@"1,4" ,@"1,5" ,@"1,6" ,@"1,7" ,@"1,8" ,@"1,9",
                   
                   @"31,1", @"6,1",  @"11,1", @"15,1", @"16,1", 
                   @"17,1", @"18,1", @"19,1", @"20,1", @"9,1", 
                   @"10,1", @"8,1",  @"14,1", @"21,1", @"22,1", 
                   @"25,1", @"26,1", @"23,1", @"24,1", @"27,1", 
                   @"32,1", @"13,1", @"33,1", @"7,1",  @"4,1", 
                   @"28,1", @"3,1",  @"29,1", @"2,1",  @"5,1", 
                   @"12,1", @"30,1",                                             
                   
             
                   @"2,2", @"3,2", @"4,2", @"2,3", @"3,3", 
                   @"4,3", @"2,4", @"3,4", @"4,4", @"2,5", 
                   @"3,5", @"4,5", @"2,6", @"3,6", @"4,6", 
                   @"2,7", @"3,7", @"4,7", @"5,7", @"2,8", 
                   @"3,8", @"4,8", @"2,9", @"3,9", @"4,9", @"5,9", 
                   
                   @"1,x", @"1,e", @"1,j",  @"1,T", @"1,u", 
                   @"1,c", @"1,l", @"1,o",  @"1,B",          // E->o 
                   @"1,U", @"1,L", @"1,\n", @"1,D", @"1,R",
                   @"1,i", @"1,O", @"1,m",
                   
                   @"1,v", @"1,y", @"1,n", @"1,t", @"1,w", @"1,s", @"1,f", @"1,r", 
                   @"1,N", @"1,p", @"1,d", @"1,H", @"1,a", @"1,b", @"1,z",
                   @"1,P", @"1,G", @"1,Y", @"1,K",
                   
                   @"1,+", @"1,-",
                   
                  @"1,I", @"1,M",
                   
                   nil
                   ];
    }

                     
                     
 */

public class Remote {
	private boolean connected = false;
	private String ip;
	protected static  Remote instance = null;
	public static final int TCP_PORT=30000;
	Socket clientSocket = null;
	DataOutputStream outToServer = null;
	PrintWriter printer;
	
	public Map<String, String> remoteKeyCode;
	
	final String[] KEYNAME = {" ",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", 
            
            "~",  "!", "", "#", "$", 
            "%",  "^", "&", "*", "(", 
            ")",  "-", "_", "+", "=", 
            "[",  "]", "{", "}", "|", 
            "\\", ":", ";", "\"", "\'", 
            "<",  ",", ">", ".", "?", 
            "/",  "`",
            
            "a", "b", "c", "d", "e", 
            "f", "g", "h", "i", "j", 
            "k", "l", "m", "n", "o", 
            "p", "q", "r", "s", "t", 
            "u", "v", "w", "x", "y", "z", 
            
            "power",  "setup",   "eject",  "tvmode",  "mute", 
            "delete", "cap_num", "return",  "source", 
            "up",    "left",    "enter",  "down",   "right", 
            "info",   "home",   "menu",
            
            "prev",  "play",  "next",   "title",    "rewind", "stop",     "forward", "repeat",
            "angle", "pause", "slow",   "timeseek",  "audio",  "subtitle", "zoom",
            "red",   "green", "yellow", "blue",
            
            "volup", "voldown",
            
            "sync_hd", "sync_mp3"};
	
	final String[] KEY_CODE_ORCHID = {
			"2,0", 
            "1,0", "4,1" ,"4,2" ,"4,3" ,"4,4" ,"4,5" ,"4,6" ,"5,7" ,"4,8" ,"5,9",
            
            //!@#$%%^^^& chars
             "31,1", "6,1", "11,1", "15,1", "16,1", 
             "17,1", "18,1", "19,1", "20,1", "9,1", 
             "10,1", "8,1", "14,1", "21,1", "22,1", 
             "25,1", "26,1", "23,1", "24,1", "27,1", 
             "32,1", "13,1", "33,1", "7,1", "4,1", 
             "28,1", "3,1", "29,1", "2,1", "5,1", 
             "12,1", "30,1",                          
            
            "1,2", "2,2", "3,2", "1,3", "2,3", 
            "3,3", "1,4", "2,4", "3,4", "1,5", 
            "2,5", "3,5", "1,6", "2,6", "3,6", 
            "1,7", "2,7", "3,7", "4,7", "1,8", 
            "2,8", "3,8", "1,.119", "2,9", "3,9", "4,9", 
            
            "1,x", "50,e", "1,j", "1,T", "1,u", 
            "1,c", "1,l",  "1,E", "1,B", // E->o 
            "1,U", "1,L", "1,\n", "1,D", "1,R",
            "1,i", "1,O", "1,m",
            
            "1,v", "1,y", "1,n", "1,t", "1,w", "1,s", "1,f", "1,r", 
            "1,N", "1,p", "1,d", "1,H", "1,a", "1,b", "1,z",
            "1,P", "1,G", "1,Y", "1,K",
            
            "1,+", "1,-",
            
            "1,I", "1,M"
	};
	
	final String[] KEY_CODE_GENESIS = {
			 "2,0",                   
             "1,0", "1,1" ,"1,2" ,"1,3" ,"1,4" ,"1,5" ,"1,6" ,"1,7" ,"1,8" ,"1,9",
             
             "31,1", "6,1",  "11,1", "15,1", "16,1", 
             "17,1", "18,1", "19,1", "20,1", "9,1", 
             "10,1", "8,1",  "14,1", "21,1", "22,1", 
             "25,1", "26,1", "23,1", "24,1", "27,1", 
             "32,1", "13,1", "33,1", "7,1",  "4,1", 
             "28,1", "3,1",  "29,1", "2,1",  "5,1", 
             "12,1", "30,1",                                             
             
       
             "2,2", "3,2", "4,2", "2,3", "3,3", 
             "4,3", "2,4", "3,4", "4,4", "2,5", 
             "3,5", "4,5", "2,6", "3,6", "4,6", 
             "2,7", "3,7", "4,7", "5,7", "2,8", 
             "3,8", "4,8", "2,9", "3,9", "4,9", "5,9", 
             
             "1,x", "1,e", "1,j",  "1,T", "1,u", 
             "1,c", "1,l", "1,o",  "1,B",          // E->o 
             "1,U", "1,L", "1,\n", "1,D", "1,R",
             "1,i", "1,O", "1,m",
             
             "1,v", "1,y", "1,n", "1,t", "1,w", "1,s", "1,f", "1,r", 
             "1,N", "1,p", "1,d", "1,H", "1,a", "1,b", "1,z",
             "1,P", "1,G", "1,Y", "1,K",
             
             "1,+", "1,-",
             
            "1,I", "1,M"	
	};
	
	public static Remote getInstance() {
		if (instance == null) {
			synchronized (Remote.class) {
				if (instance == null) {
					instance = new Remote();
				}
			}
		}
		return instance;
	}

	public Remote() {
		remoteKeyCode = new HashMap<String, String>();
		for (int i=0; i<this.KEYNAME.length; i++) {
			remoteKeyCode.put(KEYNAME[i], this.KEY_CODE_GENESIS[i]);
		}
	}

	public void execute(String command) throws IOException, Exception {		
		if (!remoteKeyCode.containsKey(command)) {
			throw new Exception("Key not found");
		}
		String k = remoteKeyCode.get(command);
		String[] part = k.split(",");
		if (clientSocket.isOutputShutdown()) {
			Log.d("MAKI:: REMOTE", "Connection is broken");
			throw new IOException();
		}
		
		if (clientSocket.isInputShutdown()) {
			Log.d("MAKI:: REMOTE", "Connection is broken");
			throw new IOException();
		}
		
		if (printer.checkError()) {
			Log.d("MAKI:: REMOTE", "Connection is broken");			
		}
		
		for (int count=0; count<Integer.parseInt(part[0]); count++) {
			Log.i("MAKI:: REMOTE", "Send key " + part[1]);
			try {
				outToServer.writeBytes(part[1]);	
				outToServer.flush();
			} catch (Exception e) {
				Log.d("MAKI: REMOTE", "Exception");
				throw e;
			}
		}
	}
	
	/**
	 * Disconnect 
	 */
	public boolean disConnect() {
		Log.i("MAKI:: REMOTE", "Trying to close socket connection");
		
		try {
			this.connected = false;
			outToServer.flush();
			//outToServer.close();
			clientSocket.shutdownOutput();
			clientSocket.close();
			Log.i("MAKI:: REMOTE", "Socket closed");
		} catch (IOException io) {
			Log.e("MAKI:: REMOTE :: ERROR", "Cannot close socket data streaming. " + io.getStackTrace());
			return false;
		} catch (Exception e) {
			Log.e("MAI:: REMOTE :: ERROR", "Unknow error. " + e.getStackTrace());
			return false;
		}
		return true;
	}
	
	public Socket connect(String ip) throws IOException, Exception {
		this.ip = ip;
		connected = false;
		try {
			clientSocket = new Socket(this.ip, Remote.TCP_PORT);			
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			printer = new PrintWriter(clientSocket.getOutputStream(), true);			
			connected = true;
		}
		catch (SocketException e) {
			Log.e("ERROR:","Socket Exception");
		}
		catch (SecurityException e) {
			Log.e("ERROR:","Security Exception");
		}
		catch (UnknownHostException e) {
			Log.e("ERROR:","Unknown Host Exception");
		}
		catch (SocketTimeoutException e) {
			Log.e("ERROR:","SocketTimeoutException");
		}
		catch (IllegalBlockingModeException e) {
			Log.e("ERROR:","IllegalBlockingModeException");
		}
		catch (IOException io) {
			Log.e("ERROR:","I/0 Exception");
		}
		catch (IllegalArgumentException e) {
			Log.e("ERROR:","IllegalArgumentException");			
		}		
		catch (Exception e) {
			e.printStackTrace();
			Log.e("Error:", "Some exception at Remote connect");
		}
		return clientSocket;
	}
	
	public Socket getConnection() {
		return clientSocket;
	}
	
	public boolean getConnected() {
		return this.connected;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public void close() {
		connected = false;
	}
}
