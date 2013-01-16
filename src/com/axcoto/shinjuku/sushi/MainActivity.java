package com.axcoto.shinjuku.sushi;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gcm.GCMRegistrar;

import com.axcoto.shinjuku.maki.MyHttpServer;
import com.axcoto.shinjuku.maki.Remote;

import static com.axcoto.shinjuku.sushi.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.axcoto.shinjuku.sushi.CommonUtilities.EXTRA_MESSAGE;
import static com.axcoto.shinjuku.sushi.CommonUtilities.SENDER_ID;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.Intent;
import android.content.IntentFilter;

public class MainActivity extends RootActivity implements OnGestureListener {
	final static int PHASE_DEVELOPMENT = 1;
	final static int PHASE_TESTING = 2;
	final static int PHASE_PRODUCTION = 3;

//	final static int ENVIRONMENT = PHASE_DEVELOPMENT;
	 final static int ENVIRONMENT = PHASE_PRODUCTION;
	// final static int ENVIRONMENT = PHASE_TESTING;

	public String remote;
	final int PORT = 5320;
	protected File homeDir;
	int count = 0;
	private GestureDetector gestureScanner;

	private long lastTouchedTime = 0;
	private long doubleTapDistance = 350000000;

	// New 1
	AsyncTask<Void, Void, Void> mRegisterTask;
	TextView lblMessage;
	AlertDialogManager alert = new AlertDialogManager();
	ConnectionDetector cd;
	// New 1

	private String currentText = "";

