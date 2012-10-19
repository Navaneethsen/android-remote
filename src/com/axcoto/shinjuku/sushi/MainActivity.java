package com.axcoto.shinjuku.sushi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.axcoto.shinjuku.maki.MyHttpServer;
import com.axcoto.shinjuku.maki.Remote;

public class MainActivity extends RootActivity implements OnGestureListener {
	final static int PHASE_DEVELOPMENT = 1;
	final static int PHASE_TESTING = 2;
	final static int PHASE_PRODUCTION = 3;
	
	final static int ENVIRONMENT = PHASE_DEVELOPMENT;
//	final static int ENVIRONMENT = PHASE_PRODUCTION;
	//final static int ENVIRONMENT = PHASE_TESTING;
		
	final static int VIRGIN = 1;
	final static String VERSION = "0.2-dev-1018";  
	public String remote;
	final int PORT = 5320;
	protected File homeDir;
	int count = 0;
	private GestureDetector gestureScanner;
	
	private long lastTouchedTime = 0;
	private long doubleTapDistance = 350000000;
	
	private String currentText = "";
	
	public File getHomeDir() {
		return homeDir;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Resources res = getResources();
//		setButtonHoldListener(findViewById(R.id.cmd_169));
//		setButtonHoldListener(findViewById(R.id.cmd_audio));
//		setButtonHoldListener(findViewById(R.id.cmd_blue));
//		setButtonHoldListener(findViewById(R.id.cmd_bookmark));
//		setButtonHoldListener(findViewById(R.id.cmd_delete));
//		setButtonHoldListener(findViewById(R.id.cmd_delete));
//		setButtonHoldListener(findViewById(R.id.cmd_eject));
//		setButtonHoldListener(findViewById(R.id.cmd_fast_backward));
//		setButtonHoldListener(findViewById(R.id.cmd_fast_forward));
//		setButtonHoldListener(findViewById(R.id.cmd_green));
//		setButtonHoldListener(findViewById(R.id.cmd_home));
//		setButtonHoldListener(findViewById(R.id.cmd_info));
//		setButtonHoldListener(findViewById(R.id.cmd_keyboard));
//		setButtonHoldListener(findViewById(R.id.cmd_menu));
//		setButtonHoldListener(findViewById(R.id.cmd_mute));
//		setButtonHoldListener(findViewById(R.id.cmd_play));
//		setButtonHoldListener(findViewById(R.id.cmd_power));
//		setButtonHoldListener(findViewById(R.id.cmd_red));
//		setButtonHoldListener(findViewById(R.id.cmd_repeat));
//		setButtonHoldListener(findViewById(R.id.cmd_return));
//		setButtonHoldListener(findViewById(R.id.cmd_rev));
//		setButtonHoldListener(findViewById(R.id.cmd_setup));
//		setButtonHoldListener(findViewById(R.id.cmd_slow));
//		setButtonHoldListener(findViewById(R.id.cmd_stop));
//		setButtonHoldListener(findViewById(R.id.cmd_subtittle));
//		setButtonHoldListener(findViewById(R.id.cmd_timeseek));
//		setButtonHoldListener(findViewById(R.id.cmd_tvmode));
//		setButtonHoldListener(findViewById(R.id.cmd_voldown));
//		setButtonHoldListener(findViewById(R.id.cmd_volup));
//		setButtonHoldListener(findViewById(R.id.cmd_yellow));
//		setButtonHoldListener(findViewById(R.id.cmd_zoom));
		setTextListener();
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

//	private void setButtonHoldListener(View v) {
//		    final RemoteKeyButton b = (RemoteKeyButton) v;
//			b.setOnLongClickListener(new OnLongClickListener() {
//				@Override
//				public boolean onLongClick(View v) {
//				Toast.makeText(getApplicationContext(), b.getKeyName(), Toast.LENGTH_SHORT).show();
//	            return true;
//				}
//			});
//		}

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
//				Log.e("SUSHI: REMOTE", "Key not found: " + command);
			}
		}
	}

	public void onRemoteClick(View v) {
//		setButtonHoldListener(v);
		String key = "";
		RemoteKeyButton b = (RemoteKeyButton) v;
		key = b.getKeyName();
		Toast.makeText(getApplicationContext(),b.getKeyName(), Toast.LENGTH_SHORT).show();
//		b.setOnLongClickListener(new OnLongClickListener() {
//			@Override
//			public boolean onLongClick(View v) {
//			Toast.makeText(getApplicationContext(),b.getKeyName(), Toast.LENGTH_SHORT).show();
//            return true;
//			}
//		});
		Log.e("SUSHI:: REMOTE", "PRESS " + key);
		this.execute(key);
	}

	@Override
	public boolean onTouchEvent(MotionEvent me)

	{
		gestureScanner.onTouchEvent(me);
		InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//		im.toggleSoftInput(1, 0);
		EditText edt = (EditText) findViewById(R.id.cmd_keyboard);
		im.hideSoftInputFromWindow(edt.getWindowToken(),0);

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
		
		float dX = e2.getX()-e1.getX();
		float dY = e1.getY()-e2.getY();
		if (Math.abs(dY)<SWIPE_MAX_OFF_PATH && 
			Math.abs(velocityX)>=SWIPE_THRESHOLD_VELOCITY &&
			Math.abs(dX)>=SWIPE_MIN_DISTANCE ) {
			if (dX>0) {
//				Toast.makeText(getApplicationContext(), "Right Swipe", Toast.LENGTH_SHORT).show();
				this.execute("right");
			} else {
//				Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
				this.execute("left");
			}
			return true;
		} 
		else if (Math.abs(dX)<SWIPE_MAX_OFF_PATH &&
				Math.abs(velocityY)>=SWIPE_THRESHOLD_VELOCITY &&
				Math.abs(dY)>=SWIPE_MIN_DISTANCE ) {
			if (dY>0) {
//				Toast.makeText(getApplicationContext(), "Up Swipe", Toast.LENGTH_SHORT).show();
				this.execute("up");
			} else {
//				Toast.makeText(getApplicationContext(), "Down Swipe", Toast.LENGTH_SHORT).show();
				this.execute("down");
			}
		return true;
		}
		return false;
	}


