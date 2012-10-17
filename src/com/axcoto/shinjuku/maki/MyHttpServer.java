package com.axcoto.shinjuku.maki;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

import android.util.Log;


interface SongBookUploader {
	public void upload();
	public void response();
}

public class MyHttpServer extends NanoHTTPD implements SongBookUploader{
	public static final String MIME_JSON = "application/json";
	
	protected static MyHttpServer instance=null;
	protected final int SERVER_PORT = 5320;
	protected File docRoot;
	
	public static MyHttpServer getInstance(int port, File docRoot) throws IOException{
		if (instance==null) {
			Log.e("MAKI: NANO", "SERVER started with docRoot: " + docRoot);
			instance = new MyHttpServer(port, docRoot);
		}
		return instance;		
	}
	
	public MyHttpServer(int port, File wwwroot) throws IOException {
		super(port, wwwroot);
		this.docRoot = wwwroot;
	}
	
	public MyHttpServer() throws IOException {
		super(5320, new File("."));
	}
	
	public Response serve( String uri, String method, Properties header, Properties parms, Properties files )
	{
	
		Log.e("MAKI: HTTP SERVER", method + " '" + uri + "' " );

		if (uri=="/info.html") {

			String msg = "<html><body><h1>Hello server</h1>\n";
			if ( parms.getProperty("username") == null )
				msg +=
					"<form action='?' method='get'>\n" +
					"  <p>Your name: <input type='text' name='username'></p>\n" +
					"</form>\n";
			else
				msg += "<p>Hello, " + parms.getProperty("username") + "!</p>";

			msg += "</body></html>\n";
			return new NanoHTTPD.Response( HTTP_OK, MIME_HTML, msg );
		}
		
		if (uri=="/upload.html") {
			return new NanoHTTPD.Response(HTTP_OK, MIME_JSON, "{result:1,msg:'File uploaded successfully'}");
		}

		Enumeration e = header.propertyNames();
		while ( e.hasMoreElements())
		{
			String value = (String)e.nextElement();
			myOut.println( "  HDR: '" + value + "' = '" +
								header.getProperty( value ) + "'" );
		}
		e = parms.propertyNames();
		while ( e.hasMoreElements())
		{
			String value = (String)e.nextElement();
			myOut.println( "  PRM: '" + value + "' = '" +
								parms.getProperty( value ) + "'" );
		}
		e = files.propertyNames();
		while ( e.hasMoreElements())
		{
			String value = (String)e.nextElement();
			myOut.println( "  UPLOADED: '" + value + "' = '" +
								files.getProperty( value ) + "'" );
			Log.e("MAKI: MYHTTP", "Copy temp file to correct location");
			
			try {
				InputStream in = new FileInputStream(new File(files.getProperty(value).toString()));
				OutputStream out = new FileOutputStream(docRoot.getAbsoluteFile() + files.getProperty("datafile").toString());
				
				byte[] buffer = new byte[1024];
				int read;
				while ((read = in.read(buffer)) != -1) {
					out.write(buffer, 0, read);
				}
				
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (FileNotFoundException fnfe) {
				Log.e("MAKI: SERVER", "File location is in correct. Error:" + fnfe.getMessage());
			} catch (IOException ioe) {
				Log.e("MAKI: SERVER", "Cannot read/write to file. Error:" + ioe.getMessage());
			} catch (Exception ex) {
				Log.e("MAKI: SERVER", "Unknow error when copying temp uploader file to correct location in docroot " + ex.getMessage());
			}
		}

		return serveFile( uri, header, this.docRoot, true );
		
	}


	public static void start( String[] args )
	{
		try
		{
			new MyHttpServer();
		}
		catch( IOException ioe )
		{
			System.err.println( "Couldn't start server:\n" + ioe );
			System.exit( -1 );
		}
		System.out.println( "Listening on port 8080. Hit Enter to stop.\n" );
		try { System.in.read(); } catch( Throwable t ) {};
	}
	
	public  void upload() {
		
	}
	
	public void response() {
		
	}
	
}
