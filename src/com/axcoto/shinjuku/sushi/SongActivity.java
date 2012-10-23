package com.axcoto.shinjuku.sushi;

import java.util.Random;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

import com.axcoto.shinjuku.database.Db;
import com.axcoto.shinjuku.database.XMLParser;
import com.axcoto.shinjuku.maki.Finder;
import com.axcoto.shinjuku.maki.Remote;

public class SongActivity extends RootActivity {
	Db db;
	public ProgressDialog progressDialog;
	public ProgressThread progressThread;

	static final int PROGRESS_DIALOG = 0;

	/**
	 * Status constant when syncing song book
	 * 
	 */
	public static int syncStatus = 0;
	public static final int SYNC_TOTAL_PHASE = 4;
	public static final int SYNC_WAIT_UPLOAD = 0;
	public static final int SYNC_RECEIVE_SONGBOOK = 1;
	public static final int SYNC_PROCESS_SONGBOOK = 2;
	public static final int SYNC_INITIALIZE_DB = 3;
	public static final int SYNC_DRAW_UI = 4;
	public static final int SYNC_DONE = 5;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song);
		this.initDb();
		// this.runTest();
	}

	public void initDb() {
		db = Db.getInstance(this);
		db.open();
	}

	public void runTest() {
		SQLiteDatabase conn = db.getDatabase();
		ContentValues v = new ContentValues();
		Random r = new Random();
		v.put("id", r.nextInt());
		v.put("title", "test");
		v.put("id", 2);

		v.put("title", "dada ");
		v.put("id", 4);
		v.put("title", "fdisf ds");
		conn.insert("HD", null, v);
	}

	public void dump() {
		try {
			Log.i("SUSHI", "Location of db is" + this.getFilesDir().toString());
			XMLParser parser = new XMLParser("HD", db, this.getFilesDir()
					.toString());
		} catch (Exception e) {
			Log.i("SUSHI", e.getMessage());
		}
		this.syncStatus = this.SYNC_DONE;
	}

	public void onSync(View v) {
		ToggleButton t = (ToggleButton) this.findViewById(R.id.karaoke_switch);
		showDialog(PROGRESS_DIALOG);
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(SongActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Waiting for song book...");
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
			progressThread.start();
		}
	}

	private Handler handler = new Handler() {
		
		/**
		 * @param Message msg: arg1 is the current Status of process
		 */
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
				case SongActivity.SYNC_RECEIVE_SONGBOOK:
					progressDialog.setMessage("Received song book");					
					break;
				case SongActivity.SYNC_PROCESS_SONGBOOK:
					progressDialog.setMessage("Process song book");
					break;
				case SongActivity.SYNC_DRAW_UI:
					break;
			}
			if (SongActivity.syncStatus == SongActivity.SYNC_DONE) {
				progressDialog.setProgress(0); // clear the value to avoid cache for next appearance.												
				dismissDialog(PROGRESS_DIALOG);
				progressThread.setState(ProgressThread.STATE_DONE);
			}
		}
	};

	/** Nested class that performs progress syncing song book */
	private class ProgressThread extends Thread {
		Handler mHandler;
		final static int STATE_DONE = 0;
		final static int STATE_RUNNING = 1;
		int mState;

		ProgressThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			mState = STATE_RUNNING;
			while (mState == STATE_RUNNING) {
				try {
					Message msg = mHandler.obtainMessage();
					msg.arg1 = SongActivity.syncStatus;
					if (SongActivity.syncStatus == SongActivity.SYNC_PROCESS_SONGBOOK) {
						dump();
					}
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					Log.e("ERROR", "Thread Interrupted");
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

	/**
	 * Handler syncing flag. Instead of setting value of syncStatus directly, we
	 * pass message to this handle to set flag.
	 */
	private Handler syncHandler = new Handler() {
		public void handleMessage(Message msg) {
			SongActivity.syncStatus = msg.arg1;

		}
	};

}
