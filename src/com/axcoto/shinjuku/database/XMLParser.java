/**
 * Parse through the XML file and push it to the sqlite database
 */
package com.axcoto.shinjuku.database;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;

public class XMLParser extends DefaultHandler{
	Boolean currentElement = false;
	String currentId= "";
	String currentName= "";
	String currentValue= "";
	Song song = null;
	private ArrayList<Song> list = new ArrayList<Song>();
	
	public ArrayList<Song> getSongs() {
		return list;
	}
	
	 @Override
	    public void startElement(String uri, String localName, String qName,
	            Attributes attributes) throws SAXException {
	 
	        currentElement = true;
	        currentValue = "";
	        currentId = attributes.getValue("id");
	        currentName = attributes.getValue("name");
	        if (qName.equals("Karaoke")) {}
	        if (qName.equals("item")) {
	            song = new Song();
	        } 
	 
	    }
	 
	 @Override
	 	public void endElement(String uri, String localName, String qName)
			    throws SAXException {
	 
	        currentElement = false;
	 
	        /** set value */
	        if (qName.equalsIgnoreCase("item")) {
	        	song = new Song(currentId,currentName);
	            list.add(song);
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
