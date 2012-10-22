package com.axcoto.shinjuku.sushi;

import java.util.Random;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

import com.axcoto.shinjuku.database.Db;
import com.axcoto.shinjuku.database.XMLParser;

public class SongActivity extends RootActivity {
	Db db;
	public ProgressDialog progressDialog;
	//public ProgressThread progressThread;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        this.initDb(); 
        //this.runTest();
    }
    
    public void initDb() {
    	db = Db.getInstance(this);
    	db.open();
    }

    public void dump(String name) {
    	SQLiteDatabase s = db.getDatabase();
    	String location = "";
    	s.execSQL("DELETE FROM hd");
    	s.execSQL("DELETE FROM mp3");
    	if (name.equals("hd")){
    		location = this.getFilesDir().toString()+"/TestDB.xml";
    	}
    	else if (name.equals("mp3")) {
    		location = this.getFilesDir().toString()+"/MP3KaraokeDB.xml";
    	}    		
    	Log.i("SUSHI", "Location of db is" + location);
   		XMLParser parser = new XMLParser(name, db, location);
    }
    
    public void onSync(View v) {
    	ToggleButton t = (ToggleButton) this.findViewById(R.id.karaoke_switch);
    	if(t.isChecked()) dump("hd");
    	else dump("mp3");
    	
    }
//    
//    protected Dialog onCreateDialog(int id) {
//		switch (id) {
//		case PROGRESS_DIALOG:
//			progressDialog = new ProgressDialog(SongActivity.this);
//			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//			progressDialog.setMessage("Scanning Device...");
//			return progressDialog;
//		default:
//			return null;
//		}
//	}
//
//	@Override
//	protected void onPrepareDialog(int id, Dialog dialog) {
//		switch (id) {
//		case PROGRESS_DIALOG:
//			progressDialog.setProgress(0);
//			progressThread = new ProgressThread(handler);
//			deviceAdapter.clear();
//			deviceIp.clear();
//			progressThread.start();
//		}
//	}
//
//	private Handler handler = new Handler() {
//		public void handleMessage(Message msg) {
//
//			int total = msg.arg1;
//			progressDialog.setProgress(total);
//			if (msg.arg2 > 0) {
//				
//				String ip = DeviceActivity.ipMaskAdd + Integer.toString(msg.arg2);
//				deviceAdapter.add(new DeviceItem(ip));
//				deviceIp.add(ip);
//			}
//			if (total >= 100) {
//				progressDialog.setProgress(0); // clear the value to avoid cache
//												// for next appearance.
//				dismissDialog(PROGRESS_DIALOG);
//				progressThread.setState(ProgressThread.STATE_DONE);
//			}
//		}
//	};
//
//	/** Nested class that performs progress calculations (counting) */
//	private class ProgressThread extends Thread {
//		Handler mHandler;
//		final static int STATE_DONE = 0;
//		final static int STATE_RUNNING = 1;
//		int mState;
//
//		int from = 2, to = 253, checkIp = from;
//		
//		String maskIp;
//
//		ProgressThread(Handler h) {
//			mHandler = h;
//			if (MainActivity.ENVIRONMENT==MainActivity.PHASE_DEVELOPMENT) {
//				from = 147;
//				to = 150;
//				checkIp = from;
//			} 
//			from = DeviceActivity.ipScanFrom;
//			to = DeviceActivity.ipScanTo;
//			checkIp = from;
//		}
//
//		public void run() {
//
//			Finder f = Finder.getInstance();
//			f.resolve();
//			mState = STATE_RUNNING;
//
//			if (MainActivity.ENVIRONMENT==MainActivity.PHASE_DEVELOPMENT) {
//				maskIp = "192.168.0.";
//			} else {
//				maskIp = f.getMaskIpAddress() + ".";
//			}
//			DeviceActivity.ipMaskAdd = maskIp;
//			
//			while (mState == STATE_RUNNING) {
//
//				try {
//					Message msg = mHandler.obtainMessage();
//					msg.arg2 = 0;
//					if (f.isPortOpen(maskIp + checkIp, Remote.TCP_PORT, DeviceActivity.ipTimeoutPing)) {
//						msg.arg2 = checkIp;
//						Log.e("MAKI::FINDER",
//								"Okay. We added the board to listview "
//										+ checkIp);
//					}
//
//					msg.arg1 = Math.round((checkIp - from) * 100 / (to - from));
//					Log.e("MAKI::FINDER", "RUNNING THREAD " + msg.arg1);
//					mHandler.sendMessage(msg);
//					checkIp++;
//				} catch (Exception e) {
//					Log.e("ERROR", "Thread Interrupted");
//				}
//			}
//
//			// }
//
//		}
//
//		/*
//		 * sets the current state for the thread, used to stop the thread
//		 */
//		public void setState(int state) {
//			mState = state;
//			if (state == STATE_DONE) {
//				// deviceAdapter.notifyDataSetChanged();
//			}
//		}
//	}

}
