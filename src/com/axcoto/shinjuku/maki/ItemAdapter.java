package com.axcoto.shinjuku.maki;

import java.io.IOException;
import java.util.ArrayList;

import com.axcoto.shinjuku.sushi.R;
import com.axcoto.shinjuku.sushi.R.drawable;
import com.axcoto.shinjuku.sushi.R.id;
import com.axcoto.shinjuku.sushi.R.layout;

import android.content.ClipData.Item;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author dphan
 *
 */
public class ItemAdapter extends ArrayAdapter<DeviceItem> {
		private ArrayList<DeviceItem> objects;
		private String hostIp;	//the ip remembered when connected
		
		/**Here we must override the constructor for ArrayAdapter
		 * the only variable we care about now is ArrayList<Item> objects,
		 * because it is the list of objects we want to display.
		 */
		public ItemAdapter(Context context, int textViewResourceId, ArrayList<DeviceItem> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
		}
		
		/**
		 * Check connection whether the current ip is the one being connected
		 * @param i the current hostIp being connected
		 * @return
		 */
		public boolean connectTest(String i) {
			try {
				return Remote.getInstance().testConnection(i);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		/**
		 * We are overriding the getView method here - this is what defines how each
		 * list item will look.
		 */
		public View getView(int position, View convertView, ViewGroup parent){

			// assign the view we are converting to a local variable
			View v = convertView;
			Remote r = Remote.getInstance();
			hostIp = r.getIp();

			// first check to see if the view is null. if so, we have to inflate it.
			// to inflate it basically means to render, or show, the view.
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.device_item, null);
			}

			/**
			 * Recall that the variable position is sent in as an argument to this method.
			 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
			 * iterates through the list we sent it)
			 * 
			 * Therefore, i refers to the current Item object.
			 */
			DeviceItem i = objects.get(position);

			if (i != null) {
				TextView tt = (TextView) v.findViewById(R.id.ipLabel);
				String itemIp = i.getIp();
				Log.i("MAKI:: DEVICE IP: ", i.getIp());
				final ImageButton ib = (ImageButton) v.findViewById(R.id.imageDeviceStatus);
				ib.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {		//disconnect whenever click on
						Remote.getInstance().disConnect();
//						ImageButton ib = (ImageButton) v;
						ib.setVisibility(View.INVISIBLE);
						//ImageView iv = findViewById(R.id.imageDeviceStatus);
//						iv.setImageResource(R.drawable.glyphicons_164_iphone_transfer);
					}
				});
				if (!i.isConnected()) {
					ib.setVisibility(View.INVISIBLE);
				}
				else {
					if (connectTest(itemIp)) {						
						Log.i("MAKI:: CONNECTED", "Connected to this device" + i.getIp());
						ib.setImageResource(R.drawable.greencheck);
						ib.setVisibility(View.VISIBLE);
					}
					else {						
						ib.setImageResource(R.drawable.graycheck);
						ib.setVisibility(View.VISIBLE);
						Log.i("MAKI:: NOT CONNECTED", "Lost Connection to this device" + i.getIp());
					}
				}

				// check to see if each individual textview is null. if not, assign some text!
				if (tt != null){
					tt.setText(itemIp);
				}
			}
			notifyDataSetChanged();
			// the view must be returned to our activity
			return v;

		}
}