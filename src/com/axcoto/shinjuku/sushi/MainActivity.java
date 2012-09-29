package com.axcoto.shinjuku.sushi;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends RootActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Resources res = getResources();
        
//        Button b = (Button) this.findViewById(R.id.button1);
//
//        b.setOnClickListener(new OnClickListener(){ public void onClick(View arg0) {        	
//        	Finder f = new Finder();
//        	f.execute();
//        }});
    }

}
