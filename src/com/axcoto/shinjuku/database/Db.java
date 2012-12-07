package com.axcoto.shinjuku.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Db {
	private MySQLiteHelper dbHelper;
	private SQLiteDatabase database=null;
	private static Db instance=null;
	
	public static Db getInstance(Context c) {
		if (instance==null) {
			 synchronized (Db.class){
                 if (instance == null) {
                         instance = new Db ();
                 }
			 }			
		}
		instance.init(c);
		return instance;
	}
	
	protected Db() {
		
	}
	
	public void init(Context c) {
		dbHelper = new MySQLiteHelper(c);
	}

	public void open(boolean readonly) throws SQLException {
		if (readonly) {
			database = dbHelper.getReadableDatabase();
		} else {
			database = dbHelper.getWritableDatabase();
		}	
	}
	
	public void open() throws SQLException {
		open(false);
	}

	public void close() {
		dbHelper.close();
	}
	
	/**
	 * Return database when user want to manipulate with raw SQL
	 * @return
	 */
	public SQLiteDatabase getDatabase() {
		if (database==null) {
			open();
		}
		return database;
	}
	
}
