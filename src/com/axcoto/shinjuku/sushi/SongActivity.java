package com.axcoto.shinjuku.sushi;

//import java.util.Random;
import android.view.*;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
//import android.content.ContentValues;
//import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
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
	private EditText ed;
	int textlength=0;
	private SongAdapter songAdapter;
	public static ArrayList<Song> songs = new ArrayList<Song>();
	public static ArrayList<Song> fullsong = new ArrayList<Song>();
	public ArrayList<String> songId;
	public ArrayList<String> songTitle;
	private static ArrayList<Song> arr_sort= new ArrayList<Song>();
	
	
	public ProgressDialog progressDialog;
	private boolean autosearch;

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
	public static final int SYNC_ERROR = 6;
	public static final int SYNC_TIMEMOUT = 7;	
	public static SongActivity t;
	private final int TRIGGER_SEARCH = 1;
	private final long SEARCH_TRIGGER_DELAY_IN_MS = 1000;
	private static String karaoke;
	private String text;
			  
	public ArrayList<Song> getSong(String location) {
		try {
		SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp;	
		sp = spf.newSAXParser();	
        XMLReader xr;
		xr = sp.getXMLReader();
        XMLParser myXMLHandler = new XMLParser();
        xr.setContentHandler(myXMLHandler);        
        	InputStream inStream;	
			inStream = new FileInputStream(new File(location));
			Reader reader;
			reader = new InputStreamReader(inStream,"UTF-8");
			InputSource is = new InputSource(reader);
	        is.setEncoding("UTF-8");
	        xr.parse(is);
	        songs = myXMLHandler.getSongs(); 
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} 	
        return songs;
	}	

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);    
        fullsong = new ArrayList<Song>();
        SongActivity.t = this;        
        songList = (ListView) findViewById(R.id.song_list);
        songList.setTextFilterEnabled(true);
        final Remote r = Remote.getInstance();
//        ToggleButton tb = (ToggleButton) findViewById(R.id.karaoke_switch);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        karaoke = sharedPref.getString("listPref","hd");
        if (songs == null || songs.size() == 0) 
        	{
	        	if (karaoke.equals("hd"))				
					songs =getSong(t.getLocation("hd"));
	        		else songs = getSong(t.getLocation("mp3"));				
			
        	}
        Log.i("Location", t.getFilesDir().toString());
        if (arr_sort == null) arr_sort = new ArrayList<Song>();
        songAdapter = new SongAdapter(SongActivity.this, R.layout.song_item, songs);
		songList.setAdapter(songAdapter);
//		We need to keep this on during device scanning--REMOVED FOR CRASH TEST
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
        
		this.autosearch = sharedPref.getBoolean("auto_search",true);
        ed=(EditText)findViewById(R.id.cmd_songsearch);
        int AndroidVersion = android.os.Build.VERSION.SDK_INT;
        if (AndroidVersion < 16)
        {
        ed.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
			     if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					 InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					 if (im.isAcceptingText()) im.hideSoftInputFromWindow(ed.getWindowToken(),0);
			    	 if (autosearch==false) {
			    		textlength=ed.getText().length();
						arr_sort.clear();
						for(int i=0;i<songs.size();i++)
						{
							if(textlength<=songs.get(i).getTitle().length())
							{
								if (Unicode.convert(songs.get(i).getTitle()).toLowerCase().contains(Unicode.convert(ed.getText().toString().toLowerCase())))
								{
								arr_sort.add(songs.get(i));
								}
							}
						}						
						songAdapter = new SongAdapter(SongActivity.this, R.layout.song_item, arr_sort);
						songList.setAdapter(songAdapter);
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
        
		if (autosearch == true) {				
			
			ed.addTextChangedListener(new TextWatcher(){			

			@Override
			public void afterTextChanged(Editable s) {
				if (ed.getText().toString().equals("")) {
//					songAdapter = new SongAdapter(SongActivity.this, R.layout.song_item, songs);
					songAdapter.getFilter().filter(ed.getText().toString());
					songList.setAdapter(songAdapter);
				}
				else {
					songAdapter.getFilter().filter(s.toString());
//					songAdapter.notifyDataSetChanged();				
				    songList.setAdapter(songAdapter);
				}				
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {	
			}
        });
		}
		
//		initDb();		
		Log.e("SUSHI:: DEVICE", "Create activity");
		final SongActivity t = this;			
		songList.setOnItemLongClickListener(new OnItemLongClickListener() {
			public View v;					
			
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				Toast.makeText(getApplicationContext(),
						songAdapter.getItem(arg2).getTitle(),
						Toast.LENGTH_LONG).show();
				return true;
			}
			
		});
		songList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Log.e("DEVICE: CLICKED", "Click ListItem Number " + position);
				Song s = songAdapter.getItem(position);
				Log.i("SUSHI::SONG", "About to open " + s.getId() + " , name: " + s.getTitle());
				String songid = s.getId();
				if (songid.length() == 0) throw new NullPointerException("empty id");
				for (int i = 0; i < songid.length();i++)
				{
					try {
						Log.i("Pressed: ",songid.substring(0+i,1+i));
						r.execute(songid.substring(0+i,1+i));
					} catch (IOException e) {
						Log.e("IOException: ","I0Exception");
						e.printStackTrace();
					}
						catch (NullPointerException e) {
						Log.e("Weird NullPointException: ", songid.substring(0+i,1+i));					
					} catch (Exception e) {
						Log.e("Exception: ","Exception");
						e.printStackTrace();
					}
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
			}
		});
