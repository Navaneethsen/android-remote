package com.ceenee.q;

import java.io.IOException;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.TextView;

import com.ceenee.maki.MyLog;
import com.ceenee.maki.Remote;
import com.ceenee.q.*;
import com.ceenee.q.R.id;

public class RootActivity extends Activity {
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityNavigator m = ActivityNavigator.getInstance();
		m.setCurrentActivity(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return ActivityNavigator.getInstance().onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return ActivityNavigator.getInstance().onOptionsItemSelected(item);
	}
}