package com.axcoto.shinjuku.sushi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
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
import android.widget.Toast;
import android.widget.ToggleButton;

import com.axcoto.shinjuku.maki.MyHttpServer;
import com.axcoto.shinjuku.maki.Remote;

public class MainActivity extends RootActivity implements OnGestureListener {
	final static int PHASE_DEVELOPMENT = 1;
	final static int PHASE_TESTING = 2;
	final static int PHASE_PRODUCTION = 3;
	
//	final static int ENVIRONMENT = PHASE_DEVELOPMENT;
	final static int ENVIRONMENT = PHASE_PRODUCTION;
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
		//Adding extra stuff to test	
//		this.findViewById(R.id.ScrollView01).setVisibility(View.VISIBLE);
		//End of test
//		Resources res = getResources();
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
//			Log.e("Text: ", Boolean.toString(r.getConnection().getReuseAddress()));
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

//	public void toggle(View v)
//	{
//		ScrollView x = (ScrollView) findViewById(R.id.ScrollView01);
//		LinearLayout y = (LinearLayout) findViewById(R.id.LinearLayout01);
//		ToggleButton t = (ToggleButton) v;
//		if (t.isChecked()) {
//			setContentView(R.layout.activity_main2);
//			ToggleButton z = (ToggleButton) findViewById(R.id.toggle2);
//			z.setChecked(true);
//			}
//		else 
//		{
//			setContentView(R.layout.activity_main);			
//		}
//	}
	
