package com.axcoto.shinjuku.sushi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.axcoto.shinjuku.maki.Finder;

public class DeviceActivity extends RootActivity {

	static final int PROGRESS_DIALOG = 0;

	private ListView listDevice;
	private ItemAdapter deviceAdapter;
	public ArrayList<DeviceItem> devices;
	public ProgressDialog progressDialog;
	public ProgressThread progressThread;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);

		listDevice = (ListView) findViewById(R.id.list_device);

		listDevice.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.e("DEVICE: CLICKED", "Click ListItem Number " + position);
				Toast.makeText(getApplicationContext(),
						"Click ListItem Number " + position, Toast.LENGTH_LONG)
						.show();
			}
		});

		String[] arrs = { "192.168.1.111", "192.168.1.112", "192.168.1.113",
				"192.168.1.114", "192.168.1.115" };
		devices = new ArrayList<DeviceItem>();
		devices.add(new DeviceItem("19"));
		devices.add(new DeviceItem("20"));
		devices.add(new DeviceItem("21"));
		devices.add(new DeviceItem("22"));

		deviceAdapter = new ItemAdapter(this, R.layout.device_item, devices);
		// this.deviceAdapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, android.R.id.text1, devices);
		deviceAdapter.notifyDataSetChanged();

		listDevice.setAdapter(deviceAdapter);
	}

	public void scanDevice(View view) {
		//devices = new ArrayList<DeviceItem>();
		deviceAdapter.clear();
		deviceAdapter.add(new DeviceItem("101"));
		deviceAdapter.add(new DeviceItem("102"));
		deviceAdapter.notifyDataSetChanged();
		showDialog(PROGRESS_DIALOG);
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(DeviceActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage("Scanning Device...");
			return progressDialog;
		default:
			return null;
		}
	}
	
	public void refresh() {
		deviceAdapter = new ItemAdapter(this, R.layout.device_item, devices);
		deviceAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog.setProgress(0);
			progressThread = new ProgressThread(handler);
			progressThread.start();
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			int total = msg.arg1;
			progressDialog.setProgress(total);
			if (total >= 100) {
				dismissDialog(PROGRESS_DIALOG);
				progressThread.setState(ProgressThread.STATE_DONE);
				
				deviceAdapter.clear();
				deviceAdapter.add(new DeviceItem("111"));
				deviceAdapter.add(new DeviceItem("122"));
				deviceAdapter.notifyDataSetChanged();

				
			}
		}
	};

	/** Nested class that performs progress calculations (counting) */
	private class ProgressThread extends Thread {
		Handler mHandler;
		final static int STATE_DONE = 0;
		final static int STATE_RUNNING = 1;
		int mState;
		int total;

		int from = 180, to = 200, checkIp = 180;
		String maskIp;

		ProgressThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			Finder f = new Finder();
			mState = STATE_RUNNING;

			// while (mState == STATE_RUNNING) {
			maskIp = "192.168.0.";
			
			while (mState == STATE_RUNNING) {
				
				try {
					if (checkIp==from) {
						deviceAdapter.clear();
					}
					if (f.isPortOpen(maskIp + checkIp, 80)) {
						deviceAdapter.add(new DeviceItem(Integer.toString(checkIp)));
						//devices.add(new DeviceItem(Integer.toString(checkIp)));
						Log.e("MAKI::FINDER", "Okay. We added the board to listview " + checkIp);
					}
					Message msg = mHandler.obtainMessage();
					msg.arg1 = total = Math.round((checkIp - from) * 100
							/ (to - from));// (int)100 * ((i - from) /
											// (to-from));
					Log.e("MAKI::FINDER", "RUNNING THREAD " + msg.arg1);
					mHandler.sendMessage(msg);
					checkIp++;
				} catch (Exception e) {
					Log.e("ERROR", "Thread Interrupted");
				}
			}

			// }

		}

		/*
		 * sets the current state for the thread, used to stop the thread
		 */
		public void setState(int state) {
			mState = state;
			if (state == STATE_DONE ) {
				deviceAdapter.notifyDataSetChanged();
			}
		}
	}

}
