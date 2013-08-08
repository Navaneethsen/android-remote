package com.ceenee.q.hd;

import java.io.IOException;
import java.util.ArrayList;

import com.ceenee.maki.Finder;
import com.ceenee.maki.MyLog;
import com.ceenee.q.hd.R;
import com.ceenee.remote.Remote;

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

public class ItemAdapter extends ArrayAdapter<DeviceItem> {
	// declaring our ArrayList of items
		private ArrayList<DeviceItem> objects;
		private String hostIp;

		/* here we must override the constructor for ArrayAdapter
		* the only variable we care about now is ArrayList<Item> objects,
		* because it is the list of objects we want to display.
		*/
		public ItemAdapter(Context context, int textViewResourceId, ArrayList<DeviceItem> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
		}

		public boolean connectTest(String i) {
			try {
				return Remote.getInstance().testConnection(i);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		/*
		 * we are overriding the getView method here - this is what defines how each
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

			/*
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
				MyLog.i("MAKI:: DEVICE IP: ", i.getIp());
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
						MyLog.i("MAKI:: CONNECTED", "Connected to this device" + i.getIp());
						ib.setImageResource(R.drawable.greencheck);
						ib.setVisibility(View.VISIBLE);
					}
					else {						
						ib.setImageResource(R.drawable.graycheck);
						ib.setVisibility(View.VISIBLE);
						MyLog.i("MAKI:: NOT CONNECTED", "Lost Connection to this device" + i.getIp());
					}
				}
				//					else {
//					ib.setVisibility(View.INVISIBLE);
//					iv.setImageResource(R.drawable.glyphicons_164_iphone_transfer);
//				}

				// check to see if each individual textview is null.
				// if not, assign some text!
				if (tt != null){
					tt.setText(itemIp);
				}
			}
			notifyDataSetChanged();
			// the view must be returned to our activity
			return v;

		}
}