	public void onRemoteClick(View v) {
//		setButtonHoldListener(v);
		String key = "";
		RemoteKeyButton b = (RemoteKeyButton) v;
		key = b.getKeyName();
		if(key.equals("playback") || key.equals("audio_control") || key.equals("settings") || key.equals("extra")) {
			findViewById(R.id.include_playback).setVisibility(View.GONE);
			findViewById(R.id.include_nothing).setVisibility(View.GONE);
			findViewById(R.id.include_settings).setVisibility(View.GONE);
			findViewById(R.id.include_audio_control).setVisibility(View.GONE);
			findViewById(R.id.include_extra).setVisibility(View.GONE);
		}
		if(key.equals("playback")) {
			Log.i("SUSHI:: REMOTE", "PRESS " + key + " icon");
			View x =findViewById(R.id.include_playback);
			if (x.isShown()== false) {
				x.setVisibility(View.VISIBLE);
			}
		}
		else if (key.equals("audio_control")) {		
			Log.i("SUSHI:: REMOTE", "PRESS " + key + " icon");
			View x =findViewById(R.id.include_audio_control);
			if (x.isShown()== false) {
				x.setVisibility(View.VISIBLE);
			}
		}
		else if (key.equals("settings")) {		
			Log.i("SUSHI:: REMOTE", "PRESS " + key + " icon");
			View x =findViewById(R.id.include_settings);
			if (x.isShown()== false) {
				x.setVisibility(View.VISIBLE);
			}
		}
		else if (key.equals("extra")) {		
			Log.i("SUSHI:: REMOTE", "PRESS " + key + " icon");
			View x =findViewById(R.id.include_extra);
			if (x.isShown()== false) {
				x.setVisibility(View.VISIBLE);
			}
		}		
		else
		{		
//		Toast toast = Toast.makeText(getApplicationContext(),b.getKeyName(), Toast.LENGTH_SHORT);
//		toast.setGravity(Gravity.TOP|Gravity.LEFT, 400,700);
//		toast.show();
//		new CountDownLatch(1).countDown();
		Log.i("SUSHI:: REMOTE", "PRESS " + key);		
		this.execute(key);		
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent me)

	{
		gestureScanner.onTouchEvent(me);		
				
//		if (im.isAcceptingText()) 
//			this.findViewById(R.id.ScrollView01).setVisibility(View.VISIBLE);
//		else
//			this.findViewById(R.id.ScrollView01).setVisibility(View.GONE);
//		EditText edt = (EditText) findViewById(R.id.cmd_keyboard);
//		im.hideSoftInputFromWindow(edt.getWindowToken(),0);

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
//			float dX = e2.getX()-e1.getX();
//			float dY = e1.getY()-e2.getY();
//			if (Math.abs(dY)<SWIPE_MAX_OFF_PATH && 
//				Math.abs(velocityX)>=SWIPE_THRESHOLD_VELOCITY &&
//				Math.abs(dX)>=SWIPE_MIN_DISTANCE ) {
//				if (dX>0) {
//	//				Toast.makeText(getApplicationContext(), "Right Swipe", Toast.LENGTH_SHORT).show();
//					this.execute("right");
//				} else {
//	//				Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
//					this.execute("left");
//				}
//				return true;
//			} 
//			else if (Math.abs(dX)<SWIPE_MAX_OFF_PATH &&
//					Math.abs(velocityY)>=SWIPE_THRESHOLD_VELOCITY &&
//					Math.abs(dY)>=SWIPE_MIN_DISTANCE ) {
//				if (dY>0) {
//	//				Toast.makeText(getApplicationContext(), "Up Swipe", Toast.LENGTH_SHORT).show();
//					this.execute("up");
//				} else {
//	//				Toast.makeText(getApplicationContext(), "Down Swipe", Toast.LENGTH_SHORT).show();
//					this.execute("down");
//				}
//			return true;
//			}
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
	
	public boolean inSameGroup(String a, String b)
	{
		Remote r = Remote.getInstance();
		String first = r.remoteKeyCode.get(a);
		String second = r.remoteKeyCode.get(b);
		String[] part1 = first.split(",");
		String[] part2 = second.split(",");
		if (part1[1].equals(part2[1])) return true;
		else return false;
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
			public void  onTextChanged  (CharSequence s, int start, int before,
	        		int count) 	{				
	        }
				
	    	@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
//	    		InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//				if (im.isAcceptingText()) {
//					Log.e("YEA", "TESTING");
//				}
			}
			@Override   
			public void afterTextChanged(Editable s) {
				String newText = s.toString();	
				if (newText.length()>= 1)
					last = newText.substring(newText.length()-1);
					else last = "";
				long tz = System.nanoTime();
				long zz = tz - this.lastKeyTouchedTime;
				Log.e("Time: ",Long.toString(zz));
				
				if (currentText.length() >= 1){
					old = currentText.substring(currentText.length()-1);
				}
				else old = "";
				
				if (newText.length()-currentText.length() == 1)  {
					if (zz<this.keyDistance && inSameGroup(old,last)==true){
					new CountDownTimer(500,1000) {
			            @Override
			            public void onTick(long arg0) {}
			            @Override
			            public void onFinish() {
			            	xyz.execute(last);  
			            }
			        }.start();			        
			        this.lastKeyTouchedTime = tz;
				}
					else 
						{
						this.lastKeyTouchedTime = tz;
						xyz.execute(last);}								
					Log.e("Keyboard: ", last);
				}
				if (newText.length()-currentText.length() == -1) {
						xyz.execute("delete");
						Log.e("Keyboard: ", "delete");				
				}
				else {}
				
            	currentText = newText;
				old = last;
			}			
		});
	}
	
//	public void setOnKeyListener(View v) {
//		EditText edt = (EditText) v;
//		edt.setOnKeyListener(new OnKeyListener() {
//
//			@Override
//			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//			
//		});
//	}

	public void onKeyboardTouch(View v) {
		findViewById(R.id.hide_button).setVisibility(View.VISIBLE);
	}
	
	public void onHideTouch(View v) {
		EditText edt = (EditText) findViewById(R.id.cmd_keyboard);
		InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		if (im.isAcceptingText()) im.hideSoftInputFromWindow(edt.getWindowToken(),0);
		v.setVisibility(View.GONE);
		
	}
}


