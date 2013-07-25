package com.ceenee.maki;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.HashMap;
import java.util.Map;

import com.axcoto.shinjuku.sushi.ServerUtilities;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Remote class. A virtual mapping between the android remote key and the
 * corresponding physical key
 * 
 */

/*
 * @" ",
 * 
 * @"0", @"1", @"2", @"3", @"4", @"5", @"6", @"7", @"8", @"9",
 * 
 * @"~", @"!", @"@", @"#", @"$",
 * 
 * @"%", @"^", @"&", @"*", @"(",
 * 
 * @")", @"-", @"_", @"+", @"=",
 * 
 * @"[", @"]", @"{", @"}", @"|",
 * 
 * @"\\", @":", @";", @"\"", @"\'",
 * 
 * @"<", @",", @">", @".", @"?",
 * 
 * @"/", @"`",
 * 
 * @"a", @"b", @"c", @"d", @"e",
 * 
 * @"f", @"g", @"h", @"i", @"j",
 * 
 * @"k", @"l", @"m", @"n", @"o",
 * 
 * @"p", @"q", @"r", @"s", @"t",
 * 
 * @"u", @"v", @"w", @"x", @"y", @"z",
 * 
 * @"power", @"setup", @"eject", @"tvmode", @"mute",
 * 
 * @"delete", @"cap_num", @"return", @"source",
 * 
 * @"up", @"left", @"enter", @"down", @"right",
 * 
 * @"info", @"home", @"menu",
 * 
 * @"prev", @"play", @"next", @"title", @"rewind", @"stop", @"forward",
 * @"repeat",
 * 
 * @"angle", @"pause", @"slow", @"timeseek", @"audio", @"subtitle", @"zoom",
 * 
 * @"red", @"green", @"yellow", @"blue",
 * 
 * @"volup", @"voldown",
 * 
 * @"sync_hd", @"sync_mp3",
 * 
 * 
 * 
 * 
 * if ([platform isEqualToString:@"orchid"]) { objects = [NSArray
 * arrayWithObjects:
 * 
 * @"2,0",
 * 
 * @"1,0", @"4,1" ,@"4,2" ,@"4,3" ,@"4,4" ,@"4,5" ,@"4,6" ,@"5,7" ,@"4,8"
 * ,@"5,9",
 * 
 * //!@#$%%^^^& chars
 * 
 * @"31,1", @"6,1", @"11,1", @"15,1", @"16,1",
 * 
 * @"17,1", @"18,1", @"19,1", @"20,1", @"9,1",
 * 
 * @"10,1", @"8,1", @"14,1", @"21,1", @"22,1",
 * 
 * @"25,1", @"26,1", @"23,1", @"24,1", @"27,1",
 * 
 * @"32,1", @"13,1", @"33,1", @"7,1", @"4,1",
 * 
 * @"28,1", @"3,1", @"29,1", @"2,1", @"5,1",
 * 
 * @"12,1", @"30,1",
 * 
 * @"1,2", @"2,2", @"3,2", @"1,3", @"2,3",
 * 
 * @"3,3", @"1,4", @"2,4", @"3,4", @"1,5",
 * 
 * @"2,5", @"3,5", @"1,6", @"2,6", @"3,6",
 * 
 * @"1,7", @"2,7", @"3,7", @"4,7", @"1,8",
 * 
 * @"2,8", @"3,8", @"1,.119", @"2,9", @"3,9", @"4,9",
 * 
 * @"1,x", @"50,e", @"1,j", @"1,T", @"1,u",
 * 
 * @"1,c", @"1,l", @"1,E", @"1,B", // E->o
 * 
 * @"1,U", @"1,L", @"1,\n", @"1,D", @"1,R",
 * 
 * @"1,i", @"1,O", @"1,m",
 * 
 * @"1,v", @"1,y", @"1,n", @"1,t", @"1,w", @"1,s", @"1,f", @"1,r",
 * 
 * @"1,N", @"1,p", @"1,d", @"1,H", @"1,a", @"1,b", @"1,z",
 * 
 * @"1,P", @"1,G", @"1,Y", @"1,K",
 * 
 * @"1,+", @"1,-",
 * 
 * @"1,I", @"1,M",
 * 
 * nil ]; } else { objects = [NSArray arrayWithObjects:
 * 
 * @"2,0",
 * 
 * @"1,0", @"1,1" ,@"1,2" ,@"1,3" ,@"1,4" ,@"1,5" ,@"1,6" ,@"1,7" ,@"1,8"
 * ,@"1,9",
 * 
 * @"31,1", @"6,1", @"11,1", @"15,1", @"16,1",
 * 
 * @"17,1", @"18,1", @"19,1", @"20,1", @"9,1",
 * 
 * @"10,1", @"8,1", @"14,1", @"21,1", @"22,1",
 * 
 * @"25,1", @"26,1", @"23,1", @"24,1", @"27,1",
 * 
 * @"32,1", @"13,1", @"33,1", @"7,1", @"4,1",
 * 
 * @"28,1", @"3,1", @"29,1", @"2,1", @"5,1",
 * 
 * @"12,1", @"30,1",
 * 
 * 
 * @"2,2", @"3,2", @"4,2", @"2,3", @"3,3",
 * 
 * @"4,3", @"2,4", @"3,4", @"4,4", @"2,5",
 * 
 * @"3,5", @"4,5", @"2,6", @"3,6", @"4,6",
 * 
 * @"2,7", @"3,7", @"4,7", @"5,7", @"2,8",
 * 
 * @"3,8", @"4,8", @"2,9", @"3,9", @"4,9", @"5,9",
 * 
 * @"1,x", @"1,e", @"1,j", @"1,T", @"1,u",
 * 
 * @"1,c", @"1,l", @"1,o", @"1,B", // E->o
 * 
 * @"1,U", @"1,L", @"1,\n", @"1,D", @"1,R",
 * 
 * @"1,i", @"1,O", @"1,m",
 * 
 * @"1,v", @"1,y", @"1,n", @"1,t", @"1,w", @"1,s", @"1,f", @"1,r",
 * 
 * @"1,N", @"1,p", @"1,d", @"1,H", @"1,a", @"1,b", @"1,z",
 * 
 * @"1,P", @"1,G", @"1,Y", @"1,K",
 * 
 * @"1,+", @"1,-",
 * 
 * @"1,I", @"1,M",
 * 
 * nil ]; }
 */

