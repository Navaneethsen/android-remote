package com.axcoto.shinjuku.sushi;

import java.io.IOException;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;

import com.axcoto.shinjuku.maki.Remote;

public class MainActivity extends RootActivity implements OnGestureListener{
	public String remote;

	private GestureDetector gestureScanner;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Resources res = getResources();
        
        RemoteKeyButton b = (RemoteKeyButton) this.findViewById(R.id.cmd_power);
        Log.e("SUSHI:: KEYNAME", "NUT POWER UP IS " .concat(b.getKeyName()));
  
		gestureScanner = new GestureDetector(this);
		
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
    
    

	
	@Override
	 
    public boolean onTouchEvent(MotionEvent me)
 
    {
 
        return gestureScanner.onTouchEvent(me);
 
    }
 
   
 
    @Override
 
    public boolean onDown(MotionEvent e)
 
    {
 
        Log.e("SUSHI:: DEVICE", "-" + "DOWN" + "-");
 
        return true;
 
    }
 
   
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.e("SUSHI: FLING", "Left Swipe");
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            	Log.e("SUSHI: FLING", "Right Swipe");
            }
        } catch (Exception e) {
            // nothing
        }
        return false;
    }
 
   
 
    @Override
 
    public void onLongPress(MotionEvent e)
 
    {
 
        Log.e("SUSHI:: DEVICE", "-" + "LONG PRESS" + "-");
 
    }
 
   
 
    @Override
 
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
 
    {
//    	if (distanceX>0) {
//    		this.execute("right");
//    		return true;
//    	}
//    	
//    	if (distanceX<0) {
//    		this.execute("left");
//    		return true;
//    	}
//    	
    	String direction = "";
    	if (distanceY>0) {
    		direction = "down";
    		this.execute("down");
    		return true;
    	}
    	if (distanceY<00) {
    		direction = "up";
    		this.execute("up");
    		return true;
    	}
    	      Log.e("SUSHI:: DEVICE", "-" + "SCROLL" + "-");
 
        return false;
 
    }
 
   
 
    @Override
 
    public void onShowPress(MotionEvent e)
 
    {
 
        Log.e("SUSHI:: DEVICE", "-" + "SHOW PRESS" + "-");
 
    }    
 
   
 
    @Override  
 
    public boolean onSingleTapUp(MotionEvent e)    
 
    {
 
        Log.e("SUSHI:: DEVICE", "-" + "SINGLE TAP UP" + "-");
 
        return true;
 
    }
}