	public File getHomeDir() {
		return homeDir;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTextListener();
		gestureScanner = new GestureDetector(this);

		final Remote r = Remote.getInstance();
		final EditText edt = (EditText) findViewById(R.id.cmd_keyboard);
		int AndroidVersion = android.os.Build.VERSION.SDK_INT;
		if (AndroidVersion < 16) {
			edt.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if ((event.getAction() == KeyEvent.ACTION_DOWN)
							&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
						InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						if (im.isAcceptingText()) {
							im.hideSoftInputFromWindow(edt.getWindowToken(), 0);
						}
						try {
							r.execute("enter");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return true;
					}
					return false;
				}
			});
		}
	}

	protected boolean isOnline() {
		cd = new ConnectionDetector(getApplicationContext());
		return cd.isConnectingToInternet();
	}

	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (this.isOnline()) {
			this.startServer();
			this.gcmHandle();
		} else {
			alert.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);

		}

	}

	/**
	 * We run a embedded web server on port 5320 for song book syncing. The
	 * docroot of web server is homedirectory of app
	 */
	private void startServer() {
		try {
			homeDir = this.getFilesDir();
			boolean mExternalStorageAvailable = false;
			boolean mExternalStorageWriteable = false;
			String state = Environment.getExternalStorageState();
			Log.i("SUSHI:: MAIN :: HomeDir is", homeDir.toString());
			// For simplicity. use internal storage for now
			// if (Environment.MEDIA_MOUNTED.equals(state)) {
			// // We can read and write the media
			// mExternalStorageAvailable = mExternalStorageWriteable = true;
			// homeDir = this.getExternalFilesDir(null);
			// };

			MyHttpServer ht = MyHttpServer.getInstance(PORT, homeDir);
			File file = this.getFileStreamPath("file-upload.html");
			if (file.exists()) {
				Log.i("MAKI: SERVER",
						"initialize app before so we don't need to copy the file for web server");
			} else {
				this.copyAssets();
			}
		} catch (IOException e) {
			Log.e("MAKI:: SERVER:: IOError", "The docroot is not valid");
		} catch (Exception e) {
			Log.e("MAKI:: SERVER:: GENERAL ERROR", e.getMessage());
		}

	}

	/**
	 * gcmHandling
	 */
	private void gcmHandle() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);

		final String regId = GCMRegistrar.getRegistrationId(this);
		Log.i("Device ID: ", regId);

		// GCMRegistrar.unregister(getApplicationContext());

		File f = new File(this.getFilesDir(),
				com.axcoto.shinjuku.sushi.CommonUtilities.REGID_FILENAME);
		if (f.exists()) {
			// We don't need to register the device
			return;
		}
		// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				Log.i("GCM:", "Device is already registered with GCM");
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user
						ServerUtilities.register(context, regId);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
	}

	private void copyAssets() {
		Log.e("MAKI:: MAIN:: ASSET COPY",
				"Start to copy asset for the first initialization of app");
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("SUSHI:: MAINACTIVITY:: ERROR", e.getMessage());
		}

		for (String filename : files) {
			if ("images".equals(filename) || "sounds".equals(filename)
					|| "webkit".equals(filename)) {
				continue;
			}
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open(filename);
				out = openFileOutput(filename, Context.MODE_PRIVATE); // new
																		// FileOutputStream("/sdcard/"
																		// +
																		// filename);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (Exception e) {
				Log.e("MAKI:: MAIN ACITIVITY", "Cannot copy asset: " + filename
						+ ". Error: " + e.getMessage());
			}
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public void setRemote(String remote) {
		this.remote = remote;
	}

	public void execute(String command) {
		Remote r = Remote.getInstance();
		if (r.getConnected()) {
			try {
				r.execute(command);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("SUSHI: REMOTE", "yException: " + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				Log.e("SUSHI: REMOTE", "Exception: " + e.getMessage());
			}
		}

	}

	// public void toggle(View v)
	// {
	// ScrollView x = (ScrollView) findViewById(R.id.ScrollView01);
	// LinearLayout y = (LinearLayout) findViewById(R.id.LinearLayout01);
	// ToggleButton t = (ToggleButton) v;
	// if (t.isChecked()) {
	// setContentView(R.layout.activity_main2);
	// ToggleButton z = (ToggleButton) findViewById(R.id.toggle2);
	// z.setChecked(true);
	// }
	// else
	// {
	// setContentView(R.layout.activity_main);
	// }
	// }

	public void onRemoteClick(View v) {
		// setButtonHoldListener(v);
		String key = "";
		RemoteKeyButton b = (RemoteKeyButton) v;
		key = b.getKeyName();
		if (key.equals("playback") || key.equals("audio_control")
				|| key.equals("settings") || key.equals("extra")) {
			findViewById(R.id.include_playback).setVisibility(View.GONE);
			findViewById(R.id.include_nothing).setVisibility(View.GONE);
			findViewById(R.id.include_settings).setVisibility(View.GONE);
			findViewById(R.id.include_audio_control).setVisibility(View.GONE);
			findViewById(R.id.include_extra).setVisibility(View.GONE);
		}
		if (key.equals("playback")) {
			Log.i("SUSHI:: REMOTE", "PRESS " + key + " icon");
			View x = findViewById(R.id.include_playback);
			if (x.isShown() == false) {
				x.setVisibility(View.VISIBLE);
			}
		} else if (key.equals("audio_control")) {
			Log.i("SUSHI:: REMOTE", "PRESS " + key + " icon");
			View x = findViewById(R.id.include_audio_control);
			if (x.isShown() == false) {
				x.setVisibility(View.VISIBLE);
			}
		} else if (key.equals("settings")) {
			Log.i("SUSHI:: REMOTE", "PRESS " + key + " icon");
			View x = findViewById(R.id.include_settings);
			if (x.isShown() == false) {
				x.setVisibility(View.VISIBLE);
			}
		} else if (key.equals("extra")) {
			Log.i("SUSHI:: REMOTE", "PRESS " + key + " icon");
			View x = findViewById(R.id.include_extra);
			if (x.isShown() == false) {
				x.setVisibility(View.VISIBLE);
			}
		} else if (key.equals("power")) {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Are you sure to turn off the player?")
					.setMessage(
							"You will be no longer able to turn on with this app. You must use an physical remote to turn on it")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Log.i("SUSHI:: REMOTE",
											"THE KEY THAT IS PRESSED ON UI IS: power");
									Remote r = Remote.getInstance();
									try {
										r.execute("power");
									} catch (Exception e) {
										Log.e("SUSHI:: MAIN:: ", e.getMessage());
									}
									r.disConnect();
								}

							}).setNegativeButton("No", null).show();

		} else {
			// Toast toast =
			// Toast.makeText(getApplicationContext(),b.getKeyName(),
			// Toast.LENGTH_SHORT);
			// toast.setGravity(Gravity.TOP|Gravity.LEFT, 400,700);
			// toast.show();
			// new CountDownLatch(1).countDown();
			Log.i("SUSHI:: REMOTE", "THE KEY THAT IS PRESSED ON UI IS: " + key);
			this.execute(key);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent me)

	{
		gestureScanner.onTouchEvent(me);

		// if (im.isAcceptingText())
		// this.findViewById(R.id.ScrollView01).setVisibility(View.VISIBLE);
		// else
		// this.findViewById(R.id.ScrollView01).setVisibility(View.GONE);
		// EditText edt = (EditText) findViewById(R.id.cmd_keyboard);
		// im.hideSoftInputFromWindow(edt.getWindowToken(),0);

		return true;
	}

	@Override
	public boolean onDown(MotionEvent e)

	{

		Log.e("SUSHI:: DEVICE", "-" + "DOWN" + "-");

		return true;

	}

	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_MAX_OFF_PATH = 150;
	private static final int SWIPE_THRESHOLD_VELOCITY = 50;

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// float dX = e2.getX()-e1.getX();
		// float dY = e1.getY()-e2.getY();
		// if (Math.abs(dY)<SWIPE_MAX_OFF_PATH &&
		// Math.abs(velocityX)>=SWIPE_THRESHOLD_VELOCITY &&
		// Math.abs(dX)>=SWIPE_MIN_DISTANCE ) {
		// if (dX>0) {
		// // Toast.makeText(getApplicationContext(), "Right Swipe",
		// Toast.LENGTH_SHORT).show();
		// this.execute("right");
		// } else {
		// // Toast.makeText(getApplicationContext(), "Left Swipe",
		// Toast.LENGTH_SHORT).show();
		// this.execute("left");
		// }
		// return true;
		// }
		// else if (Math.abs(dX)<SWIPE_MAX_OFF_PATH &&
		// Math.abs(velocityY)>=SWIPE_THRESHOLD_VELOCITY &&
		// Math.abs(dY)>=SWIPE_MIN_DISTANCE ) {
		// if (dY>0) {
		// // Toast.makeText(getApplicationContext(), "Up Swipe",
		// Toast.LENGTH_SHORT).show();
		// this.execute("up");
		// } else {
		// // Toast.makeText(getApplicationContext(), "Down Swipe",
		// Toast.LENGTH_SHORT).show();
		// this.execute("down");
		// }
		// return true;
		// }
		return false;
	}

	// if (velocityX>0) {
	// this.execute("right");
	// return true;
	// }
	//
	// if (velocityX<0) {
	// this.execute("left");
	// return true;
	// }
	// else return false;

	@Override
	public void onLongPress(MotionEvent e)

	{

		Log.e("SUSHI:: DEVICE", "-" + "LONG PRESS" + "-");

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY)

	{
		// if (distanceX>0) {
		// this.execute("right");
		// return true;
		// }
		//
		// if (distanceX<0) {
		// this.execute("left");
		// return true;
		// }

		// String direction = "";
		// if (distanceY < 0) {
		// direction = "down";
		// this.execute("down");
		// return true;
		// }
		// if (distanceY >0) {
		// direction = "up";
		// return true;
		// }
		// Log.e("SUSHI:: DEVICE", "-" + "SCROLL" + "-");

		return false;
		// return false;
	}

	@Override
	public void onShowPress(MotionEvent e)

	{

		// Log.e("SUSHI:: DEVICE", "-" + "SHOW PRESS" + "-");

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		long t = System.nanoTime();
		Log.e("SUSHI:: MAIN", "Touched at: " + t);
		if (this.lastTouchedTime > 1000
				&& (t - this.lastTouchedTime) < this.doubleTapDistance) {
			Log.e("SUSHI:: MAIN", "Double tap at: " + t);
			this.execute("enter");
		}
		this.lastTouchedTime = t;

		return true;
	}

	public boolean inSameGroup(String a, String b) {
		Remote r = Remote.getInstance();
		String first = r.remoteKeyCode.get(a);
		String second = r.remoteKeyCode.get(b);
		String[] part1 = first.split(",");
		String[] part2 = second.split(",");
		if (part1[1].equals(part2[1]))
			return true;
		else
			return false;
	}

	public void setTextListener() {
		EditText t = (EditText) findViewById(R.id.cmd_keyboard);
		final MainActivity xyz = this;
		t.addTextChangedListener(new TextWatcher() {
			private long lastKeyTouchedTime = 0;
			private int keyDistance = 850000000;
			private String old = "";
			private String last = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// InputMethodManager im = (InputMethodManager)
				// getSystemService(INPUT_METHOD_SERVICE);
				// if (im.isAcceptingText()) {
				// Log.e("YEA", "TESTING");
				// }
			}

			@Override
			public void afterTextChanged(Editable s) {
				String newText = s.toString();
				if (newText.length() >= 1)
					last = newText.substring(newText.length() - 1);
				else
					last = "";
				long tz = System.nanoTime();
				long zz = tz - this.lastKeyTouchedTime;
				Log.e("Time: ", Long.toString(zz));

				if (currentText.length() >= 1) {
					old = currentText.substring(currentText.length() - 1);
				} else
					old = "";

				if (newText.length() - currentText.length() == 1) {
					if (zz < this.keyDistance && inSameGroup(old, last) == true) {
						new CountDownTimer(500, 1000) {
							@Override
							public void onTick(long arg0) {
							}

							@Override
							public void onFinish() {
								xyz.execute(last);
							}
						}.start();
						this.lastKeyTouchedTime = tz;
					} else {
						this.lastKeyTouchedTime = tz;
						xyz.execute(last);
					}
					Log.e("Keyboard: ", last);
				}
				if (newText.length() - currentText.length() == -1) {
					xyz.execute("delete");
					Log.e("Keyboard: ", "delete");
				} else {
				}

				currentText = newText;
				old = last;
			}
		});
	}

	// public void setOnKeyListener(View v) {
	// EditText edt = (EditText) v;
	// edt.setOnKeyListener(new OnKeyListener() {
	//
	// @Override
	// public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// });
	// }

	public void onKeyboardTouch(View v) {
		EditText edt = (EditText) findViewById(R.id.cmd_keyboard);
		InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(edt.getWindowToken(), 1);
	}

	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			Log.e("MESSAGE: ", newMessage);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message depending upon your app
			 * requirement For now i am just displaying it on the screen
			 * */

			// Showing received message
			lblMessage.append(newMessage + "\n");
			Toast.makeText(getApplicationContext(),
					"New Message: " + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

	// }
}