public class Remote {
	private boolean connected = false;
	private String ip = "No Host"; // default ip if not connected
	protected static Remote instance = null;
	public static final int TCP_PORT = 30000; // opened port of the CeeNee
												// product
	public static final int SOCKET_TIMEOUT = 7000;
	Socket clientSocket = null;
	DataOutputStream outToServer = null;
	DataInputStream inToServer = null;
	PrintWriter printer;

	public Map<String, String> remoteKeyCode;

	final String[] KEYNAME = { " ", "0", "1", "2", "3", "4", "5", "6", "7",
			"8", "9",

			"~", "!", "", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_",
			"+", "=", "[", "]", "{", "}", "|", "\\", ":", ";", "\"", "\'", "<",
			",", ">", ".", "?", "/", "`",

			"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
			"n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",

			"power", "setup", "eject", "tvmode", "mute", "delete", "cap_num",
			"return", "source", "up", "left", "enter", "down", "right", "info",
			"home", "menu",

			"prev", "play", "next", "title", "rewind", "stop", "forward",
			"repeat", "angle", "pause", "slow", "timeseek", "audio",
			"subtitle", "zoom", "red", "green", "yellow", "blue",

			"volup", "voldown",

			"sync_hd", "sync_mp3" };

	final String[] KEY_CODE_ORCHID = { "2,0", "1,0", "4,1", "4,2",
			"4,3",
			"4,4",
			"4,5",
			"4,6",
			"5,7",
			"4,8",
			"5,9",

			// !@#$%%^^^& chars
			"31,1", "6,1", "11,1", "15,1", "16,1", "17,1", "18,1", "19,1",
			"20,1", "9,1", "10,1", "8,1", "14,1", "21,1", "22,1", "25,1",
			"26,1", "23,1", "24,1", "27,1", "32,1", "13,1", "33,1", "7,1",
			"4,1", "28,1", "3,1", "29,1", "2,1", "5,1", "12,1", "30,1",

			"1,2", "2,2", "3,2", "1,3", "2,3", "3,3", "1,4", "2,4", "3,4",
			"1,5", "2,5", "3,5", "1,6", "2,6", "3,6", "1,7", "2,7", "3,7",
			"4,7", "1,8", "2,8", "3,8", "1,.119", "2,9", "3,9", "4,9",

			"1,x", "50,e", "1,j", "1,T", "1,u", "1,c", "1,l",
			"1,E",
			"1,B", // E->o
			"1,U", "1,L", "1,\n", "1,D", "1,R", "1,i", "1,O", "1,m",

			"1,v", "1,y", "1,n", "1,t", "1,w", "1,s", "1,f", "1,r", "1,N",
			"1,p", "1,d", "1,H", "1,a", "1,b", "1,z", "1,P", "1,G", "1,Y",
			"1,K",

			"1,+", "1,-",

			"1,I", "1,M" };

