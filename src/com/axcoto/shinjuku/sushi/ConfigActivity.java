package com.axcoto.shinjuku.sushi;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;

public class ConfigActivity extends PreferenceActivity{
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);            
            addPreferencesFromResource(R.xml.setting);
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}