package com.axcoto.shinjuku.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Db {
	private MySQLiteHelper dbHelper;
	private SQLiteDatabase database;
	
	public Db(Context c) {
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
	 * Run a raw query
	 * @param sql
	 */
	public void execute(String sql) {
		
	}

}
