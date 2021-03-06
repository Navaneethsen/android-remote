package com.axcoto.shinjuku.sushi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.axcoto.shinjuku.maki.Finder;
import com.axcoto.shinjuku.maki.Remote;

public class DeviceActivity extends RootActivity implements OnGestureListener{
	final public String DEVICE_FILENAME = "device";
	
	static final int PROGRESS_DIALOG = 0;
	
	private ListView listDevice;
	private ItemAdapter deviceAdapter;
	public ArrayList<DeviceItem> devices;
	public ArrayList<String> deviceIp;
	public ProgressDialog progressDialog;
	public ProgressThread progressThread;

	
	private GestureDetector gestureScanner;
	
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  // Save UI state changes to the savedInstanceState.
	  // This bundle will be passed to onCreate if the process is
	  // killed and restarted.
		savedInstanceState.putStringArray("deviceIp", (String[])deviceIp.toArray());
		//savedInstanceState.put
		// etc.
		Log.e("SUSHI: SAVED", "Saved Device Ip");
	  super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  // Restore UI state from the savedInstanceState.
	  // This bundle has also been passed to onCreate.
	  String[] ip = savedInstanceState.getStringArray("deviceIp");
	  Log.e("SUSHI:: RESTORE", "From Restore. We had devices: " + ip.length);
	  
	  deviceIp = new ArrayList<String>();
		devices = new ArrayList<DeviceItem>();
		if (ip.length>0) {
			for (int i=0; i<ip.length; i++) {
				deviceIp.add(ip[i]);
				devices.add(new DeviceItem(ip[i]));
			}

			deviceAdapter = new ItemAdapter(this, R.layout.device_item, devices);
			deviceAdapter.notifyDataSetChanged();
			listDevice.setAdapter(deviceAdapter);
		}	  
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.e("SUSHI:: DEVICE", "Puase activity");
		
		String ip;
		try {
			FileOutputStream fos = openFileOutput(this.DEVICE_FILENAME, Context.MODE_PRIVATE);
			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(fos));
			for (int i=0; i < deviceIp.size(); i++) {
				ip = deviceIp.get(i) + "\n";				
				//fos.write(ip.getBytes());
				br.write(ip);
			}			
			br.flush();
			br.close();
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e("SUSHI:: DEVICE", "Cannot find the file ");
		} catch (IOException e) {
			Log.e("SUSHI:: DEVICE", "Cannot write data to the file ");			
		}
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		listDevice = (ListView) findViewById(R.id.list_device);
		
		Log.e("SUSHI:: DEVICE", "Create activity");
		
		listDevice.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.e("DEVICE: CLICKED", "Click ListItem Number " + position);
				DeviceItem d = deviceAdapter.getItem(position);
				Log.e("SUSHI::DEVICE", "About to connect to " + d.getIp());
				try {
					Remote r = d.connect();
					Toast.makeText(getApplicationContext(),
							"Click ListItem Number " + position,
							Toast.LENGTH_LONG).show();
					r.execute("power up");
				} catch (IOException e) {

				} catch (Exception e) {

				}
			}

		});
		
		deviceIp = new ArrayList<String>();
		devices = new ArrayList<DeviceItem>();		
//		if (savedInstanceState.containsKey("deviceIp")) {
//			
//			String[] ip = savedInstanceState.getStringArray("deviceIp");
//			Log.e("SUSHI:: RESTORE", " From create. We had devices: " + ip.length);
//			  
//			if (ip.length>0) {
//				for (int i=0; i<ip.length; i++) {
//					deviceIp.add(ip[i]);
//					devices.add(new DeviceItem(ip[i]));
//				}
//			}
//		}
		try {
			
			//FileInputStream fis = new FileInputStream(this.DEVICE_FILENAME);
			FileInputStream fis = openFileInput(this.DEVICE_FILENAME);
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String ip;
			while ((ip = br.readLine()) != null)   {
				  // Print the content on the console
				  deviceIp.add(ip);
				  devices.add(new DeviceItem(ip));
			}
			
			fis.close();
		} catch (FileNotFoundException e) {
			Log.e("SUSHI:: DEVICE", "Cannot find the file for reading data");
		} catch (IOException e) {
			Log.e("SUSHI:: DEVICE", "Cannot read file data ");			
		}
		deviceAdapter = new ItemAdapter(this, R.layout.device_item, devices);
		deviceAdapter.notifyDataSetChanged();
		listDevice.setAdapter(deviceAdapter);
		

		gestureScanner = new GestureDetector(this);
	}

	public void doTest(View view) {
		Remote r = Remote.getInstance();
		if (r.getConnected()) {
			try {
				r.execute("DOWN");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void scanDevice(View view) {
		// devices = new ArrayList<DeviceItem>();
		// deviceAdapter.clear();
		// deviceAdapter.add(new DeviceItem("101"));
		// deviceAdapter.add(new DeviceItem("102"));
		// deviceAdapter.notifyDataSetChanged();
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
			deviceIp.clear();
			progressThread.start();
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			int total = msg.arg1;
			progressDialog.setProgress(total);
			if (msg.arg2 > 0) {
				String ip = "192.168.0." + Integer.toString(msg.arg2);
				deviceAdapter.add(new DeviceItem(ip));
				deviceIp.add(ip);
			}
			if (total >= 100) {
				progressDialog.setProgress(0); // clear the value to avoid cache
												// for next appearance.
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

		int from = 147, to = 150, checkIp = from;
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
						Log.e("MAKI::FINDER",
								"Okay. We added the board to listview "
										+ checkIp);
					}

					msg.arg1 = Math.round((checkIp - from) * 100 / (to - from));// (int)100
																				// *
																				// ((i
																				// -
																				// from)
																				// /
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
			if (state == STATE_DONE) {
				// deviceAdapter.notifyDataSetChanged();
			}
		}
	}

	
	@Override
	 
    public boolean onTouchEvent(MotionEvent me)
 
    {
 
        return gestureScanner.onTouchEvent(me);
 
    }
 
   
 
    @Override
 
    public boolean onDown(MotionEvent e)
 
    {
 
        Log.e("SUSHI:: DEVICE", "-" + "DOWN" + "-");
 
        return true;
 
    }
 
   
 
    @Override
 
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
 
    {
 
        Log.e("SUSHI:: DEVICE", "-" + "FLING" + "-");
 
        return true;
 
    }
 
   
 
    @Override
 
    public void onLongPress(MotionEvent e)
 
    {
 
        Log.e("SUSHI:: DEVICE", "-" + "LONG PRESS" + "-");
 
    }
 
   
 
    @Override
 
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
 
    {
 
        Log.e("SUSHI:: DEVICE", "-" + "SCROLL" + "-");
 
        return true;
 
    }
 
   
 
    @Override
 
    public void onShowPress(MotionEvent e)
 
    {
 
        Log.e("SUSHI:: DEVICE", "-" + "SHOW PRESS" + "-");
 
    }    
 
   
 
    @Override  
 
    public boolean onSingleTapUp(MotionEvent e)    
 
    {
 
        Log.e("SUSHI:: DEVICE", "-" + "SINGLE TAP UP" + "-");
 
        return true;
 
    }
	
}
