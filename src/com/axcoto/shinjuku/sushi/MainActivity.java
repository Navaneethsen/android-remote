package com.axcoto.shinjuku.sushi;

import java.io.IOException;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.axcoto.shinjuku.maki.Remote;

public class MainActivity extends RootActivity {
	public String remote;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Resources res = getResources();
        
        RemoteKeyButton b = (RemoteKeyButton) this.findViewById(R.id.cmd_power);
        Log.e("SUSHI:: KEYNAME", "NUT POWER UP IS " .concat(b.getKeyName()));
        
//
//        b.setOnClickListener(new OnClickListener(){ public void onClick(View arg0) {        	
//        	Finder f = new Finder();
//        	f.execute();
//        }});
    }

    public void setRemote(String remote) {
    	this.remote = remote;
    }
    
    public void execute(String command) {
		Remote r = Remote.getInstance();
		if (r.getConnected()) {
			try {
				r.execute(command);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				Log.e("SUSHI: REMOTE", "Key not found: " + command);
			}
			
		}
    }
    
    public void onRemoteClick(View v) {
    	String key = "";
    	RemoteKeyButton b = (RemoteKeyButton)v;
    	key = b.getKeyName();
        Log.e("SUSHI:: REMOTE", "PRESS " + key);
    	this.execute(key);
    }
    
}
