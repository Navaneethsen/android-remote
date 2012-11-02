package com.axcoto.shinjuku.sushi;

//import java.util.Random;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
//import android.content.ContentValues;
//import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.axcoto.shinjuku.database.Db;
import com.axcoto.shinjuku.database.Song;
//import com.axcoto.shinjuku.database.Db;
import com.axcoto.shinjuku.database.XMLParser;
import com.axcoto.shinjuku.maki.Remote;

public class SongActivity extends RootActivity{
	private Db db;
	private ListView songList;
	private SongAdapter songAdapter;
	public ArrayList<Song> songs = new ArrayList<Song>();
	public ArrayList<String> songId;
	public ArrayList<String> songTitle;
	
	
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
	public static SongActivity t;
	
	public ArrayList<Song> getSong(String location) throws ParserConfigurationException, SAXException, IOException
	{
		SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        XMLParser myXMLHandler = new XMLParser();
        xr.setContentHandler(myXMLHandler);
        InputStream inStream = new FileInputStream(new File(location));
        Reader reader = new InputStreamReader(inStream,"UTF-8");        
        InputSource is = new InputSource(reader);
        is.setEncoding("UTF-8");
        xr.parse(is);
        Log.i("DONE: ", "DONE");
//        songs = myXMLHandler.getSongs();        
//        songs = new XMLParser(location).get();
        return songs;
	}	

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        SongActivity.t = this;
        final Remote r = Remote.getInstance();
//		We need to keep this on during device scanning--REMOVED FOR CRASH TEST
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		songList = (ListView) findViewById(R.id.song_list);
		initDb();
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
		songs = new ArrayList<Song>();
//		getSong(this.getLocation());
		songAdapter = new SongAdapter(this, R.layout.song_item, songs);
		songAdapter.notifyDataSetChanged();
		songList.setAdapter(songAdapter);
    }
      
	public String getLocation() {
//		return t.getFilesDir() + "/KaraokeDB.xml";
		return "/sdcard/2mbKaraokeDB.xml";
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
    }
    
    public void refresh() throws ParserConfigurationException, SAXException, IOException {
    	long t = System.currentTimeMillis();
    	songs = getSong(this.getLocation());
    	long t2 = System.currentTimeMillis();
    	Log.i("Total TIME: ", Long.toString(t2-t));
//    	Log.i("TOTAL AMOUNT: ", Integer.toString(songs.size()));
//    	songAdapter = new SongAdapter(this, R.layout.song_item, songs);
//		songAdapter.notifyDataSetChanged();
//		songList.setAdapter(songAdapter);
		
	}

    public void testButton(View v) throws ParserConfigurationException, SAXException, IOException
    {    	    	
       refresh();     
        
//    	Toast.makeText(SongActivity.t, "Done processing", Toast.LENGTH_LONG).show();
    	
    }
    
    private class UpdateSongBookTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String...params) {        	
//        	songAdapter = new SongAdapter(SongActivity.t, R.layout.song_item, getSong(SongActivity.t.getLocation()));
        	return "DONE BACKGROUND WORK";
        }

        @Override
        protected void onPostExecute(String result) {
//          songList.setAdapter(songAdapter);
        }	
      }
    
	public void initDb() {
		db = Db.getInstance(this);
		db.open();
	}
	
	public Db getDb() {
		return db;
	}

//	public void runTest() {
//		SQLiteDatabase conn = db.getDatabase();
//		ContentValues v = new ContentValues();
//		Random r = new Random();
//		v.put("id", r.nextInt());
//		v.put("title", "test");
//		v.put("id", 2);
//
//		v.put("title", "dada ");
//		v.put("id", 4);
//		v.put("title", "fdisf ds");
//		conn.insert("HD", null, v);
//	}

	public void dump() {
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
//					songs = new XMLParser(SongActivity.t.getLocation()).get();	
					SongActivity.syncStatus = STATE_DONE;					
					Log.i("DONE: ", "Status done");
					Message msg = mHandler.obtainMessage();
					msg.arg1 = SongActivity.syncStatus;
					if (SongActivity.syncStatus == SongActivity.SYNC_PROCESS_SONGBOOK) {
					//	dump();
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
				songAdapter = new SongAdapter(t, R.layout.song_item, songs);
				songAdapter.notifyDataSetChanged();
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
