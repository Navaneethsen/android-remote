/**
 * Parse through the XML file and push it to the sqlite database
 */
package com.axcoto.shinjuku.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

//import android.content.ContentValues;
//import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class XMLParser {
	Scanner sc;
	static int count = 0;
	static String id = "";
	static ArrayList<Song> list = new ArrayList<Song>();
	
	public XMLParser(String location) {
		long t = System.currentTimeMillis();
//		SQLiteDatabase conn = db.getDatabase();
//        ContentValues v = new ContentValues();

		try {
			sc = new Scanner(new File(location));
		} catch (FileNotFoundException e) {
			Log.e("ERROR: ", "File not found exception");
		}

		sc.nextLine();
		sc.useDelimiter("\"");
		while (sc.hasNext())
		{
			String data = sc.next();
			count ++;
			if (count%4==0)
			{
				String title = data.toString().substring(0,data.toString().lastIndexOf("."));
				list.add(new Song(id,title));
				Log.i("Added values: ", id+". "+title);
//		        v.put("title", title);
//		        conn.insert(dbName, null,v);
			}
			else if (count%2==0) { 
				id = data;
//				v.put("id", data);
			}
		}
		sc.close();
		long t2 = System.currentTimeMillis();
		
		Log.i("TIME ELAPSED: ",""+(t2-t));
	}
	
	public ArrayList<Song> get()
	{
		return list;
	}
}
