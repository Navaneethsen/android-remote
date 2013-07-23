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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.axcoto.shinjuku.maki.MyLog;
import com.axcoto.shinjuku.maki.Remote;
import com.axcoto.shinjuku.maki.ShareKitFactory;
import com.axcoto.shinjuku.maki.ShareKit;
import com.axcoto.shinjuku.maki.Song;
import com.axcoto.shinjuku.maki.SongAdapter;
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
 * Song book activity. 
 * Song book is an XML file is stored at by default at /files directory of app
 * Once load, the activity will setup even handle, and process song in background.
 *  
 * @see Activity#getFilesDir()
 * @author kureikain
 *
 */
public class SongActivity extends RootActivity implements OnKeyListener, OnItemDoubleTapLister, OnItemLongClickListener {
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
	public Remote remote;
	
	/**
	 * Load song book task.
	 * We should return some progess here
	 * @author kureikain
	 *
	 */
	private class FetchSongsTask extends AsyncTask <String, Integer, Integer> {
		ProgressDialog connectProgress;
		protected void onPreExecute() {
			connectProgress = ProgressDialog
					.show(t,
							"Loading songs...",
							"Please wait, song boosk is processing...",
							true);	
		}
		protected Integer doInBackground(String... urls) {
	         songs = getSong(getLocation(karaoke.equals("hd")? "hd":"mp3"));
	         return songs.size();
	     }

		 protected void onPostExecute(Integer result) {
			 songAdapter = new SongAdapter(t, R.layout.song_item, songs);
	         songList.setAdapter(songAdapter);
	         songAdapter.notifyDataSetChanged();
	         songList.setTextFilterEnabled(true);
	         btn_sharesong.setEnabled(true);
	         connectProgress.hide();
	         MyLog.i("FETCH_SONG", "Finish fetching a number of songs:" + songs.size());
	     }
	}
	
	/**
	 * Load song book task.
	 * We should return some progess here
	 * @author kureikain
	 *
	 */
	private class PdfRenderTask extends AsyncTask <String, Boolean, Boolean> {
		ProgressDialog connectProgress;
		protected void onPreExecute() {
			connectProgress = ProgressDialog
					.show(t,
							"Rendering PDF...",
							"Please wait, PDF is rendering...",
							true);	
		}
		
		/**
		 * Input a XML file and try to render it in PDF
		 */
		protected Boolean doInBackground(String... songbook) {
			String[] part = songbook[0].split("\\.");
	        String filename = part[0];
	        MyLog.i(TAG, "filename: " + filename);
	        
	    	com.lowagie.text.Document doc = new com.lowagie.text.Document(PageSize.A4,10.0f,10.0f,10.0f,10.0f);
	         try {
	        	File dir = new File(t.getFilesDir() + "/pdf/");
	            if(!dir.exists()) dir.mkdirs();
	            File file = new File(dir, "ceenee_songbook.pdf");
	            FileOutputStream fOut = new FileOutputStream(file);
	            PdfWriter.getInstance(doc, fOut);
	            doc.open();
	            
	            Paragraph title = new Paragraph("Karaoke Song Book \n\n");
	            Font titleFont= new Font(Font.TIMES_ROMAN,20,Font.BOLD,harmony.java.awt.Color.RED);
	            title.setAlignment(Paragraph.ALIGN_CENTER);
	            title.setFont(titleFont);
	            doc.add(title);
	            
	            PdfPTable table = new PdfPTable(2);
	            table.setWidthPercentage(new float[]{50,475}, PageSize.A4);
	            PdfPCell c1 = new PdfPCell(new Phrase("Song Number"));
	            c1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
	            table.addCell(c1);

	            c1 = new PdfPCell(new Phrase("Song Name"));
	            c1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
	            table.addCell(c1);
	            
	            
	         } catch (DocumentException de) {
	             Log.e(TAG, "DocumentException:" + de);
	             return false;
	         } catch (IOException e) {
	             Log.e(TAG, "ioException:" + e);
	             return false;
	         }
	         finally
	         {
	             doc.close();
	         }
	         return true;
	     }

		 protected void onPostExecute(Boolean result) {
			 connectProgress.hide();	
			 if (result) {
				 MyLog.i("PDF_CREAEING", "SUccess");
				try {
					ShareKit s = ShareKitFactory.getInstance("email");
					s.execute();
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("SHARE_EMAIL", e.getStackTrace().toString());
				}
			 } else {
				 MyLog.e("PDF_CREAEING", "Failt");
			 }
	     }
	}
	
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
		setContentView(R.layout.activity_song);
		
