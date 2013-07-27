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
		public boolean execute(String source, String target);
	}

	public interface OnExportListener {
		public void beforeRun();
		public void whenDone();
	};
	
	protected ExportDriver driver;
	protected Activity activity; 
	protected String TAG = "SONG_EXPORT";
	protected Hashtable<String, String> params;
	public OnExportListener onExportListener;
	protected TaskWorker taskWorker;
	
	/**
	 * Setter method
	 * @see this{@link #onExportListener} 
	 * @param e
	 */
	public void setOnExportListener(OnExportListener e) {
		onExportListener = e;
	}
	
	/**
	 * Set parameter for export task 
	 * @param name
	 * @param value
	 */
	public void setParam(String n, String v) {
		params.put(n, v);
	}
	
	public Export(Activity parent, String type) {
		activity = parent;
		params = new Hashtable<String, String>();
		
		if (type.equalsIgnoreCase("pdf")) {
			driver = new PdfExport();
		}
		taskWorker = new TaskWorker();
	}

	private class TaskWorker extends AsyncTask <Hashtable<String, String>, Boolean, Boolean> {
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
		protected Boolean doInBackground(Hashtable<String, String>... taskParams) {
			return driver.execute(taskParams[0].get("source"), taskParams[0].get("to"));
	     }
		
		/**
		 * Once we done, we can run delegate method if a delegation is defined.
		 * 
		 */
		 protected void onPostExecute(Boolean result) {
			 if (onExportListener==null) {
				 connectProgress.hide();	
				 
				 if (result) {
					 MyLog.i("PDF_CREAEING", "Success");
					try {
						ShareKit s = ShareKitFactory.getInstance(activity, "email");
						s.execute();
					} catch (Exception e) {
						e.printStackTrace();
						Log.i("SHARE_EMAIL", e.getStackTrace().toString());
					}
				 } else {
					 MyLog.e("PDF_CREAEING", "Failt");
				 }
			 } else {
				 onExportListener.whenDone();
			 }
	     }

	}
	
	public void run() {
		taskWorker.execute(params);
	}
	
}
