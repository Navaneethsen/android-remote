package com.ceenee.q;

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

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ceenee.maki.DeviceItem;
import com.ceenee.maki.Finder;
import com.ceenee.maki.ItemAdapter;
import com.ceenee.maki.MyLog;
import com.ceenee.maki.Remote;
import com.ceenee.q.R;

public class DeviceActivity extends RootActivity implements OnGestureListener {
	final public String DEVICE_FILENAME = "device";

	final public int ACTION_CONNECT = 1;
	final public int ACTION_DISCONNECT = 2;

	static final int PROGRESS_DIALOG = 0;

	private ListView listDevice;
	private ItemAdapter deviceAdapter;
	public ArrayList<DeviceItem> devices;
	public ArrayList<String> deviceIp;
	public ProgressDialog progressDialog;
	public ProgressThread progressThread;

	static int ipScanFrom = 2;
	static int ipScanTo = 253;
	static int ipTimeoutPing = 400;
	static String ipMaskAdd = "";
	AsyncTask<Integer, Void, Void> mConnectTask;
	Remote r;
	//Used for scan QR code to get IP address
	static String qrcodestring = null;
	static Boolean isresumefromqrcodescanner=false;
	static String ipqrcodescan = null;
	private GestureDetector gestureScanner;

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putStringArrayList("deviceIp", deviceIp);
		// savedInstanceState.put
		// etc.
		Log.e("SUSHI: SAVED", "Saved Device Ip");
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		ArrayList<String> ip = savedInstanceState
				.getStringArrayList("deviceIp");
		Log.e("SUSHI:: ==RESTORE", "From Restore. We had devices: " + ip.size());

