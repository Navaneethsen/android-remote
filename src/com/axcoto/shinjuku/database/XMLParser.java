/**
 * Parse through the XML file and push it to the sqlite database
 */
package com.axcoto.shinjuku.database;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.axcoto.shinjuku.sushi.SongActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class XMLParser extends DefaultHandler{
	Boolean currentElement = false;
	String currentId= "";
	String currentName= "";
	String currentValue= "";
	Song song = null;
	ArrayList<Song> list;
	Db db = SongActivity.t.getDb();
	
	public XMLParser() {
//		SQLiteDatabase conn = db.getDatabase();	  
//    	conn.execSQL("DELETE FROM hd");
//    	conn.execSQL("DELETE FROM mp3");
		list = new ArrayList<Song>();
	}
//	public Db getSongs() {
//		return db;
//	}
	
	public ArrayList<Song> getSongs() {
		return list;
	}
	
	 @Override
	    public void startElement(String uri, String localName, String qName,
	            Attributes attributes) throws SAXException {
	 
	        currentElement = true;
	        currentValue = "";
	        currentId = attributes.getValue("id");
	        String temp = attributes.getValue("name");
	        if ((temp != null) && temp.contains("."))
	        currentName = temp.substring(0,temp.lastIndexOf("."));
//	        currentName = temp.substring(0,temp.lastIndexOf("."));
	        if (qName.equals("Karaoke")) {}
	        if (qName.equals("item")) {
//	            song = new Song();
	        } 
	 
	    }
	 
	 @Override
	 	public void endElement(String uri, String localName, String qName)
			    throws SAXException {
	 
	        currentElement = false;
	 
	        /** set value */
	        if (qName.equalsIgnoreCase("item")) {
//	        	SQLiteDatabase conn = db.getDatabase();	
//	        	ContentValues v = new ContentValues();
//	        	v.put("id", currentId);
//	        	v.put("title", currentName);
//	        	conn.insert("hd", null, v);
	        	list.add(new Song(currentId,currentName));
	        }
	        else
	            {}
	    }
	
	 @Override
	    public void characters(char[] ch, int start, int length)
	    throws SAXException {
	 
	        if (currentElement) {
	            currentValue = currentValue +  new String(ch, start, length);
	        }	
	    }
}
