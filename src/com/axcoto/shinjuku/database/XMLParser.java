/**
 * Parse through the XML file and push it to the sqlite database
 */
package com.axcoto.shinjuku.database;

import java.io.IOException;
import java.util.Scanner;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class XMLParser {
	Scanner sc;
	String location;
	static int count = 0;
	SQLiteDatabase conn;
	 //TESTED IN WARMUP SECTION
	public XMLParser(String dbName, Db db, String location) throws IOException{
		conn = db.getDatabase();
        ContentValues v = new ContentValues();
//		if (dbName.equals("HD")) location = "...HD location";
//		else if (dbName.equals("MP3")) location = "...MP3 location";
		sc = new Scanner(location);
//		StringBuilder output = new StringBuilder();
		sc.nextLine();
		sc.useDelimiter("\"");
		while (sc.hasNext())
		{
			String data = sc.next();
			count ++;
			if (count%4==0)
			{
				String title = data.toString().substring(0,data.toString().lastIndexOf("."));
//				output.append(title + "\n");		        
		        v.put("Title", title);
		        conn.insert(dbName, null,v);
			}
			else if (count%2==0) { 
//				output.append(data + "::");
				v.put("ID", data);
			}
		}
		
//		String newLocation = location.substring(0,location.lastIndexOf("/"));
//		File f = new File(newLocation+"/output.txt");
//		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
//		writer.write(output.toString());
	}

	
	
}
