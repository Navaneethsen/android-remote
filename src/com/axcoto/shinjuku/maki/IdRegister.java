package com.axcoto.shinjuku.maki;
 
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
 
import android.content.Context;
import android.util.Log;
 
public class IdRegister {
	String id;
	Context context;
	
	public IdRegister(String id) {
		this.id = id;
	}
	
	public void setContext(Context c) {
		this.context = c;
	}
	
	public boolean perform() {
		try {
			FileOutputStream fos = context.openFileOutput(com.axcoto.shinjuku.sushi.CommonUtilities.REGID_FILENAME, Context.MODE_PRIVATE);
			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(fos));			
			br.write(id);
			br.flush();
			br.close();
			fos.close();
		} catch (FileNotFoundException e) {
			MyLog.e("SUSHI:: DEVICE", "Cannot find the file ");
		} catch (IOException e) {
			MyLog.e("SUSHI:: DEVICE", "Cannot write data to the file ");			
		}
		return true;
	}
 
}