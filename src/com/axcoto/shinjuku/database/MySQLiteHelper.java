package com.axcoto.shinjuku.database;

import java.util.Arrays;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database Helper for song book database. We creates 2 tables for database.
 * One for HD Karaoke, One for Mp3 karaoke
 * 
 * @author kureikain
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_HD = "hd";
  public static final String TABLE_MP3 = "mp3";
  
  public static final String COLUMN_ID = "id";
  public static final String COLUMN_TITLE = "title";

  private static final String DATABASE_NAME = "song.db";
  private static final int DATABASE_VERSION = 6;

  // Database creation sql statement
  private static final String[] DATABASE_CREATE = { "create table "
      + TABLE_HD + "(" + COLUMN_ID
      + " integer primary key," + COLUMN_TITLE
      + " text not null);"
      
      ,  
      
      "create table "
      + TABLE_MP3 + "(" + COLUMN_ID
      + " integer primary key , " + COLUMN_TITLE
      + " text not null);"
  	};

  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
	  Log.i("MAKI:: DATA", "DEBUG " + Arrays.toString(DATABASE_CREATE));
	  for (int run=0, total=DATABASE_CREATE.length; run<total; run++) {
		  database.execSQL(DATABASE_CREATE[run]);
	  }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_HD);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_MP3);
    onCreate(db);
  }

} 