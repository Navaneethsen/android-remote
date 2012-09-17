package com.axcoto.shinjuku.sushi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.axcoto.shinjuku.maki.*;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        Button b = (Button) this.findViewById(R.id.button1);

        b.setOnClickListener(new OnClickListener(){ public void onClick(View arg0) { 
        	
        	Finder f = new Finder();
        	f.execute();
        	
        	
        	// Place code to handle Button-Click here. 
        	//Log.e(Log.VERBOSE, "Lolz. Test event listenr");
        	/* Create an Intent to start * MySecondActivity. */ 
        	//Intent i = new Intent( MainActivity.this, DeviceActivity.class); 
        	/* Send intent to the OS to make * it aware that we want to start * MySecondActivity as a SubActivity. */ 
        	//startActivityForResult(i, 0x1337);
        	
        	
        }});
        
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