//		 if (velocityX>0) {
//			 this.execute("right");
//			 return true;
//			 }
//			
//			 if (velocityX<0) {
//			 this.execute("left");
//			 return true;
//			 }
//			 else return false;


	@Override
	public void onLongPress(MotionEvent e)

	{
		
		Log.e("SUSHI:: DEVICE", "-" + "LONG PRESS" + "-");

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY)

	{
//		 if (distanceX>0) {
//		 this.execute("right");
//		 return true;
//		 }
//		
//		 if (distanceX<0) {
//		 this.execute("left");
//		 return true;
//		 }
		
//		String direction = "";
//		if (distanceY < 0) {
//			direction = "down";
//			this.execute("down");
//			return true;
//		}
//		if (distanceY >0) {
//			direction = "up";
//			return true;
//		}
//		Log.e("SUSHI:: DEVICE", "-" + "SCROLL" + "-");

		return false;
//		return false;
	}

	@Override
	public void onShowPress(MotionEvent e)

	{

//		Log.e("SUSHI:: DEVICE", "-" + "SHOW PRESS" + "-");

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		long t = System.nanoTime();
		Log.e("SUSHI:: MAIN", "Touched at: " + t);		
		if (this.lastTouchedTime>1000 && (t-this.lastTouchedTime)<this.doubleTapDistance) {
			Log.e("SUSHI:: MAIN", "Double tap at: " + t);		
			this.execute("enter");				
		}
		this.lastTouchedTime = t;
		
		return true;
	}
	
	public void setTextListener() {
		EditText t = (EditText) findViewById(R.id.cmd_keyboard);
		final MainActivity xyz = this;
		t.addTextChangedListener(new TextWatcher() {              
			@Override
			public void  onTextChanged  (CharSequence s, int start, int before,
	        		int count) 	{ 
	        }
	    	@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override   
			public void afterTextChanged(Editable s) {
				String newText = s.toString();				
				if (newText.length()-currentText.length() == 1)  {
					String last = newText.substring(newText.length()-1);
					xyz.execute(last);
					Log.e("Keyboard: ", last);
//					count = 1;
				}
				if (newText.length()-currentText.length() == -1) {
					xyz.execute("delete");
					Log.e("Keyboard: ", "delete");
//					count = 0;
				}
//				if (newText.length() - currentText.length() == 0 && count == 0)  {
//					xyz.execute("delete");
//					String last = newText.substring(newText.length()-1);
//					xyz.execute(last);
//					Log.e("Keyboard: ", "delete");
//					Log.e("Keyboard: ", last);
//					count = 0;
//				}
				currentText = newText;
			}
		});
	}
}


