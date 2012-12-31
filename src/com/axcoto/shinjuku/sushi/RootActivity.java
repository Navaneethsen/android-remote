package com.axcoto.shinjuku.sushi;

import java.io.IOException;

import com.axcoto.shinjuku.maki.Remote;
import com.axcoto.shinjuku.sushi.R.id;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import com.axcoto.shinjuku.sushi.*;

public class RootActivity extends Activity {

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
            case R.id.menu_about:
            	AlertDialogManager alert = new AlertDialogManager();
            	alert.showAlertDialog(this,
						"iCeeNee v" + CommonUtilities.RELEASE_VERSION,
						"CeeNee Remote Control by Http://CeeNee.Com.", false);			
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
            	i = new Intent( this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);; 
            	finish();
            	startActivityForResult(i, 0x13343);
            	break;
        }
        return false;
    }
}
