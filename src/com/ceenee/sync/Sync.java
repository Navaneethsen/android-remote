package com.ceenee.sync;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import com.ceenee.maki.MyLog;
import com.ceenee.remote.Remote;

/**
 * Socket Sync Class. This handles syncing process. It creates a socket
 * connection to the board at port 5230, send a POST request and get the song
 * book back.
 * 
 * @see OnSyncListener
 * @author kureikain
 * 
 */
public class Sync {

	Socket clientSocket = null;
	DataOutputStream outToServer = null;
	DataInputStream inToServer = null;
	int TCP_PORT = 5230;
	final int SOCKET_TIMEOUT = 7000;
	String TAG = "SONGBOOK SYNC";
	protected OnSyncListener onBookSyncListener;

	protected String command; //The command we are going to send to the board.
	protected String playerIp; //IP Address of the board
	protected String syncTo; //Write the file to this location

	/**
	 * This listener will be invoked during syncing process.
	 * This methd is chainable.
	 * @param e
	 */
	public Sync setOnBookSyncListener(OnSyncListener e) {
		onBookSyncListener = e;
		return this;
	}

	/**
	 * Set command to sync.
	 * 
	 * @param command
	 */
	public Sync setCommand(String command) {
		this.command = command;
		return this;
	}
	
	/**
	 * Set player ip. This method is chainable.
	 * @param ip
	 * @return
	 */
	public Sync setPlayerIp(String ip) {
		this.playerIp = ip;
		return this;
	}
	
	/**
	 * Set destination file path of song book.
	 * 
	 * @param syncToFile
	 * @return this for chaianble.
	 */
	public Sync setSyncTo(String syncToFile) {
		this.syncTo = syncToFile;
		return this;
	}

	/**
	 * Connect to the board.
	 * 
	 * @param ip
	 * @return true if succesfully to copy
	 * 
	 */
	public boolean connect(String ip) {
		SocketAddress sockAddr = new InetSocketAddress(playerIp, TCP_PORT);
		try {
			clientSocket.connect(sockAddr, SOCKET_TIMEOUT);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inToServer = new DataInputStream(clientSocket.getInputStream());
			MyLog.i(TAG, "IP of server: "
					+ clientSocket.getInetAddress().toString());
			return true;
		} catch (SocketTimeoutException e) {
			MyLog.e(TAG, "SocketTimeoutException");
			// SongActivity.errortoast =
			// "Can not connect to CeeNee player. Please check device's connection and try again!";
		} catch (SocketException e) {
			e.printStackTrace();
			MyLog.e(TAG, "Socket Exception");
			// SongActivity.errortoast =
			// "Can not connect to CeeNee player. Please check device's connection and try again!";
		} catch (SecurityException e) {
			MyLog.e(TAG, "Security Exception");
			// SongActivity.errortoast =
			// "Can not connect to CeeNee player. Please check device's connection and try again!";
		} catch (UnknownHostException e) {
			MyLog.e(TAG, "Unknown Host Exception");
			// SongActivity.errortoast =
			// "Can not connect to CeeNee player. Please check device's connection and try again!";
		} catch (IllegalArgumentException e) {
			MyLog.e(TAG, "IllegalArgumentException");
			// SongActivity.errortoast =
			// "Can not connect to CeeNee player. Please check device's connection and try again!";
		} catch (IllegalBlockingModeException e) {
			MyLog.e(TAG, "IllegalBlockingModeException");
			// SongActivity.errortoast =
			// "Can not connect to CeeNee player. Please check device's connection and try again!";
		} catch (IOException e) {
			MyLog.e(TAG, "I/0 Exception");
			// SongActivity.errortoast =
			// "Can not connect to CeeNee player. Please check device's connection and try again!";
		} catch (Exception e) {
			e.printStackTrace();
			MyLog.e(TAG, "Some exception at Remote connect");
			// SongActivity.errortoast =
			// "Can not connect to CeeNee player. Please check device's connection and try again!";
		}
		return false;
	}

	public Sync(String command) {
		this.command = command;
		clientSocket = new Socket();
	}

	public boolean start() {
		if (!this.connect(playerIp)) {
			this.onBookSyncListener.onSyncFail(new Exception(
					"Cannot connect to the board"));
		}

		try {
			outToServer.writeChars(command);
			MyLog.i("iCeeNee New sync song book ", "ReceiveXMLfilefromboard");
			BufferedInputStream in = new BufferedInputStream(
					clientSocket.getInputStream());
			// FileOutputStream fileout = new FileOutputStream();
			this.onBookSyncListener.onReadyReceiveBook();
			byte[] bytebuffer = new byte[1024];
			int bytesRead = 0;
			
			FileOutputStream songBookFileWriter = new FileOutputStream(syncTo);
			MyLog.i("SYNC_WRITE_TO", syncTo);

			this.onBookSyncListener.onProcessBook();			
			while ((bytesRead = in.read(bytebuffer)) > 0) {
				songBookFileWriter.write(bytebuffer, 0, bytesRead);
			}
			songBookFileWriter.flush();
			songBookFileWriter.close();
			in.close();
			MyLog.i("Sync Status: ", "SONG BOOK RETRIEVED");
			if (this.onBookSyncListener !=null) {
				this.onBookSyncListener.onReceivedBook(syncTo);
			}
			in.close();// also close socket
			this.onBookSyncListener.onFinishSyncing();
			clientSocket.close();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			MyLog.e(TAG, "IOException: " + e.toString());
			return false;
			
		} finally {
			try {
				clientSocket.close();	
			} catch (Exception e) {
				e.printStackTrace();
				MyLog.e("SOCKET: ERROR", "Cannot close socket");
			}				
		}
		
		return false;
	}

}
