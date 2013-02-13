package com.axcoto.shinjuku.maki;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

import com.axcoto.shinjuku.sushi.SongActivity;

import android.util.Log;

interface SongBookUploader {
	public void upload();

	public void response();
}

public class MyHttpServer extends NanoHTTPD implements SongBookUploader {
	public static final String MIME_JSON = "application/json";
	private static int has_file = 0;
	protected static MyHttpServer instance = null;
	protected final int SERVER_PORT = 5320;
	protected static File docRoot;
	protected static int port;
	InputStream in;
	OutputStream out;

	public static MyHttpServer getInstance(int port, File docRoot)
			throws IOException {
		if (instance == null) {
			MyLog.e("MAKI: NANO", "SERVER started with docRoot: " + docRoot);
			instance = new MyHttpServer(port, docRoot);
			MyHttpServer.docRoot = docRoot;
			MyHttpServer.port = port;
		}
		return instance;
	}

	public static MyHttpServer getInstance() throws IOException {
		if (instance == null) {
			MyLog.e("MAKI: NANO", "SERVER started with docRoot: " + docRoot);
			instance = new MyHttpServer(port, docRoot);
		}
		return instance;
	}

	public static MyHttpServer start() throws IOException {
		MyLog.e("MAKI: NANO", "SERVER started with docRoot: " + docRoot);
		return instance = new MyHttpServer(port, docRoot);

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
				out = new FileOutputStream(docRoot.getAbsoluteFile() + "/"
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

		return serveFile(uri, header, this.docRoot, true);

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