	final String[] KEY_CODE_GENESIS = { "2,0", "1,0", "1,1", "1,2", "1,3",
			"1,4", "1,5", "1,6", "1,7", "1,8", "1,9",

			"31,1", "6,1", "11,1", "15,1", "16,1", "17,1", "18,1", "19,1",
			"20,1", "9,1", "10,1", "8,1", "14,1", "21,1", "22,1", "25,1",
			"26,1", "23,1", "24,1", "27,1", "32,1", "13,1", "33,1", "7,1",
			"4,1", "28,1", "3,1", "29,1", "2,1", "5,1", "12,1", "30,1",

			"2,2", "3,2", "4,2", "2,3", "3,3", "4,3", "2,4", "3,4", "4,4",
			"2,5", "3,5", "4,5", "2,6", "3,6", "4,6", "2,7", "3,7", "4,7",
			"5,7", "2,8", "3,8", "4,8", "2,9", "3,9", "4,9", "5,9",

			"1,x", "1,e", "1,j", "1,T", "1,u", "1,c", "1,l",
			"1,o",
			"1,B", // E->o
			"1,U", "1,L", "1,\n", "1,D", "1,R", "1,i", "1,O", "1,m",

			"1,v", "1,y", "1,n", "1,t", "1,w", "1,s", "1,f", "1,r", "1,N",
			"1,p", "1,d", "1,H", "1,a", "1,b", "1,z", "1,P", "1,G", "1,Y",
			"1,K",

			"1,+", "1,-",

			"1,I", "1,M" };

	/**
	 * Constructor
	 */
	public Remote() {
		remoteKeyCode = new HashMap<String, String>();
		for (int i = 0; i < this.KEYNAME.length; i++) {
			remoteKeyCode.put(KEYNAME[i], this.KEY_CODE_GENESIS[i]);
		}
	}

	/**
	 * Access to the remote at that instance
	 * 
	 * @return instance of the remote
	 */
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

	/**
	 * Pressing the key
	 * 
	 * @param command
	 *            - the name of the key
	 * @throws IOException
	 * @throws Exception
	 */

	public void execute(String command) throws IOException, Exception {
		if (!remoteKeyCode.containsKey(command)) {
			throw new Exception("Key not found");
		}
		String k = remoteKeyCode.get(command);
		String[] part = k.split(",");
		if (clientSocket.isOutputShutdown()) {
			MyLog.d("MAKI:: REMOTE:: EXECUTE", "Connection is broken");
			throw new IOException();
		}

		if (clientSocket.isInputShutdown()) {
			MyLog.d("MAKI:: REMOTE:: EXECUTE", "Connection is broken");
			throw new IOException();
		}

		if (printer.checkError()) {
			MyLog.d("MAKI:: REMOTE:: EXECUTE", "Connection is broken");
		}

		for (int count = 0; count < Integer.parseInt(part[0]); count++) {
			MyLog.i("MAKI:: REMOTE:: EXECUTE", "Send key " + part[1]);
			try {
				outToServer.writeBytes(part[1]);
				outToServer.flush();
			} catch (Exception e) {
				MyLog.d("MAKI: REMOTE:: EXECUTE",
						"Exception when trying to write key code to stream"
								+ e.getMessage());
				throw e;
			}
		}
	}

