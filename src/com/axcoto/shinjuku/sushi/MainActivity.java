package com.axcoto.shinjuku.sushi;

import java.io.IOException;

import android.content.res.Resources;
import android.os.Bundle;
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
        
        ImageButton b = (ImageButton) this.findViewById(R.id.cmd_power);
       
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
				r.execute("DOWN");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
    public void onRemoteClick(View v) {
    	this.execute("test");
    }
    
}
