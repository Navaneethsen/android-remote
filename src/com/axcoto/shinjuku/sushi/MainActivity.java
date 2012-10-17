package com.axcoto.shinjuku.sushi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.axcoto.shinjuku.maki.MyHttpServer;
import com.axcoto.shinjuku.maki.Remote;

public class MainActivity extends RootActivity implements OnGestureListener {
	public String remote;
	final int PORT = 5320;
	protected File homeDir;

	private GestureDetector gestureScanner;

	public File getHomeDir() {
		return homeDir;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Resources res = getResources();

		RemoteKeyButton b = (RemoteKeyButton) this.findViewById(R.id.cmd_power);
		Log.e("SUSHI:: KEYNAME", "NUT POWER UP IS ".concat(b.getKeyName()));

		gestureScanner = new GestureDetector(this);

		try {
			homeDir = this.getFilesDir();
			boolean mExternalStorageAvailable = false;
			boolean mExternalStorageWriteable = false;
			String state = Environment.getExternalStorageState();

			// For simplicity. use internal storage for now
//			if (Environment.MEDIA_MOUNTED.equals(state)) {
//				// We can read and write the media
//				mExternalStorageAvailable = mExternalStorageWriteable = true;
//				homeDir = this.getExternalFilesDir(null);
//			};

			MyHttpServer ht = MyHttpServer.getInstance(PORT, homeDir);
			File file = this.getFileStreamPath("f.html");
			if (file.exists()) {
				Log.e("MAKI: SERVER", "initialize app before");
			} else {
				this.copyAssets();
			}
		} catch (IOException e) {
			Log.e("MAKI:: SERVER", "The docroot is not valid");
		} catch (Exception e) {
			Log.e("MAKI:: SERVER", e.getMessage());
		}

	}

	private void copyAssets() {
		Log.e("MAKI: ASSET COPY",
				"Start to copy asset for the first initialization of app");
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
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
				e.printStackTrace();
			} catch (Exception e) {
				Log.e("SUSHI: REMOTE", "Key not found: " + command);
			}

		}
	}

	public void onRemoteClick(View v) {
		String key = "";
		RemoteKeyButton b = (RemoteKeyButton) v;
		key = b.getKeyName();
		Log.e("SUSHI:: REMOTE", "PRESS " + key);
		this.execute(key);
	}

	@Override
	public boolean onTouchEvent(MotionEvent me)

	{

		return gestureScanner.onTouchEvent(me);

	}

//	@Override
	public boolean onDown(MotionEvent e)

	{

		Log.e("SUSHI:: DEVICE", "-" + "DOWN" + "-");

		return true;

	}

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

//	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		try {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				return false;
			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				Log.e("SUSHI: FLING", "Left Swipe");
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				Log.e("SUSHI: FLING", "Right Swipe");
			}
		} catch (Exception e) {
			// nothing
		}
		return false;
	}

//	@Override
	public void onLongPress(MotionEvent e)

	{

		Log.e("SUSHI:: DEVICE", "-" + "LONG PRESS" + "-");

	}

//	@Override
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
		//
		String direction = "";
		if (distanceY > 0) {
			direction = "down";
			this.execute("down");
			return true;
		}
		if (distanceY < 00) {
			direction = "up";
			this.execute("up");
			return true;
		}
		Log.e("SUSHI:: DEVICE", "-" + "SCROLL" + "-");

		return false;

	}

//	@Override
	public void onShowPress(MotionEvent e)

	{

		Log.e("SUSHI:: DEVICE", "-" + "SHOW PRESS" + "-");

	}

//	@Override
	public boolean onSingleTapUp(MotionEvent e)

	{

		Log.e("SUSHI:: DEVICE", "-" + "SINGLE TAP UP" + "-");
		this.execute("ok");
		return true;

	}
	
	
	public void openkeyboard(View v) {
    	InputMethodManager inputMgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	inputMgr.toggleSoftInput(0, 0);
//    	Log.d("MAGIC: ", inputMgr.toString());
    }
}
