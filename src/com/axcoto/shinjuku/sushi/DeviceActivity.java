package com.axcoto.shinjuku.sushi;

import java.io.IOException;
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
import com.axcoto.shinjuku.maki.Remote;

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
				DeviceItem d = deviceAdapter.getItem(position);
				Log.e("SUSHI::DEVICE", "About to connect to " + d.getIp());
				try {
					Remote r = d.connect();					
					Toast.makeText(getApplicationContext(),
						"Click ListItem Number " + position, Toast.LENGTH_LONG)
						.show();
					r.execute("power up");
				} catch (IOException e) {
					
				} catch (Exception e) {
					
				}
			}
			
		});

		devices = new ArrayList<DeviceItem>();
		deviceAdapter = new ItemAdapter(this, R.layout.device_item, devices);
		deviceAdapter.notifyDataSetChanged();
		listDevice.setAdapter(deviceAdapter);
	}
	
	public void doTest(View view) {
		Remote r = Remote.getInstance();
		if (r.getConnected()) {
			try {
				r.execute("DOWN");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void scanDevice(View view) {
		//devices = new ArrayList<DeviceItem>();
		//deviceAdapter.clear();
//		deviceAdapter.add(new DeviceItem("101"));
//		deviceAdapter.add(new DeviceItem("102"));
//		deviceAdapter.notifyDataSetChanged();
		showDialog(PROGRESS_DIALOG);
	}

	public void refresh() {
		deviceAdapter = new ItemAdapter(this, R.layout.device_item, devices);
		deviceAdapter.notifyDataSetChanged();
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
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog.setProgress(0);
			progressThread = new ProgressThread(handler);
			deviceAdapter.clear();
			progressThread.start();
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			int total = msg.arg1;
			progressDialog.setProgress(total);
			if (msg.arg2>0) {
				String ip = "192.168.0." + Integer.toString(msg.arg2);
				deviceAdapter.add(new DeviceItem(ip));				
			}
			if (total >= 100) {
				progressDialog.setProgress(0); //clear the value to avoid cache for next appearance.
				dismissDialog(PROGRESS_DIALOG);
				progressThread.setState(ProgressThread.STATE_DONE);				
			}
		}
	};

	/** Nested class that performs progress calculations (counting) */
	private class ProgressThread extends Thread {
		Handler mHandler;
		final static int STATE_DONE = 0;
		final static int STATE_RUNNING = 1;
		int mState;
		
		int from = 148, to = 154, checkIp = from;
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
					Message msg = mHandler.obtainMessage();
					msg.arg2 = 0;
					if (f.isPortOpen(maskIp + checkIp, Remote.TCP_PORT)) {
						msg.arg2 = checkIp;
						Log.e("MAKI::FINDER", "Okay. We added the board to listview " + checkIp);
					}
					
					msg.arg1 = Math.round((checkIp - from) * 100
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
				//deviceAdapter.notifyDataSetChanged();
			}
		}
	}

}
