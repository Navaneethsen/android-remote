package com.ceenee.maki;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import com.ceenee.q.SongActivity;


interface SongBookUploader {
	public void upload();

	public void response();
}

public class MyHttpServer extends NanoHTTPD implements SongBookUploader {
	public static final String MIME_JSON = "application/json";
	private static int has_file = 0;
	protected static MyHttpServer instance = null;
	protected static int SERVER_PORT = 5320;
	protected static File DOC_ROOT;
	protected static int port;
	InputStream in;
	OutputStream out;

	/**
	 * We run a embedded web server on port 5320 for song book syncing. The
	 * docroot of web server is homedirectory of app. So, we just prepare some document for it
	 */
	public static void prepareDocRoot(Activity activity) {
		try {
			DOC_ROOT = activity.getFilesDir();
			MyLog.i("SUSHI:: MAIN :: HomeDir is", DOC_ROOT.toString());
			File file = activity.getFileStreamPath("file-upload.html");
			if (file.exists()) {
				MyLog.i("MAKI: SERVER", "initialize app before so we don't need to copy the file for web server");
			} else {
				copyAssets(activity);
			}
		} catch (Exception e) {
			MyLog.e("MAKI:: SERVER:: GENERAL ERROR", e.getMessage());
		}
	}
	
	/**
	 * Copy asset for Docroot of http from android resource system into DOCROOT
	 */
	public static void copyAssets(Activity activity) {
		MyLog.e("MAKI:: MAIN:: ASSET COPY",
				"Start to copy asset for the first initialization of app");
		AssetManager assetManager = activity.getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			MyLog.e("SUSHI:: MAINACTIVITY:: ERROR", e.getMessage());
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
				out = activity.openFileOutput(filename, Context.MODE_PRIVATE); // new
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
				MyLog.e("MAKI:: MAIN ACITIVITY", "Cannot copy asset: " + filename
						+ ". Error: " + e.getMessage());
			}
		}
	}

	/**
	 * Sadly Java doesn't have sth make copy file a breeze.
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	private static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public static MyHttpServer getInstance(int port, File docRoot)
			throws IOException {
		if (instance == null) {
			MyLog.e("MAKI: NANO", "SERVER started with docRoot: " + docRoot);
			instance = new MyHttpServer(port, docRoot);
			DOC_ROOT = docRoot;
			MyHttpServer.port = port;
		}
		return instance;
	}
	
	public static MyHttpServer start() throws IOException {
		return instance = new MyHttpServer(SERVER_PORT, DOC_ROOT);
	}

	public MyHttpServer(int port, File wwwroot) throws IOException {
		super(port, wwwroot);
	}

	public MyHttpServer() throws IOException {
		super(5320, new File("."));
	}

	public Response serve(String uri, String method, Properties header,
			Properties parms, Properties files) {
		has_file = 0;

		MyLog.e("MAKI: HTTP SERVER", method + " '" + uri + "' ");

		if (uri.equalsIgnoreCase("/upload.html")) {
			has_file = 1;
		}

		Enumeration e = header.propertyNames();
		while (e.hasMoreElements()) {
			String value = (String) e.nextElement();
			myOut.println("  HDR: '" + value + "' = '"
					+ header.getProperty(value) + "'");
		}
		e = parms.propertyNames();
		while (e.hasMoreElements()) {
			String value = (String) e.nextElement();
			myOut.println("  PRM: '" + value + "' = '"
					+ parms.getProperty(value) + "'");
		}
		e = files.propertyNames();

		while (e.hasMoreElements()) {
			String value = (String) e.nextElement();
			myOut.println("  UPLOADED: '" + value + "' = '"
					+ files.getProperty(value) + "'");

			MyLog.e("MAKI: START_COPY_UPLOADED_FILE",
					"Copy temp file to correct location");
			try {
				SongActivity.syncStatus = SongActivity.SYNC_RECEIVE_SONGBOOK;
				MyLog.i("Sync Status: ", "STARTING TO WRITE SONGBOOK");
				in = new FileInputStream(new File(files.getProperty(value)
						.toString()));
				out = new FileOutputStream(DOC_ROOT.getAbsoluteFile() + "/"
						+ parms.getProperty("upload1").toString());

				BufferedInputStream bis = new BufferedInputStream(in);

				byte[] buffer = new byte[1024];
				// int read;
				// while ((read = in.read(buffer)) != -1) {
				// out.write(buffer, 0, read);
				// }
				int length;
				while ((length = in.read(buffer)) > 0) {

					out.write(buffer, 0, length);

				}

				in.close();
				out.close();
				has_file = 2;
				SongActivity.syncStatus = SongActivity.SYNC_PROCESS_SONGBOOK;
				MyLog.i("Sync Status: ", "SONG BOOK RETRIEVED");
			} catch (FileNotFoundException fnfe) {
				MyLog.e("MAKI: SERVER", "File location is incorrect. Error:"
						+ fnfe.getMessage());
			} catch (IOException ioe) {
				MyLog.e("MAKI: SERVER",
						"Cannot read/write to file. Error:" + ioe.getMessage());
			} catch (Exception ex) {
				MyLog.e("MAKI: SERVER",
						"Unknow error when copying temp uploader file to correct location in docroot "
								+ ex.getMessage());
			}
		}
		if (has_file == 2) {
			SongActivity.syncStatus = SongActivity.SYNC_DONE;
			has_file = 0;
		} else {
			SongActivity.syncStatus = SongActivity.SYNC_ERROR;
			has_file = 0;
		}
		if (uri.equalsIgnoreCase("/info.html")) {

			String msg = "<html><body><h1>Hello server</h1>\n";
			if (parms.getProperty("username") == null)
				msg += "<form action='?' method='get'>\n"
						+ "  <p>Your name: <input type='text' name='username'></p>\n"
						+ "</form>\n";
			else
				msg += "<p>Hello, " + parms.getProperty("username") + "!</p>";

			msg += "</body></html>\n";
			return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, msg);
		} else if (uri.equalsIgnoreCase("upload.html")) {
			return new NanoHTTPD.Response(HTTP_OK, MIME_JSON,
					"{result:1,msg:'File uploaded successfully'}");
		} else {
		}

		return serveFile(uri, header, DOC_ROOT, true);

	}

	public void upload() {

	}

	public void response() {

	}

	public static void close() {
		instance.stop();
		instance = null;
	}
	
}
