package com.ceenee.maki.songs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.ceenee.maki.MyLog;
import com.ceenee.maki.XMLParser;
import com.ceenee.maki.songs.Song;

public class SongBook {
//	protected String[] _songs;
//	protected String[] _songAlphabet;
	
	public ArrayList<Song> songs = new ArrayList<Song>();
	
	public void setSong(ArrayList<Song> s) {
		songs = s;
	}
	
	public ArrayList<Song> load(String location) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp;
			sp = spf.newSAXParser();
			XMLReader xr;
			xr = sp.getXMLReader();
			XMLParser myXMLHandler = new XMLParser();
			xr.setContentHandler(myXMLHandler);
			InputStream inStream;
			inStream = new FileInputStream(new File(location));
			Reader reader;
			reader = new InputStreamReader(inStream, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			xr.parse(is);
			songs = myXMLHandler.getSongs();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			MyLog.i("SONGBOOK_NOT_FOUND", "SONG BOOK DON'T EXIST. IGNORE");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return songs;
	}
	
	
}
