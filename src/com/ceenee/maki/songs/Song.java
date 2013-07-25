package com.ceenee.maki.songs;

/**
 * Song class
 */
public class Song {
	private String id;
	private String title;

	/**
	 * Empty Constructor
	 * 
	 */
	public Song() {

	}

	/**
	 * Constructor with id and title
	 * 
	 * @param id
	 *            the id assigned to the song
	 * @param title
	 *            title of the song
	 */
	public Song(String id, String title) {
		this.id = id;
		this.title = title;
	}

	/**
	 * Get the id of the song
	 * 
	 * @return id the id of the song
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id for the song
	 * 
	 * @param id
	 *            the id to be assigned
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the title of the song
	 * 
	 * @return title the id of the song
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title for the song
	 * 
	 * @param title
	 *            the title to be assigned
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return title;
	}
}