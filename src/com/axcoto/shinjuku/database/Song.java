package com.axcoto.shinjuku.database;

public class Song {

	private String id;
	private String title;

	public Song(String id, String title) {
		this.id = id;
		this.title = title;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return title;
	}
}