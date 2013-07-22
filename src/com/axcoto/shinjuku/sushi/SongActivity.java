package com.axcoto.shinjuku.sushi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.axcoto.shinjuku.maki.MyLog;
import com.axcoto.shinjuku.maki.Remote;
import com.axcoto.shinjuku.maki.Song;
import com.axcoto.shinjuku.maki.SongAdapter;
import com.axcoto.shinjuku.maki.SongbookTransporter;
import com.axcoto.shinjuku.maki.Unicode;
import com.axcoto.shinjuku.maki.XMLParser;
import com.axcoto.shinjuku.maki.ListView;
import com.axcoto.shinjuku.maki.ListView.OnItemDoubleTapLister;
//import android.widget.ListView;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Song book hanlder. 
 * Songbook is an XML file is stored at by defau
 * @author kureikain
 *
 */
public class SongActivity extends RootActivity {
	private ListView songList;
	private EditText ed;
	int textlength = 0;
	private SongAdapter songAdapter;
	public static ArrayList<Song> songs = new ArrayList<Song>();
	public static ArrayList<Song> fullsong = new ArrayList<Song>();
	public ArrayList<String> songId;
	public ArrayList<String> songTitle;
	private static ArrayList<Song> arr_sort = new ArrayList<Song>();

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
	//share song book
	private BluetoothService mBTService = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private static final String TAG = "Bluetooth";
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_SEND_EMAIL = 10;
	public static String MAC_ADDRESS = "";
	public static String myUUID;
	private static final int DLG_EXAMPLE1 = 0;
    private static final int TEXT_ID = 0;
    String aEmailList;
    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
	Button btn_sharesong;
    
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
			reader = new InputStreamReader(inStream, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			xr.parse(is);
			songs = myXMLHandler.getSongs();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			MyLog.i("SONGBOOK_NOT_FOUND", "SONG BOOK DON'T EXIST. IGNORE");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
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
		// if (getResources().getConfiguration().orientation==
		// Configuration.ORIENTATION_PORTRAIT)
		setContentView(R.layout.activity_song);
		// else
		// setContentView(R.layout.activity_song_land);
		fullsong = new ArrayList<Song>();
		btn_sharesong = (Button) findViewById(R.id.btn_share);
		SongActivity.t = this;
		songList = (ListView) findViewById(R.id.song_list);
		songList.setTextFilterEnabled(true);
		final Remote r = Remote.getInstance();
		// ToggleButton tb = (ToggleButton) findViewById(R.id.karaoke_switch);
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		karaoke = sharedPref.getString("listPref", "hd");
		// if (songs == null || songs.size() == 0)
		// {
		if (karaoke.equals("hd"))
			songs = getSong(t.getLocation("hd"));
		else
			songs = getSong(t.getLocation("mp3"));

		// }
		MyLog.i("Location", t.getFilesDir().toString());
		if (arr_sort == null)
			arr_sort = new ArrayList<Song>();
		songAdapter = new SongAdapter(SongActivity.this, R.layout.song_item,
				songs);
		songList.setAdapter(songAdapter);
		if (songList.getCount()>0)
		{
			//enable btn_share when load listview ok
			if (!btn_sharesong.isEnabled())
			{
				btn_sharesong.setEnabled(true);
			}
		}
		
		// We need to keep this on during device scanning--REMOVED FOR CRASH
		// TEST
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		this.autosearch = sharedPref.getBoolean("auto_search", true);
		ed = (EditText) findViewById(R.id.cmd_songsearch);
		int AndroidVersion = android.os.Build.VERSION.SDK_INT;
		if (AndroidVersion < 16) {
			ed.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if ((event.getAction() == KeyEvent.ACTION_DOWN)
							&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
						InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						if (im.isAcceptingText())
							im.hideSoftInputFromWindow(ed.getWindowToken(), 0);
						if (autosearch == false) {
							textlength = ed.getText().length();
							arr_sort.clear();
							for (int i = 0; i < songs.size(); i++) {
								if (textlength <= songs.get(i).getTitle()
										.length()) {
									if (Unicode
											.convert(songs.get(i).getTitle())
											.toLowerCase()
											.contains(
													Unicode.convert(ed
															.getText()
															.toString()
															.toLowerCase()))) {
										arr_sort.add(songs.get(i));
									}
								}
							}
							songAdapter = new SongAdapter(SongActivity.this,
									R.layout.song_item, arr_sort);
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

			ed.addTextChangedListener(new TextWatcher() {

				@Override
				public void afterTextChanged(Editable s) {
					if (ed.getText().toString().equals("")) {
						// songAdapter = new SongAdapter(SongActivity.this,
						// R.layout.song_item, songs);
						songAdapter.getFilter().filter(ed.getText().toString());
						songList.setAdapter(songAdapter);
					} else {
						songAdapter.getFilter().filter(s.toString());
						// songAdapter.notifyDataSetChanged();
						songList.setAdapter(songAdapter);
					}
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}
			});
		}

		// initDb();
		MyLog.e("SUSHI:: DEVICE", "Create activity");
		final SongActivity t = this;
		
		songList.setOnItemDoubleClickListener(new OnItemDoubleTapLister() {
			@Override
			public void OnDoubleTap(AdapterView<?> parent, View view, int position,
					long id) {
				view.setBackgroundColor(getResources().getColor(R.color.Green));
				Song s = songAdapter.getItem(position);
				MyLog.i("SUSHI::SONG", "About to open " + s.getId() + " , name: "
						+ s.getTitle());
				String songid = s.getId();
				if (songid.length() == 0)
					throw new NullPointerException("empty id");
				for (int i = 0; i < songid.length(); i++) {
					try {
						MyLog.i("Pressed: ", songid.substring(0 + i, 1 + i));
						r.execute(songid.substring(0 + i, 1 + i));
					} catch (IOException e) {
						MyLog.e("IOException: ", "I0Exception");
						e.printStackTrace();
					} catch (NullPointerException e) {
						MyLog.e("Weird NullPointException: ",
								songid.substring(0 + i, 1 + i));
					} catch (Exception e) {
						MyLog.e("Exception: ", "Exception");
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
			
			@Override
			public void OnSingleTap(AdapterView<?> parent, View view, int position,
					long id) {
				 MyLog.i("SONG: SINGLE CLICK", "selected song");
			}
		});

		songList.setOnItemLongClickListener(new OnItemLongClickListener() {
			public View v;

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				Toast.makeText(getApplicationContext(),
						songAdapter.getItem(arg2).getTitle(), Toast.LENGTH_LONG)
						.show();
				return true;
			}
		});
		
//		songList.setOnItemClickListener(new OnItemClickListener() {
//			@SuppressLint({ "ResourceAsColor", "NewApi" })
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// MyLog.e("DEVICE: CLICKED", "Click ListItem Number " +
//				// position);
//				view.setBackgroundColor(getResources().getColor(R.color.Green));
//				Song s = songAdapter.getItem(position);
//				MyLog.i("SUSHI::SONG", "About to open " + s.getId() + " , name: "
//						+ s.getTitle());
//				String songid = s.getId();
//				if (songid.length() == 0)
//					throw new NullPointerException("empty id");
//				for (int i = 0; i < songid.length(); i++) {
//					try {
//						MyLog.i("Pressed: ", songid.substring(0 + i, 1 + i));
//						r.execute(songid.substring(0 + i, 1 + i));
//					} catch (IOException e) {
//						MyLog.e("IOException: ", "I0Exception");
//						e.printStackTrace();
//					} catch (NullPointerException e) {
//						MyLog.e("Weird NullPointException: ",
//								songid.substring(0 + i, 1 + i));
//					} catch (Exception e) {
//						MyLog.e("Exception: ", "Exception");
//						e.printStackTrace();
//					}
//				}
//				try {
//					r.execute("enter");
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		});
		
		// getSong(this.getLocation());
		songAdapter = new SongAdapter(this, R.layout.song_item, songs);
		// songAdapter.notifyDataSetChanged();
		songList.setAdapter(songAdapter);
	}

	// public void onToggle(View v) throws ParserConfigurationException,
	// SAXException, IOException {
	// ToggleButton tb = (ToggleButton) v;
	// if (tb.getText().equals("HD")) {
	// MyLog.i("Location::" ,t.getLocation("hd"));
	// songs =getSong(t.getLocation("hd"));
	// }
	// else {
	// MyLog.i("Location::",t.getLocation("mp3"));
	// songs = getSong(t.getLocation("mp3"));
	// }
	// songAdapter = new SongAdapter(this, R.layout.song_item, songs);
	// songList.setAdapter(songAdapter);
	// }

	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	//
	// // Checks the orientation of the screen
	// if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	// setContentView(R.layout.activity_song_land);
	// // Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
	// } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	// setContentView(R.layout.activity_song);
	// // Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
	// }
	// }

	// public void onSaveInstanceState(Bundle savedInstanceState) {
	// savedInstanceState.putString("MyText", edtMyText.getText().toString());
	// }
	
	public void search(View v) {
		textlength = ed.getText().length();
		songAdapter.getFilter().filter(ed.getText().toString());
		// songAdapter.notifyDataSetChanged();
		songList.setAdapter(songAdapter);
	}

	public String getLocation(String databaseName) {
		if (databaseName.equals("hd")) {
			MyLog.e("Location", t.getFilesDir().toString());
			return t.getFilesDir() + "/KaraokeDB.xml";
		}
		// return "/storage/sdcard0/Ceenee/KaraokeDB.xml";
		else
			return t.getFilesDir() + "/MP3KaraokeDB.xml";
		// else return "/storage/sdcard0/Ceenee/MP3KaraokeDB.xml";
		// return "/data/data/com.axcoto.shinjuku.sushi/files/2mbKaraokeDB.xml";
	}

	public void testButton(View v) throws ParserConfigurationException,
			SAXException, IOException {
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
				// Toast.makeText(t, "DONE", Toast.LENGTH_LONG).show();
				if (songList.getCount()>0)
				{
					//enable btn_share when load listview ok
					if (!btn_sharesong.isEnabled())
					{
						btn_sharesong.setEnabled(true);
					}
				}
			} else {
				Toast.makeText(t, "No Songbook Found", Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			Remote r = Remote.getInstance();
			String locationType;
			MyLog.i("Sync Status : ", Integer.toString(syncStatus));
			// ToggleButton tb = (ToggleButton)
			// findViewById(R.id.karaoke_switch);
			try {
				if (karaoke.equals("hd")) {
					locationType = "hd";
					r.execute("sync_hd");
				} else {
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
					for (Song s : songs) {
						fullsong.add(s);
					}
					long t2 = System.currentTimeMillis();
					MyLog.i("Total TIME: ", Long.toString(t2 - t1));
					check = false;
				}
				// Set timeout to 45 seconds.
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

	public void clickPlay(View v) {
		this.execute("play");
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
	  
	
	public void click_share(View v) {
//		createPDF();
//		showDialog(DLG_EXAMPLE1);
		
		try {
			SongbookTransporter p = new SongbookTransporter("Email");
			p.send();
		} catch (Exception e) {
			MyLog.i("SONGBOOK_SHARE", e.getMessage());
		}
		
//		AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
//		aboutDialog.setTitle("Notice");
//		aboutDialog.setMessage("Please choose type to share song book");
//	    aboutDialog.setIcon(R.drawable.shareicon);
//		aboutDialog.setPositiveButton("By email", new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				//share song book by email
//				// the first create file pdf to send 
//				createPDF();
//				showDialog(DLG_EXAMPLE1);
//			}
//		});
//		aboutDialog.setNegativeButton("By bluetooth", new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				//share song book by bluetooth
//				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//				//check bluetooth
//				if (mBluetoothAdapter == null)
//				{
//					Toast.makeText(getBaseContext(),"Bluetooth is not available", Toast.LENGTH_LONG).show();
//					Log.d("Bluetooth", "mBluetoothAdapter == null");
//					return;
//				}
//				
//				//bluetooth have support but not enable
//				if (!mBluetoothAdapter.isEnabled()) {
//					Log.d("Bluetooth", "!mBluetoothAdapter.isEnabled()");
//				    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//				    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//				}
//				
//				if (mBluetoothAdapter.isEnabled()) {
//					// Ensure this device is discoverable by others
////		        	ensureDiscoverable(); //no need
//					//show dialog scan devices
//					Intent serverIntent = new Intent(t, DeviceListActivity.class);
//		            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//				}
//			}
//		});
//		AlertDialog dialog = aboutDialog.show();
//		TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
//		messageText.setGravity(Gravity.CENTER);
//		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	String text = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            	if (text.contains(":") || text.contains("-"))
            	{
            		MAC_ADDRESS = text;
            		Log.d(TAG, "MAC_ADDRESS = " + MAC_ADDRESS);
            		if (karaoke.equals("hd"))
            			connectDevice(MAC_ADDRESS, true);
            		else {
            			connectDevice(MAC_ADDRESS, true);
					}
            	}
            	else 
            	{
            		Toast.makeText(getBaseContext(), "Devices not found!", Toast.LENGTH_LONG).show();
            	}
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
        	if (resultCode == RESULT_CANCELED)
			{
				Toast.makeText(getBaseContext(), "Bluetooth was not enabled", Toast.LENGTH_LONG).show();
			}
        case REQUEST_SEND_EMAIL:
        		Toast.makeText(getBaseContext(), "Shared song book to email:" + aEmailList, Toast.LENGTH_LONG).show();
        }
	}
	
    public void connectDevice(String Mac_address, boolean secure) {
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(Mac_address);
        Log.d(TAG, "device = " + device.toString());
        Log.d(TAG, "MAC_ADDRESS = " + Mac_address);
        // Attempt to connect to the device
        if (karaoke.equals("hd"))
        {
        	Log.d(TAG, "mBTService.connect(device, secure,KaraokeDB.xml);");
        	mBTService.connect(device, secure,"KaraokeDB.xml");
        }
        else if (karaoke.equals("mp3"))
        {
        	Log.d(TAG, "mBTService.connect(device, secure,MP3KaraokeDB.xml);");
        	mBTService.connect(device, secure,"MP3KaraokeDB.xml");
		}
        if (mBTService.isIshandlefile())
        {
			File file = null;
			if (karaoke.equals("hd"))
			{
				Log.d(TAG, "if (karaoke.equals(hd))");
        		file = getApplicationContext().getFileStreamPath("KaraokeDB.xml");
			}
			else if (karaoke.equals("mp3")){
				Log.d(TAG, "if (karaoke.equals(mp3))");
				file = getApplicationContext().getFileStreamPath("MP3KaraokeDB.xml");
			}
			if (file.exists())
			{
				Log.d(TAG, "if (file.exists())");
				songs = getSong(t.getLocation(karaoke));
				for (Song s : songs) {
					fullsong.add(s);
				}
				songAdapter = new SongAdapter(t, R.layout.song_item, songs);
				songList.setAdapter(songAdapter);
			}
        }
       
    }
    
    public void getUUID() {
	myUUID = UUID.randomUUID().toString();
    }
    
    public static String getMimeType(String url)
    {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }
    
    /**
     * Called to create a dialog to be shown.
     */
    @Override
    protected Dialog onCreateDialog(int id) {
 
        switch (id) {
            case DLG_EXAMPLE1:
                return createExampleDialog();
            default:
                return null;
        }
    }
 
    /**
     * If a dialog has already been created,
     * this is called to reset the dialog
     * before showing it a 2nd time. Optional.
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
 
        switch (id) {
            case DLG_EXAMPLE1:
                // Clear the input box.
                EditText text = (EditText) dialog.findViewById(TEXT_ID);
                text.setText("");
                break;
        }
    }
 
    /**
     * Create and return an example alert dialog with an edit text box.
     */
    private Dialog createExampleDialog() {
 
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sharing song book by email");
        builder.setMessage("Please input email address:");
 
         // Use an EditText view to get user input.
         final EditText input = new EditText(this);
         input.setId(TEXT_ID);
         builder.setView(input);
 
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
 
            @SuppressLint("NewApi")
			@Override
            public void onClick(DialogInterface dialog, int whichButton) {
                aEmailList = input.getText().toString();
                Log.d(TAG, "email address: " + aEmailList);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
//                i.setType("application/pdf");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{aEmailList});
                i.putExtra(Intent.EXTRA_SUBJECT, "Android remote share song book");
                i.putExtra(Intent.EXTRA_TEXT   , "Song book file is in attachment");
                
                String fname = "";
                String filenamepdf = "";
                if (karaoke.equals("hd"))
                {
                	fname = "KaraokeDB.xml";
                }
                else if (karaoke.equals("mp3"))
                {
                	fname = "MP3KaraokeDB.xml";
        		}
                String[] part = fname.split("\\.");
                String s = part[0];
                filenamepdf = s + ".pdf";
                
                Uri uri;
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/iCeeNee" + "/" + filenamepdf);
                Log.i(TAG, "file path pdf: " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/iCeeNee" + "/" + filenamepdf);
                uri = Uri.fromFile(f);
                i.putExtra(Intent.EXTRA_STREAM, uri);
                
                try {
                    startActivityForResult(Intent.createChooser(i, "Send mail..."),REQUEST_SEND_EMAIL);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(t, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
 
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
 
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
 
        return builder.create();
    }

    public void createPDF()
    {
//		get file name
		String fname = "";
		String filename = "";
        if (karaoke.equals("hd"))
        {
        	fname = "KaraokeDB.xml";
        }
        else if (karaoke.equals("mp3"))
        {
        	fname = "MP3KaraokeDB.xml";
		    }
        String[] part = fname.split("\\.");
        filename = part[0];
        Log.i(TAG, "filename: " + filename);
        
    	com.lowagie.text.Document doc = new com.lowagie.text.Document(PageSize.A4,10.0f,10.0f,10.0f,10.0f);
         try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/iCeeNee";
            File dir = new File(path);
                if(!dir.exists())
                    dir.mkdirs();
            File file = new File(dir, filename + ".pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter.getInstance(doc, fOut);
            //open the document
            doc.open();
            
            Paragraph title = new Paragraph("Karaoke Song Book \n\n");
            Font titleFont= new Font(Font.TIMES_ROMAN,20,Font.BOLD,harmony.java.awt.Color.RED);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setFont(titleFont);
            doc.add(title);
            
            //add table to file pdf
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(new float[]{50,475}, PageSize.A4);
            PdfPCell c1 = new PdfPCell(new Phrase("ID"));
            c1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Song Name"));
            c1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            table.addCell(c1);
            
//			read content file .xml to file .pdf
            try {
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document filexml = docBuilder.parse (new File(t.getFilesDir() + "/" + fname));
                // normalize text representation
                filexml.getDocumentElement ().normalize ();
                NodeList listOfitems = filexml.getElementsByTagName("item");
                int totalitems = listOfitems.getLength();
                Log.i(TAG, "Total no of songs : " + totalitems);
                for(int s=0; s < totalitems ; s++){
                    Node firstitemNode = listOfitems.item(s);
                    if(firstitemNode.getNodeType() == Node.ELEMENT_NODE){
                        Element firstSongElement = (Element)firstitemNode;
                        table.addCell(firstSongElement.getAttribute("id"));
                        table.addCell(firstSongElement.getAttribute("name"));
                    }//end of if clause
                }//end of for loop with s var
    	        }catch (SAXParseException err) {
	    	        Log.e (TAG,"** Parsing error" + ", line " 
	    	             + err.getLineNumber () + ", uri " + err.getSystemId ());
	    	        Log.e (TAG," " + err.getMessage ());

    	        }catch (SAXException e) {
    	        	Log.e (TAG,e.toString());

    	        }catch (Throwable t) {
    	        	Log.e (TAG,t.toString());
    	        
    	        }
            	finally
            	{
            		doc.add(table);
            		Log.i(TAG, "Added to pdf");
            	}
                 
         } catch (DocumentException de) {
             Log.e(TAG, "DocumentException:" + de);
         } catch (IOException e) {
             Log.e(TAG, "ioException:" + e);
         }
         finally
         {
             doc.close();
         }
    } 
}
	
