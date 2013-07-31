package com.ceenee.maki.songs;

import java.util.Hashtable;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.ceenee.maki.MyLog;
import com.ceenee.maki.sharekit.ShareKit;
import com.ceenee.maki.sharekit.ShareKitFactory;
import com.ceenee.maki.songs.PdfExport;

/**
 * Exporter. This will handle song book any to any format (pdf for now, but txt can be add or different format)
 * This has a onExportListener delegate so we can define what method to run before and after finish processing
 * @see this{@link #onExportListener}
 * @see this{@link #Export(Activity, String)}
 * @author kureikain
 *
 */
public class Export {
	public interface ExportDriver {
		public void setActivity(Activity a);		
		public boolean execute(String source, String target) throws Exception;
		public boolean execute(SongBook songbook, String target) throws Exception;
	}

	public interface OnExportListener {
		public void beforeRun();
		
		/**
		 * This method is only called once the exporting is successful
		 */
		public void whenDone();
	};
	
	protected ExportDriver driver;
	protected Activity activity; 
	protected String TAG = "SONG_EXPORT";
	protected Hashtable<String, String> params;
	public OnExportListener onExportListener;
	protected TaskWorker taskWorker;
	public SongBook songbook;
	
	/**
	 * Setter method
	 * @see this{@link #onExportListener} 
	 * @param e
	 */
	public void setOnExportListener(OnExportListener e) {
		onExportListener = e;
	}
	
	/**
	 * Assign songbook that will be exported.
	 * 
	 * @param b
	 */
	public void setSongBook(SongBook b) {
		songbook = b;
	}
	
	/**
	 * Set parameter for export task.
	 * An exporting task should have 
	 * @param name
	 * @param value
	 */
	public void setParam(String n, String v) {
		params.put(n, v);
	}
	
	public Export() throws Exception{
		throw new IllegalArgumentException("You need to provide Acitivty and an export format: pdf, txt..");
	}
	
	public Export(Activity parent, String type) {
		activity = parent;
		params = new Hashtable<String, String>();
		
		if (type.equalsIgnoreCase("pdf")) {
			driver = new PdfExport();
			driver.setActivity(parent);
		}
		taskWorker = new TaskWorker();
	}

	private class TaskWorker extends AsyncTask <String, Boolean, Boolean> {
		ProgressDialog connectProgress;
		
		protected void onPreExecute() {
			if (onExportListener == null) {
				connectProgress = ProgressDialog
						.show(activity,
								"Rendering song book...",
								"Please wait, we are processing your song book...",
								true);	
			} else {
				onExportListener.beforeRun();
			}
		}
		
		/**
		 * Input a XML file and try to render it in PDF
		 */
		@Override
		protected Boolean doInBackground(String... to) {
			Boolean result = true;
			try {
				result = driver.execute(songbook, to[0]);
			} catch (Exception e) {
				MyLog.e("SONGBOOK_EXPORT", "Cannot exporting song book.");
			}
			return result;
	     }
		
		/**
		 * Once we done, we can run delegate method if a delegation is defined.
		 * 
		 */
		 protected void onPostExecute(Boolean result) {
			 if (result) {
				 if (onExportListener==null) {
					 connectProgress.hide();	
					 MyLog.i("PDF_CREATING", "Success to generate file. However, no listene is implemented yet. Let implement onExportLisnterner");
				 } else {
					 onExportListener.whenDone();
				 }
			 } else {
				 MyLog.e("PDF_CREAEING", "Failt");
			 }
	     }

	}
	
	/**
	 * Run the task.
	 * 
	 * @param to
	 */
	public void run(String to) {
		taskWorker.execute(to);
	}
	
}
