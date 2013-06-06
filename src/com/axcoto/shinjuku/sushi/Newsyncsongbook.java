package com.axcoto.shinjuku.sushi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.axcoto.shinjuku.maki.Song;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Newsyncsongbook {
	public static final int TCP_PORT = 5230;
	public static final int SOCKET_TIMEOUT = 7000;
	Socket clientSocket = null;
	DataOutputStream outToServer = null;
	DataInputStream inToServer = null;
	PrintWriter printer;
	String TAG = "iCeeNee sunc song book";
	
	/**
	 * Create connection to socket
	 * @param ip IP Address of BeeGee
	 * @return clientSocket
	 * @throws IOException
	 * @throws Exception
	 */
	public Socket connect(String ip) throws IOException, Exception {
		try {
			clientSocket = new Socket();
			SocketAddress sockAddr = new InetSocketAddress(ip,
					TCP_PORT);
			clientSocket.connect(sockAddr, SOCKET_TIMEOUT);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inToServer = new DataInputStream(clientSocket.getInputStream());
			printer = new PrintWriter(clientSocket.getOutputStream(), true);
			Log.i(TAG,"IP of server: " + clientSocket.getInetAddress().toString());
		} catch (SocketTimeoutException e) {
			Log.e(TAG, "SocketTimeoutException");
			SongActivity.errortoast = "Can not connect to CeeNee  media player. Please check device's connection and try again!";
			return null;
		} catch (SocketException e) {
			Log.e(TAG, "Socket Exception");
			SongActivity.errortoast = "Can not connect to CeeNee  media player. Please check device's connection and try again!";
			return null;
		} catch (SecurityException e) {
			Log.e(TAG, "Security Exception");
			SongActivity.errortoast = "Can not connect to CeeNee  media player. Please check device's connection and try again!";
			return null;
		} catch (UnknownHostException e) {
			Log.e(TAG, "Unknown Host Exception");
			SongActivity.errortoast = "Can not connect to CeeNee  media player. Please check device's connection and try again!";
			return null;
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "IllegalArgumentException");
			SongActivity.errortoast = "Can not connect to CeeNee  media player. Please check device's connection and try again!";
			return null;
		} catch (IllegalBlockingModeException e) {
			Log.e(TAG, "IllegalBlockingModeException");
			SongActivity.errortoast = "Can not connect to CeeNee  media player. Please check device's connection and try again!";
			return null;
		} catch (IOException e) {
			Log.e(TAG, "I/0 Exception");
			SongActivity.errortoast = "Can not connect to CeeNee  media player. Please check device's connection and try again!";
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "Some exception at Remote connect");
			SongActivity.errortoast = "Can not connect to CeeNee  media player. Please check device's connection and try again!";
			return null;
		}
		return clientSocket;
	}
	
	public Boolean createfilestring(String info) {
		try {
			FileOutputStream out = new FileOutputStream(new File(SongActivity.t.getFilesDir() + "/string.xml"));
			byte [] buffer  = info.getBytes();
			out.write(buffer);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException: " + e.toString());
			return false;
		}
		return true;
	}
	
	public Boolean SendXMLinfotoboard(Socket sock, String info) {
		try {
			BufferedOutputStream out = new BufferedOutputStream(sock.getOutputStream());
			FileInputStream in = new FileInputStream(new File(SongActivity.t.getFilesDir() + "/string.xml"));
			Log.i("iCeeNee New sync song book ","SendXMLinfotoboard");
			byte [] buffer  = new byte [1024];
	        int bytesRead =0;
	        while ((bytesRead = in.read(buffer)) > 0) 
	        {
	        	 out.write(buffer, 0, bytesRead);
	        }
	        out.flush();
	        in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException: " + e.toString());
			return false;
		}
		return true;
	}
	
	public Boolean ReceiveXMLfilefromboard(Socket sock, String filename) {
		try {
			Log.i("iCeeNee New sync song book ","ReceiveXMLfilefromboard");
			BufferedInputStream in = new BufferedInputStream(sock.getInputStream());
			FileOutputStream fileout = new FileOutputStream(filename);
			byte [] bytebuffer  = new byte [1024];
	        int bytesRead =0;
	        while ((bytesRead = in.read(bytebuffer)) >0) 
	        {
	        	 fileout.write(bytebuffer, 0, bytesRead);
	        }
	        fileout.flush();
	        fileout.close();
	        in.close();// also close socket 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException: " + e.toString());
			return false;
		}
		return true;
	}
	
	public int CheckReceiveXMLfile (String filename) {
		int totalitems;
		NodeList listOfitems;
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document filexml = docBuilder.parse (new File(filename));
	        // normalize text representation
	        filexml.getDocumentElement ().normalize ();
	        listOfitems = filexml.getElementsByTagName("iCeeNee");
	        totalitems = listOfitems.getLength();
		}catch (SAXParseException err) {
	        Log.e (TAG," " + err.getMessage ());
	        return -1;
        }catch (SAXException e) {
        	Log.e (TAG,e.toString());
        	return -1;
        }catch (Throwable t) {
        	Log.e (TAG,t.toString());
        	return -1;
        }
		if (totalitems > 0)
		{
			Node firstitemNode = listOfitems.item(0);
			if(firstitemNode.getNodeType() == Node.ELEMENT_NODE){
                Element firstSongElement = (Element)firstitemNode;
                return Integer.parseInt(firstSongElement.getAttribute("error"));
			}
		}
		return 3;
	}
	
	
	public void SavefiletoKaraoke(String filename, String filenamexml) throws IOException {
		FileOutputStream out = new FileOutputStream(filenamexml);
		FileInputStream in = new FileInputStream(filename);
		byte [] bytebuffer  = new byte [1024];
        int bytesRead =0;
        while ((bytesRead = in.read(bytebuffer)) >0) 
        {
        	 out.write(bytebuffer, 0, bytesRead);
        }
        out.flush();
        out.close();
        in.close();
	}
	
	/**
	 * Get IP address of devices
	 * @return IP address of android device
	 */
	
	public String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = 
	        		NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) 
	        {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); 
	            		enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    String ip = inetAddress.getHostAddress();
	                    return ip;
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e("IP address", ex.toString());
	    }
	    return null;
	}
	
	public String getWifiIpAddr() {
	   WifiManager wifiManager = (WifiManager) SongActivity.t.getSystemService(Context.WIFI_SERVICE); 
	   WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	   int ip = wifiInfo.getIpAddress();
	
	   String ipString = String.format(
	   "%d.%d.%d.%d",
	   (ip & 0xff),
	   (ip >> 8 & 0xff),
	   (ip >> 16 & 0xff),
	   (ip >> 24 & 0xff));

	   return ipString;
	}
}
