package com.axcoto.shinjuku.sushi;

//import java.util.Random;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
//import android.content.ContentValues;
//import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;

import com.axcoto.shinjuku.database.Song;
//import com.axcoto.shinjuku.database.Db;
import com.axcoto.shinjuku.database.XMLParser;
import com.axcoto.shinjuku.maki.Remote;

public class SongActivity extends RootActivity{
	private ListView songList;
	private SongAdapter songAdapter;
	public ArrayList<Song> songs = new ArrayList<Song>();
	public ArrayList<String> songId;
	public ArrayList<String> songTitle;
	
	
	public ProgressDialog progressDialog;
	//public ProgressThread progressThread;
	
	public void getSong(String location)
	{
        songs = new XMLParser(location).get(); 
	}	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        final Remote r = Remote.getInstance();
        getSong(this.getFilesDir().toString()+"/Mp3KaraokeDB.xml");       
//		We need to keep this on during device scanning--REMOVED FOR CRASH TEST
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		songList = (ListView) findViewById(R.id.song_list);
		
		Log.e("SUSHI:: DEVICE", "Create activity");
		final SongActivity t = this;		
		
		songList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Log.e("DEVICE: CLICKED", "Click ListItem Number " + position);
				Song s = songAdapter.getItem(position);
//				Log.e("SUSHI::SONG", "About to open " + s.getTitle());
				String songid = s.getId();
				for (int i = 0; i < songid.length()-1;i++)
				{
					try {
						r.execute(songid.substring(0+i,1+i));
					} catch (IOException e) {
						Log.e("IOException: ","I0Exception");
						e.printStackTrace();
					} catch (Exception e) {
						Log.e("Exception: ","Exception");
						e.printStackTrace();
					}
				}				
			}
		});

		songAdapter = new SongAdapter(this, R.layout.song_item, songs);
		songAdapter.notifyDataSetChanged();
		songList.setAdapter(songAdapter);
    }
      
    public void dump(String name) {
//    	SQLiteDatabase s = db.getDatabase();
    	songs = new ArrayList<Song>();
    	String location = "";
//    	s.execSQL("DELETE FROM hd");
//    	s.execSQL("DELETE FROM mp3");
    	if (name.equals("hd")){
    		location = this.getFilesDir().toString()+"/KaraokeDB.xml";
    	}
    	else if (name.equals("mp3")) {
    		location = this.getFilesDir().toString()+"/MP3KaraokeDB.xml";
    	}    		
//    	Log.i("SUSHI", "Location of db is" + location);
//   		XMLParser parser = new XMLParser(name, db, location);
    	songs = new XMLParser(location).get();    	
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
