package com.axcoto.shinjuku.sushi;
import java.util.ArrayList;

import android.content.ClipData.Item;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.axcoto.shinjuku.maki.Finder;

public class DeviceActivity extends RootActivity {
	
	
	private ListView listDevice;
	private ItemAdapter deviceAdapter;
	public ArrayList<DeviceItem> devices;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);  
		// Button b = (Button) findViewById()
		listDevice = (ListView) findViewById(R.id.list_device);
		
		listDevice.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				  Log.e("DEVICE: CLICKED", "Click ListItem Number " + position);
			    Toast.makeText(getApplicationContext(),
			      "Click ListItem Number " + position, Toast.LENGTH_LONG)
			      .show();
			  }
			});
		
		String[] arrs = {"192.168.1.111", "192.168.1.112", "192.168.1.113", "192.168.1.114","192.168.1.115"};
		devices = new ArrayList<DeviceItem>();
		devices.add(new DeviceItem("19"));
		devices.add(new DeviceItem("20"));
		devices.add(new DeviceItem("21"));
		devices.add(new DeviceItem("22"));
		
		deviceAdapter = new ItemAdapter(this, R.layout.device_item, devices);
		//this.deviceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, devices);
		deviceAdapter.notifyDataSetChanged();
		
		listDevice.setAdapter(deviceAdapter);	
	}

	
	public void scanDevice(View view) {
		
		// here we are defining our runnable thread.
        Runnable viewParts = new Runnable() {
        	public void run(){
        		handler.sendEmptyMessage(0);
        	}
        };

        // here we call the thread we just defined - it is sent to the handler below.
        Thread thread =  new Thread(null, viewParts, "DeviceScanningBackground");
        thread.start();
	}

	 private Handler handler = new Handler()
	 {
		public void handleMessage(Message msg)
		{
		
    		Log.e("MAKI::FINDER", "Horay. Started to scan devices");
    		
    		deviceAdapter.clear();
    		deviceAdapter.add(new DeviceItem("23"));
    		deviceAdapter.add(new DeviceItem("25"));
    		deviceAdapter.add(new DeviceItem("2"));
    		
    		//this.deviceAdapter.notifyDataSetChanged();
    		//deviceAdapter = new ArrayAdapter<String>(this, R.layout.device_item, R.id.textView1, devices);
    		//this.deviceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, devices);
    		deviceAdapter.notifyDataSetChanged();        		
    		//listDevice.setAdapter(deviceAdapter);	
    		Finder f = new Finder();
    		//deviceAdapter.clear();
    		//deviceAdapter.add("this is a new one");
    		 f.execute();

		}
	};
	
}



