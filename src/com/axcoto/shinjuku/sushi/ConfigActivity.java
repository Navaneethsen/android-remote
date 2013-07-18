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
import com.axcoto.shinjuku.maki.MyLog;
import com.axcoto.shinjuku.maki.Remote;

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
								MyHttpServer s = MyHttpServer.getInstance();
								MyLog.i("SUSHI:: CONFIG", "Stop the web server");
								s.close();
							} else {
								MyLog.i("SUSHI:: CONFIG",
										"Restart the web server");
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
		return ActivityNavigator.getInstance().onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return ActivityNavigator.getInstance().onOptionsItemSelected(item);
	}
}