package com.ceenee.q.hd;

import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ceenee.q.hd.R;
import com.ceenee.q.hd.ConfigActivity;
import com.ceenee.q.hd.DeviceActivity;
import com.ceenee.q.hd.MainActivity;
import com.ceenee.q.hd.SongActivity;
import com.ceenee.remote.Remote;
import com.ceenee.maki.AppInfo;

public class ActivityNavigator {
	protected static ActivityNavigator _instance;
	protected Activity _currentActivity;
	
	String currentActivityName;
	final String MENU_NAME_MAIN = "CONFIG";
	final String MENU_NAME_SONG = "SONG";
	final String MENU_NAME_DEVICE = "DEVICE";
	final String MENU_NAME_ABOUT = "ABOUT";
	final String MENU_NAME_CONFIG = "OPTION";
	final String MENU_NAME_POWEROFF = "POWER";
	
	public static ActivityNavigator getInstance() {
		if (_instance == null) {
			_instance = new ActivityNavigator();
		}
		return _instance;
	}
	
	public void setCurrentActivity(Activity a) {
		_currentActivity = a;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		_currentActivity.getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.menu_about:
			String version;
			
			version = new String(AppInfo.VERSION);
			AlertDialog.Builder aboutDialog = new AlertDialog.Builder(_currentActivity);
			aboutDialog.setTitle(version + "-" + AppInfo.ENVIRONMENT);
			aboutDialog.setMessage("Copyright (c) 2013. Developed by CeeNee. Http://CeeNee.Com.");
			aboutDialog.show();
			break;
			
		case R.id.menu_device:
			if (this.currentActivityName == this.MENU_NAME_DEVICE) {
				return false;
			}
			/* Create an Intent to start * MySecondActivity. */
			i = new Intent(_currentActivity, DeviceActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			_currentActivity.finish();
			_currentActivity.startActivityForResult(i, 0x1337);
			currentActivityName = this.MENU_NAME_DEVICE;						
			break;

		case R.id.menu_config:
			if (this.currentActivityName == this.MENU_NAME_CONFIG) {
				return false;
			}
			i = new Intent(_currentActivity, ConfigActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			;
			_currentActivity.finish();
			_currentActivity.startActivityForResult(i, 0x1339);
			currentActivityName = this.MENU_NAME_CONFIG;
			break;
			
		case R.id.menu_song:
			if (this.currentActivityName == this.MENU_NAME_SONG) {
				return false;
			}
			i = new Intent(_currentActivity, SongActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			;
			_currentActivity.finish();
			_currentActivity.startActivityForResult(i, 0x13341);
			this.currentActivityName = MENU_NAME_SONG;
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
			break;
		case R.id.menu_remote:
		default:
			if (_currentActivity instanceof MainActivity) {
				return false;
			}
			i = new Intent(_currentActivity, MainActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			;
			_currentActivity.finish();
			_currentActivity.startActivityForResult(i, 0x13343);
			this.currentActivityName = MENU_NAME_MAIN;
			break;
		}
		return true;
	}
}
