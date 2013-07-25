/**
 * Parse through the XML file and push it to the sqlite database
 */
package com.ceenee.maki;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.ceenee.maki.songs.Song;

public class XMLParser extends DefaultHandler {
	Boolean currentElement = false;
	String currentId = "";
	String currentName = "";
	String currentValue = "";
	Song song = null;
	ArrayList<Song> list;

	public XMLParser() {
		list = new ArrayList<Song>();
	}

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
		if ((temp != null) && temp.contains(".")) currentName = temp.substring(0, temp.lastIndexOf("."));
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		currentElement = false;

		/** set value */
		if (qName.equalsIgnoreCase("item")) {
			list.add(new Song(currentId, currentName));
		} else {
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (currentElement) {
			currentValue = currentValue + new String(ch, start, length);
		}
	}
}