//		getSong(this.getLocation());
		songAdapter = new SongAdapter(this, R.layout.song_item, songs);
//		songAdapter.notifyDataSetChanged();
		songList.setAdapter(songAdapter);
    }


//	public void onToggle(View v) throws ParserConfigurationException, SAXException, IOException {		
//		ToggleButton tb = (ToggleButton) v;		
//		if (tb.getText().equals("HD")) {
//			Log.i("Location::" ,t.getLocation("hd"));
//			songs =getSong(t.getLocation("hd"));
//		}
//		else {
//			Log.i("Location::",t.getLocation("mp3"));
//			songs = getSong(t.getLocation("mp3"));
//		}
//		songAdapter = new SongAdapter(this, R.layout.song_item, songs);
//		songList.setAdapter(songAdapter);
//	}

	
	public void search(View v) {		
		textlength=ed.getText().length();
		songAdapter.getFilter().filter(ed.getText().toString());
//		songAdapter.notifyDataSetChanged();				
	    songList.setAdapter(songAdapter);		
	}
	
	public String getLocation(String databaseName) {
		if (databaseName.equals("hd")) 
			return t.getFilesDir() + "/KaraokeDB.xml";
//			return "/storage/sdcard0/Ceenee/KaraokeDB.xml";
		else return t.getFilesDir() + "/MP3KaraokeDB.xml";
//		else return "/storage/sdcard0/Ceenee/MP3KaraokeDB.xml";
//		return "/data/data/com.axcoto.shinjuku.sushi/files/2mbKaraokeDB.xml";
	}
    
    public void testButton(View v) throws ParserConfigurationException, SAXException, IOException
    {    	    	
    	new ProgressTask().execute();      
    }
    
    private class ProgressTask extends AsyncTask<String, Void, Boolean> {
    	private long elapsed_time = 0;
    	private ProgressDialog dialog = new ProgressDialog(SongActivity.this);
    	
    	@Override
    	 protected void onPreExecute() {
    		elapsed_time = System.currentTimeMillis(); 
    		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
             dialog.setMessage("SYNCING");
             dialog.show();
             syncStatus = SYNC_WAIT_UPLOAD;
         }
    	 
    	@Override
        protected void onPostExecute(final Boolean success) {
    		if (dialog.isShowing()) {
            	dialog.dismiss();
            }
    		songAdapter = new SongAdapter(t, R.layout.song_item, songs);
			songList.setAdapter(songAdapter);          
			
            if (success) {
//                Toast.makeText(t, "DONE", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(t, "No Songbook Found", Toast.LENGTH_LONG).show();
            }
        }
		@Override
		protected Boolean doInBackground(String... params) {
			Remote r = Remote.getInstance();
			String locationType;
			Log.i("Sync Status : ", Integer.toString(syncStatus));
//			ToggleButton tb = (ToggleButton) findViewById(R.id.karaoke_switch);
			try {
			if (karaoke.equals("hd")) {
				locationType = "hd";
				r.execute("sync_hd");
			}
			else {
				locationType = "mp3";
				r.execute("sync_mp3");
			}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			boolean check = true;
			while (check == true) {
				if (syncStatus == SYNC_ERROR) {
					songs = new ArrayList<Song>();
					check = false;
					return false;
				} else if (syncStatus == SYNC_DONE) {
					long t1 = System.currentTimeMillis(); 		
						songs = getSong(t.getLocation(locationType));
						for (Song s: songs){
							fullsong.add(s);
						}
			    	long t2 = System.currentTimeMillis();
			    	Log.i("Total TIME: ", Long.toString(t2-t1));				
					check = false;
				}
				//Set timeout to 45 seconds.
				if (System.currentTimeMillis() - this.elapsed_time > 1000 * 45) {
					check = false;
					syncStatus = SYNC_ERROR;
				}
			}			
			return true;
		}
    	
    }           
    
    public void clickDelete(View v) {		
		this.execute("delete");		
	}
    
    public void clickRight(View v) {
    	this.execute("right");
    }
    
    public void clickLeft(View v) {
    	this.execute("left");
    }
    
    public void execute(String command) {
		Remote r = Remote.getInstance();
		if (r.getConnected()) {
			try {
				r.execute(command);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
			}
		}
	}
}
