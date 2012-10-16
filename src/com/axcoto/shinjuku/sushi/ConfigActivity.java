package com.axcoto.shinjuku.sushi;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ConfigActivity extends PreferenceActivity{
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);            
            addPreferencesFromResource(R.layout.activity_config);
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
        switch (item.getItemId()) 
        {
            case R.id.menu_device:
                //ShowScreenAddSite();

            	// Place code to handle Button-Click here. 
            	//Log.e(Log.VERBOSE, "Lolz. Test event listenr");
            	/* Create an Intent to start * MySecondActivity. */ 
            	i = new Intent( this, DeviceActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
            	/* Send intent to the OS to make * it aware that we want to start * MySecondActivity as a SubActivity. */ 
            	finish();
            	startActivityForResult(i, 0x1337);            	
                break;

            case R.id.menu_config:
            	i = new Intent( this, ConfigActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);;
            	finish();
            	startActivityForResult(i, 0x1339);
				break;
            case R.id.menu_song:
            	i = new Intent( this, SongActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);; 
            	finish();
            	startActivityForResult(i, 0x13341);
            	break;	
            case R.id.menu_remote:	
            default:    
            	i = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);; 
            	finish();
            	startActivityForResult(i, 0x13343);
            	break;
        }
        return false;
    }
}