package com.axcoto.shinjuku.sushi;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.axcoto.shinjuku.maki.MyHttpServer;
import com.axcoto.shinjuku.maki.Remote;

public class ConfigActivity extends PreferenceActivity {
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);            
            addPreferencesFromResource(R.layout.activity_config);
            final CheckBoxPreference serverPref = (CheckBoxPreference)findPreference("sync_server_status");
            serverPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					try {
						if (serverPref.isChecked()) {
							MyHttpServer s = MyHttpServer.getInstance();
							Log.i("SUSHI:: CONFIG", "Stop the web server");
							s.close();
						} else {
							Log.i("SUSHI:: CONFIG", "Restart the web server");
							MyHttpServer s = MyHttpServer.start();					
						}
						return true;
					} catch (Exception e) {
						
					}
					return true;
				}
            	
            });
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
            case R.id.menu_power:
            	Remote r = Remote.getInstance();
				try {
					r.execute("power");
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
            	finish();
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