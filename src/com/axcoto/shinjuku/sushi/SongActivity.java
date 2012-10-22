package com.axcoto.shinjuku.sushi;

import java.util.Random;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.axcoto.shinjuku.database.Db;

public class SongActivity extends RootActivity {
	Db db;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        this.initDb(); 
        this.runTest();
    }
    
    public void initDb() {
    	db = Db.getInstance(this);
    	db.open();
    }
    
    public void runTest() {    	
    	SQLiteDatabase conn = db.getDatabase();
        ContentValues v = new ContentValues();
        Random r = new Random();
        v.put("id", r.nextInt());
        v.put("title", "test");
        conn.insert("HD", null, v);
    }

}