	/**
	 * Close socket connection - Disconnect
	 */
	public boolean disConnect() {
		MyLog.i("MAKI:: REMOTE", "Trying to close socket connection");

		try {
			this.connected = false;
			outToServer.flush();
			// outToServer.close();
			clientSocket.shutdownOutput();
			clientSocket.close();
			MyLog.i("MAKI:: REMOTE", "Socket closed");
		} catch (IOException io) {
			MyLog.e("MAKI:: REMOTE :: ERROR",
					"Cannot close socket data streaming. " + io.getStackTrace());
			return false;
		} catch (Exception e) {
			MyLog.e("MAI:: REMOTE :: ERROR", "Unknow error. " + e.getStackTrace());
			return false;
		}
		return true;
	}

	/**
	 * Connect to the specified ip address
	 * 
	 * @param ip
	 *            the specified ip address
	 * @return clientSocket - name of the socket in connection
	 * @throws IOException
	 * @throws Exception
	 */
	public Socket connect(String ip) throws IOException, Exception {
		this.ip = ip;
		connected = false;
		try {
			clientSocket = new Socket();
			SocketAddress sockAddr = new InetSocketAddress(this.ip,
					Remote.TCP_PORT);
			clientSocket.connect(sockAddr, Remote.SOCKET_TIMEOUT);
			// clientSocket.setSoTimeout(100);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inToServer = new DataInputStream(clientSocket.getInputStream());
			printer = new PrintWriter(clientSocket.getOutputStream(), true);
			connected = true;
		} catch (SocketTimeoutException e) {
			MyLog.e("MAKI:: REMOTE:: ERROR:", "SocketTimeoutException");
			throw e;
		} catch (SocketException e) {
			MyLog.e("MAKI:: REMOTE:: ERROR:", "Socket Exception");
			throw e;
		} catch (SecurityException e) {
			MyLog.e("MAKI:: REMOTE:: ERROR:", "Security Exception");
			throw e;
		} catch (UnknownHostException e) {
			MyLog.e("MAKI:: REMOTE:: ERROR:", "Unknown Host Exception");
			throw e;
		} catch (IllegalArgumentException e) {
			MyLog.e("MAKI:: REMOTE:: ERROR:", "IllegalArgumentException");
			throw e;
		} catch (IllegalBlockingModeException e) {
			MyLog.e("MAKI:: REMOTE:: ERROR:", "IllegalBlockingModeException");
			throw e;
		} catch (IOException e) {
			MyLog.e("MAKI:: REMOTE:: ERROR:", "I/0 Exception");
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			MyLog.e("MAKI:: REMOTE:: Error:", "Some exception at Remote connect");
			throw e;
		}
		return clientSocket;
	}

	/**
	 * Get the connected socket
	 * 
	 * @return clientSocket the connected socket
	 */
	public Socket getConnection() {
		return clientSocket;
	}

	/**
	 * Check the socket status
	 * 
	 * @return connected - the current state of the socket (true/false)
	 */
	public boolean getConnected() {
		return this.connected;
	}

	/**
	 * Test the connection status from the android device to the specified ip
	 * 
	 * @param i
	 *            the ip to be checked
	 * @return true if the ip is still pingable false if the ip's ping time out
	 * @throws IOException
	 */
	public boolean testConnection(String i) throws IOException {
		Process p;
		p = new ProcessBuilder("sh").redirectErrorStream(true).start();
		DataOutputStream os = new DataOutputStream(p.getOutputStream());
		os.writeBytes("ping -c 1 -w 1 -s 1 " + i + '\n');
		os.flush();

		// Close the terminal
		os.writeBytes("exit\n");
		os.flush();

		// read ping replys
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		int count = 0;
		while ((line = reader.readLine()) != null) {
			count++;
			// MyLog.i("Pinging: ", line);
		}
		// MyLog.i("COUNT", Integer.toString(count));
		if (count > 5)
			return true;
		else
			return false;
	}

	/**
	 * Return the host ip being remembered by the Remote
	 * 
	 * @return host ip of the device
	 */
	public String getIp() {
		return this.ip;
	}

	/**
	 * Set socket connection status to false
	 */
	public void close() {
		connected = false;
	}
}