		deviceIp = new ArrayList<String>();
		devices = new ArrayList<DeviceItem>();
		if (ip.size() > 0) {
			for (int i = 0; i < ip.size(); i++) {
				deviceIp.add(ip.get(i));
				devices.add(new DeviceItem(ip.get(i)));
			}

			deviceAdapter = new ItemAdapter(this, R.layout.device_item, devices);
			deviceAdapter.notifyDataSetChanged();
			listDevice.setAdapter(deviceAdapter);
		}
	}
	


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("onResume", "onResume");
		//onResume called after scan QR code
		if (isresumefromqrcodescanner)
		{
			final Context context = this;
			Boolean isipduplicate = false;
			isresumefromqrcodescanner=false;
			for (int i =0; i < deviceIp.size();i++)
			{
				//IP address existed in listView
				if (ipqrcodescan.equalsIgnoreCase(deviceIp.get(i)))
				{
					isipduplicate = true;
					AlertDialog.Builder downloadDialog = new AlertDialog.Builder(context);
				    downloadDialog.setTitle("CeeNee player's ip address duplicate!");
				    downloadDialog.setMessage("Click on the ip address to connect (If it disconnect)");
				    downloadDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//Nothing for you
						}
					});
					downloadDialog.show();
				}	
			}
			//Ip address not exist in listView
			if (!isipduplicate)
			{
			deviceAdapter.add(new DeviceItem(ipqrcodescan));
			deviceIp.add(ipqrcodescan);
			saveDeviceList();
			final DeviceItem d = new DeviceItem(ipqrcodescan);
			try {
				final int action = d.isConnected() ? ACTION_DISCONNECT
						: ACTION_CONNECT;

				mConnectTask = new AsyncTask<Integer, Void, Void>() {
					ProgressDialog connectProgress;

					@Override
					protected void onPreExecute() {
						connectProgress = ProgressDialog
								.show(context,
										"Connect Status",
										"Trying to connect to CeeNee Media Player...",
										true);
					}

					@Override
					protected Void doInBackground(Integer... params) {

						try {
							// We need to close previous connection first.
							if (action == ACTION_DISCONNECT) {
								Remote.getInstance().disConnect();
							}
							r = d.connect();
						} catch (Exception e) {

							Log.e("SUSHI:: DEVICE:: CONNECT_ERROR", e
									.getStackTrace().toString());
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mConnectTask = null;
						if (d.isConnected()) {
							Toast.makeText(
									getApplicationContext(),
									"Click on the check mark to disconnect",
									Toast.LENGTH_SHORT).show();
							deviceAdapter.notifyDataSetChanged();
							listDevice.setAdapter(deviceAdapter);
							// Intent i = new Intent( t,
							// MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							// finish();
							// startActivityForResult(i, 0x13343);

						} else {
							Toast.makeText(getApplicationContext(),
									"Cannot connect to the device",
									Toast.LENGTH_LONG).show();
						}
						connectProgress.dismiss();
					}

				};
				mConnectTask.execute(action, null, null);
				// Intent i = new Intent( t,
				// MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				// finish();
				// startActivityForResult(i, 0x13343);
			} catch (Exception e) {
				Log.e("SUSHI:: DEVICE", e.getMessage());
			}
			Log.i("SUSHI :: DEVICE:: WAIT_CONNECT",
					"Waiting for device connection in background");
			} //of if (!isipduplicate)
		} //of if (isresumefromqrcodescanner)
	}

	/**Handle when user click button Scan to scan ip address using QRcode
	 * 
	 * @param v
	 */
	public void qrcodescan(View v) {
	    Intent intent = new Intent("com.google.zxing.client.android.SCAN");	    
	    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
	    startActivityForResult(intent, 0);
	} 
	/**Convert JSONString to get ip address string
	 * @param string got from scanner qrcode 
	 * @return ip address string
	 * @throws JSONException if JSON String true format, null if otherwise
	 */
	
	public String convertqrcode(String string) throws JSONException{
		String jsonstring = string;
		JSONObject jsonobjectdata;
		isresumefromqrcodescanner = true;
		try {
			jsonobjectdata= new JSONObject(jsonstring);
		} catch (JSONException e) {
			// TODO: handle exception
			Log.i("QRcode scan","JSON object error:"+ e.toString());
			return "0.0.0.0";
		}
		Log.i("QRcode scan",jsonobjectdata.getString("ip"));
		return jsonobjectdata.getString("ip");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == Activity.RESULT_OK) 
			{
	           if (qrcodestring.equalsIgnoreCase("0")) {
	               Toast.makeText(this, "QR code is error", Toast.LENGTH_LONG).show();
	           }
	           else {
	        	   Log.d("QRcode scan", "IP address "+qrcodestring);
	               try {
					ipqrcodescan= convertqrcode(qrcodestring);
					MyLog.i("ip scanned: ", ipqrcodescan);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	           }
	        } 
	        else if (resultCode == Activity.RESULT_CANCELED) {
	            Toast.makeText(this, "Problem with scan the QRcode\n  Scan progress was cancelled", 
	            		Toast.LENGTH_LONG).show();
	        }
	}

	public void saveDeviceList() {
		String ip;
		try {
			FileOutputStream fos = openFileOutput(this.DEVICE_FILENAME,
					Context.MODE_PRIVATE);
			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(fos));
			for (int i = 0; i < deviceIp.size(); i++) {
				ip = deviceIp.get(i) + "\n";
				// fos.write(ip.getBytes());
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

		// Okay, now we done we can skip it
		// getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// We need to keep this on during device scanning
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//keep screen oriention alway portrait after scan QRcode 
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.activity_device);
		listDevice = (ListView) findViewById(R.id.list_device);

		Log.e("SUSHI:: DEVICE", "Create activity");
		final DeviceActivity t = this;
		final Context context = this;
		// waitingConnectBar.setVisibility(View.VISIBLE);
		listDevice.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i("SUSHI :: DEVICE: CLICKED", "Click ListItem Number "
						+ position);
				final DeviceItem d = deviceAdapter.getItem(position);
				Log.i("SUSHI:: DEVICE", "About to connect to " + d.getIp());
				Log.i("SUSHI :: DEVICE", Integer.toString(view.getId()));

				try {
					final int action = d.isConnected() ? ACTION_DISCONNECT
							: ACTION_CONNECT;

					mConnectTask = new AsyncTask<Integer, Void, Void>() {
						ProgressDialog connectProgress;

						@Override
						protected void onPreExecute() {
							connectProgress = ProgressDialog
									.show(context,
											"Connect Status",
											"Trying to connect to CeeNee Media Player...",
											true);
						}

						@Override
						protected Void doInBackground(Integer... params) {

							try {
								// We need to close previous connection first.
								if (action == ACTION_DISCONNECT) {
									Remote.getInstance().disConnect();
								}
								r = d.connect();
							} catch (Exception e) {

								Log.e("SUSHI:: DEVICE:: CONNECT_ERROR", e
										.getStackTrace().toString());
							}
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							mConnectTask = null;
							if (d.isConnected()) {
								Toast.makeText(
										getApplicationContext(),
										"Click on the check mark to disconnect",
										Toast.LENGTH_SHORT).show();
								deviceAdapter.notifyDataSetChanged();
								listDevice.setAdapter(deviceAdapter);
								
								MainActivity.sipaddress_connected = d.getIp(); //save ip connected
								// Intent i = new Intent( t,
								// MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								// finish();
								// startActivityForResult(i, 0x13343);

							} else {
								Toast.makeText(getApplicationContext(),
										"Cannot connect to the device",
										Toast.LENGTH_LONG).show();
							}
							connectProgress.dismiss();
						}

					};
					mConnectTask.execute(action, null, null);
					// Intent i = new Intent( t,
					// MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					// finish();
					// startActivityForResult(i, 0x13343);
				} catch (Exception e) {
					Log.e("SUSHI:: DEVICE", e.getMessage());
				}
				Log.i("SUSHI :: DEVICE:: WAIT_CONNECT",
						"Waiting for device connection in background");

			}

		});

		deviceIp = new ArrayList<String>();
		devices = new ArrayList<DeviceItem>();

		try {
			FileInputStream fis = openFileInput(this.DEVICE_FILENAME);
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String ip;
			while ((ip = br.readLine()) != null) {
				// Print the content on the console
				deviceIp.add(ip);
				devices.add(new DeviceItem(ip));
			}
			fis.close();
		} catch (FileNotFoundException e) {
			Log.e("SUSHI:: DEVICE", "Device file has not existed yet.");
		} catch (IOException e) {
			Log.e("SUSHI:: DEVICE", "Cannot read device file");
		}
		deviceAdapter = new ItemAdapter(this, R.layout.device_item, devices);
		deviceAdapter.notifyDataSetChanged();
		listDevice.setAdapter(deviceAdapter);
		listDevice.invalidateViews();

		gestureScanner = new GestureDetector(this);

		Log.i("SUSHI:: ", "RUN TO HERE");
		try {
			Log.i("SUSHI:: CURRENT CONNECTED IP", Remote.getInstance().getIp());
		} catch (Exception e) {
			Log.i("Ignore", "IGNORE");
		}
	}

	public void scanDevice(View view) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		this.ipScanFrom = sharedPref.getInt("ip_from", 2);
		this.ipScanTo = sharedPref.getInt("ip_end", 253);
		this.ipTimeoutPing = sharedPref.getInt("ping_time", 400);

		Log.i("SUSHI: PREF", "IP FROM IS: " + this.ipScanFrom);
		Log.i("SUSHI: PREF", "IP FROM IS: " + this.ipScanTo);
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

				String ip = DeviceActivity.ipMaskAdd
						+ Integer.toString(msg.arg2);
				deviceAdapter.add(new DeviceItem(ip));
				deviceIp.add(ip);
			}
			if (total >= 100) {
				saveDeviceList();
				progressThread.setState(ProgressThread.STATE_DONE);
				removeDialog(PROGRESS_DIALOG);//instead of use dismissDialog(PROGRESS_DIALOG) 
				//to avoid app force close when scan ip address
			}
		}
	};

	/** Nested class that performs progress calculations (counting) */
	private class ProgressThread extends Thread {
		Handler mHandler;
		final static int STATE_DONE = 0;
		final static int STATE_RUNNING = 1;
		int mState;

		int from = 2, to = 253, checkIp = from;

		String maskIp;

		ProgressThread(Handler h) {
			mHandler = h;

			from = DeviceActivity.ipScanFrom;
			to = DeviceActivity.ipScanTo;
			checkIp = from;
		}

		public void run() {
			Finder f = Finder.getInstance();
			boolean res = f.resolve();
			progressDialog.setOnDismissListener(new 
					DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					//cancel thread scan when progress dialog dismiss (press back key)
					saveDeviceList();
					progressThread.setState(ProgressThread.STATE_DONE);
					removeDialog(PROGRESS_DIALOG);//instead of use dismissDialog(PROGRESS_DIALOG) 
					//to avoid the overhead of saving and restoring it in the future
				}
			});
			if (res == false) {
				Log.e("Error: ", "no internet connection");
				Message msg1 = mHandler.obtainMessage();
				msg1.arg2 = 0;
				msg1.arg1 = 120;
				mHandler.sendMessage(msg1);
			}
			// f.resolve();
			else {
				mState = STATE_RUNNING;
				
				maskIp = "192.168.0.";
				if (MainActivity.ENVIRONMENT == MainActivity.PHASE_PRODUCTION) {
					maskIp = f.getMaskIpAddress() + ".";
				}
				
				DeviceActivity.ipMaskAdd = maskIp;

				while (mState == STATE_RUNNING) {
					Log.i("MaskIp=", maskIp + checkIp);
					try {
						Message msg = mHandler.obtainMessage();
						msg.arg2 = 0;

						if (f.isPortOpen(maskIp + checkIp, Remote.TCP_PORT,
								DeviceActivity.ipTimeoutPing)) {
							msg.arg2 = checkIp;
							Log.e("MAKI::FINDER",
									"Okay. We added the board to listview "
											+ checkIp);
						}

						msg.arg1 = Math.round((checkIp - from) * 100
								/ (to - from));
						Log.e("MAKI::FINDER", "RUNNING THREAD " + msg.arg1);
						mHandler.sendMessage(msg);
						checkIp++;
					} catch (Exception e) {
						Log.e("ERROR", "Thread Interrupted");
					}
				}
			}

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
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY)

	{

		// Log.e("SUSHI:: DEVICE", "-" + "FLING" + "-");
		return true;

	}

	@Override
	public void onLongPress(MotionEvent e) {
		Log.e("SUSHI:: DEVICE", "-" + "LONG PRESS" + "-");
		Remote.getInstance().disConnect();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY)

	{

		// Log.e("SUSHI:: DEVICE", "-" + "SCROLL" + "-");
		//
		// return true;
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e)

	{

		// Log.e("SUSHI:: DEVICE", "-" + "SHOW PRESS" + "-");

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e)

	{

		// Log.e("SUSHI:: DEVICE", "-" + "SINGLE TAP UP" + "-");
		// return true;
		return false;
	}

}