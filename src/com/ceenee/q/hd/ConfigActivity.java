package com.ceenee.q.hd;

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

import com.ceenee.maki.MyHttpServer;
import com.ceenee.maki.MyLog;
import com.ceenee.q.hd.R;
import com.ceenee.remote.Remote;

public class ConfigActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.activity_config);
		final CheckBoxPreference serverPref = (CheckBoxPreference) findPreference("sync_server_status");
		serverPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						try {
							if (serverPref.isChecked()) {
								MyHttpServer.close();
								MyLog.i("SUSHI:: CONFIG", "Stop the web server");
							} else {
								MyLog.i("SUSHI:: CONFIG",
										"Restart the web server");
								MyHttpServer.start();
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
		ActivityNavigator.getInstance().setCurrentActivity(this);
		return ActivityNavigator.getInstance().onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return ActivityNavigator.getInstance().onOptionsItemSelected(item);
	}
}