		fullsong = new ArrayList<Song>();
		btn_sharesong = (Button) findViewById(R.id.btn_share);
		SongActivity.t = this;
		remote = Remote.getInstance();
		if (arr_sort == null) arr_sort = new ArrayList<Song>();
		songList = (ListView) findViewById(R.id.song_list);
		MyLog.i("SongBook_Location", t.getFilesDir().toString());
		
		this.loadConfiguration();
		this.setUpEventHandler();
		this.loadSong();
	}
	
	/**
	 * Load song in background and show a progess bar. Once done, reload song view list.
	 */
	private void loadSong() {
		File f = new File(getLocation(karaoke.equals("hd")? "hd":"mp3"));
		if (f.exists()) {
			new FetchSongsTask().execute(f.getAbsolutePath());	
		}
	}
	
	private void loadConfiguration() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		karaoke = sharedPref.getString("listPref", "hd");
		autosearch = sharedPref.getBoolean("auto_search", true);
	}
	
	private void setUpEventHandler() {
		songList.setTextFilterEnabled(true);
		if (songList.getCount()>0 && !btn_sharesong.isEnabled()) btn_sharesong.setEnabled(true);
		
		ed = (EditText) findViewById(R.id.cmd_songsearch);
		int AndroidVersion = android.os.Build.VERSION.SDK_INT;
		if (AndroidVersion < 16) {
			ed.setOnKeyListener(this);
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

		songList.setOnItemDoubleClickListener(this);
		songList.setOnItemLongClickListener(this);
	}

	
	/**
	 * Input handler for song book searching textfield.
	 * 
	 * @see OnKeyListener
	 */
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
				songAdapter = new SongAdapter(SongActivity.this, R.layout.song_item, arr_sort);
				songList.setAdapter(songAdapter);
				songAdapter.notifyDataSetChanged();
			}
			try {
				remote.execute("enter");
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
	
	/**
	 * Even handle when double on SongList.
	 * 
	 * @see OnItemDoubleTapLister
	 * @param parent adapter view
	 * @param current view
	 * @param position
	 * @param id
	 */
	@Override
	public void OnDoubleTap(AdapterView<?> parent, View view, int position,
			long id) {
		view.setBackgroundColor(getResources().getColor(R.color.Green));
		Song s = songAdapter.getItem(position);
		MyLog.i("SUSHI::SONG", "About to open " + s.getId() + " , name: "
				+ s.getTitle());
		String songid = s.getId();
		if (songid.length() == 0) throw new NullPointerException("empty id");
		
		for (int i = 0; i < songid.length(); i++) {
			try {
				MyLog.i("Pressed: ", songid.substring(0 + i, 1 + i));
				remote.execute(songid.substring(0 + i, 1 + i));
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
			remote.execute("enter");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Even handle when double on SongList.
	 * 
	 * @see OnItemDoubleTapLister
	 */
	@Override
	public void OnSingleTap(AdapterView<?> parent, View view, int position,long id) {
		 MyLog.i("SONG: SINGLE CLICK", "selected song");
	}	

	/**
	 * Long press on a list view item. We will show its full song name here.
	 * 
	 * @see OnItemLongClickListener
	 * @param AdapterView of song list
	 * @param Viewe
	 * @param selected item index
	 * @param arg3
	 * @return
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Toast.makeText(getApplicationContext(),
				songAdapter.getItem(arg2).getTitle(), Toast.LENGTH_LONG)
				.show();
		return true;
	}
	
	/**
	 * Search through song list with filter of songAdaper
	 * @param v
	 */
	public void search(View v) {
		textlength = ed.getText().length();
		songAdapter.getFilter().filter(ed.getText().toString());
//		songList.setAdapter(songAdapter);
		songAdapter.notifyDataSetChanged();		
	}
	
	/**
	 * Find song book database location. The song book is stored under Files dir of application
	 * @see Activity#getFilesDir() 
	 * @param databaseName
	 * @return string to XML song book
	 */
	public String getLocation(String databaseName) {
		if (databaseName.equals("hd")) {
			MyLog.e("Location", t.getFilesDir().toString());
			return t.getFilesDir() + "/KaraokeDB.xml";
		}
		else
			return t.getFilesDir() + "/MP3KaraokeDB.xml";
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
		new PdfRenderTask().execute(t.getLocation(karaoke));
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

}
	
