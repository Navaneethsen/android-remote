package com.ceenee.maki;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import com.ceenee.q.DeviceActivity;

import android.util.Log;
import android.widget.Toast;

public class Finder {

	protected Integer _from;
	protected Integer _to;
	protected String _maskIpAddress;
	protected String _ipAddress;
	protected static Finder instance = null;

	public void setFrom(int from) {
		this._from = from;
	}

	public void setTo(int from) {
		this._from = from;
	}

	public static Finder getInstance() {
		if (instance == null) {
			synchronized (Finder.class) {
				if (instance == null) {
					instance = new Finder();
				}
			}
		}
		return instance;
	}

	public Finder() {
		// this.resolve();
	}

	public void execute() {
		this._from = 148;
		this._to = 152;
		this._maskIpAddress = "192.168.0.";
		String myIp = _ipAddress;
		MyLog.e("MAKI:", "My IP is" + myIp);

		for (int i = _from; i <= _to; i++) {
			try {
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(_maskIpAddress + i,
						Remote.TCP_PORT), 10000);
				socket.close();
				MyLog.e("MAKI::FINDER", "Horay. We found out the board at: "
						+ _maskIpAddress + i);
				socket.close();
			} catch (IOException e) {
				MyLog.e("MAKI::FINDER", "Badly. No board at " + _maskIpAddress
						+ i);
			} catch (Exception e) {
				MyLog.e("MAKI::FINDER", "Horay. No board at " + _maskIpAddress
						+ i);

			}
		}
	}

	public boolean isPortOpen(String ip, int port, int timeout) {
		boolean found = false;
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port), timeout);
			socket.close();
			MyLog.i("MAKI::FINDER", "Horay. We found out the board at: " + ip);
			found = true;
		} catch (IOException e) {
			MyLog.w("MAKI::FINDER", "Badly. No board at " + ip);
		} catch (Exception e) {
			MyLog.w("MAKI::FINDER", "Horay. No board at " + ip);
		}
		return found;
	}

	public boolean isPortOpen(String ip, int port) {
		return this.isPortOpen(ip, port, 500);
	}

	/*
	 * Get phone ip and mask address. maskIpAddress is without "." notation
	 * 
	 * @return void
	 */
	public boolean resolve() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress
									.getHostAddress())) {
						_ipAddress = inetAddress.getHostAddress();
						// MyLog.e("ipAddress: ", _ipAddress);
						String[] part = _ipAddress.split("\\.");
						MyLog.e("MAKI", "Current IP of Device is " + _ipAddress);
						MyLog.e("MAKI: Finder",
								"Ip Part is " + Arrays.toString(part));
						_maskIpAddress = part[0] + "." + part[1] + "."
								+ part[2];
						MyLog.i("_maskIpAddress", _maskIpAddress);
					}
				}
			}
			return true;
		} catch (SocketException ex) {
			MyLog.e("MAKI: FIND IP", ex.toString());
			return false;
		}

	}

	public String getMaskIpAddress() {
		return _maskIpAddress;
	}
}
