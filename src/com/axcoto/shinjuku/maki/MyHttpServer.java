package com.axcoto.shinjuku.maki;

import java.io.File;
import java.io.IOException;
import java.util.Properties;


interface SongBookUploader {
	public void upload();
	public void response();
}

public class MyHttpServer extends NanoHTTPD implements SongBookUploader{
	protected static MyHttpServer instance=null;
	protected final int SERVER_PORT = 5320;
	
	public static MyHttpServer getInstance() throws IOException{
		if (instance==null) {
			instance = new MyHttpServer();
		}
		return instance;
		
	}
	public MyHttpServer() throws IOException {
		super(5320, new File("."));
	}
	
	public Response serve( String uri, String method, Properties header, Properties parms, Properties files )
	{
		if (uri=="upload.html") {
			
		} else {
			
		}
		
		System.out.println( method + " '" + uri + "' " );
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
