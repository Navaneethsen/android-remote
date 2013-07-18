package com.axcoto.shinjuku.sushi;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.axcoto.shinjuku.maki.Remote;
import com.axcoto.shinjuku.sushi.ConfigActivity;
import com.axcoto.shinjuku.sushi.DeviceActivity;
import com.axcoto.shinjuku.sushi.MainActivity;
import com.axcoto.shinjuku.sushi.SongActivity;

public class ActivityNavigator {
	protected static ActivityNavigator _instance;
	protected Activity _currentActivity;
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
		case R.id.menu_device:
			if (_currentActivity instanceof DeviceActivity) {
				return false;
			}
			/* Create an Intent to start * MySecondActivity. */
			i = new Intent(_currentActivity, DeviceActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			/*
			 * Send intent to the OS to make * it aware that we want to start *
			 * MySecondActivity as a SubActivity.
			 */
			_currentActivity.finish();
			_currentActivity.startActivityForResult(i, 0x1337);
			break;

		case R.id.menu_config:
			if (_currentActivity instanceof ConfigActivity) {
				return false;
			}
			i = new Intent(_currentActivity, ConfigActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			;
			_currentActivity.finish();
			_currentActivity.startActivityForResult(i, 0x1339);
			break;
		case R.id.menu_song:
			if (_currentActivity instanceof SongActivity) {
				return false;
			}
			i = new Intent(_currentActivity, SongActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			;
			_currentActivity.finish();
			_currentActivity.startActivityForResult(i, 0x13341);
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
			_currentActivity.finish();
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
			break;
		}
		return true;
	}
}
