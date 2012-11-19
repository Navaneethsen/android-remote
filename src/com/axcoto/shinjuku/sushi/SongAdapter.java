package com.axcoto.shinjuku.sushi;

import java.util.ArrayList;

import com.axcoto.shinjuku.database.Song;

import android.content.ClipData.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class SongAdapter extends ArrayAdapter<Song> {
	// declaring our ArrayList of items
		private ArrayList<Song> objects;
		private ArrayList<Song> fitems;
		private Filter filter;
		
		/* here we must override the constructor for ArrayAdapter
		* the only variable we care about now is ArrayList<Item> objects,
		* because it is the list of objects we want to display.
		*/
		public SongAdapter(Context context, int textViewResourceId, ArrayList<Song> objects) {
			super(context, textViewResourceId, objects);			
			this.objects = objects;			
		}

		/*
		 * we are overriding the getView method here - this is what defines how each
		 * list item will look.
		 */
		public View getView(int position, View convertView, ViewGroup parent){

			// assign the view we are converting to a local variable
			View v = convertView;
			
			// first check to see if the view is null. if so, we have to inflate it.
			// to inflate it basically means to render, or show, the view.
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				v = inflater.inflate(R.layout.song_item, null);
				v = inflater.inflate(R.layout.song_item,parent, false);
			}
			if (position % 2 == 0) {
				v.setBackgroundResource(R.drawable.list_selector1);
			} 
			else {
				v.setBackgroundResource(R.drawable.list_selector2);
			}				

			/*
			 * Recall that the variable position is sent in as an argument to this method.
			 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
			 * iterates through the list we sent it)
			 * 
			 * Therefore, i refers to the current Item object.
			 */
			Song i = objects.get(position);
			
			if (i != null) {

				// This is how you obtain a reference to the TextViews.
				// These TextViews are created in the XML files we defined.

				TextView id = (TextView) v.findViewById(R.id.songId);				
				if (id != null){
					id.setText(i.getId());
				}
				
				TextView title = (TextView) v.findViewById(R.id.songTitle);
				if (title != null){
					title.setText(i.getTitle());
//					title.setSelected(true);

//					title.setOnLongClickListener(new OnLongClickListener() {
//						
//						@Override
//						public boolean onLongClick(View v) {
//							v.setSelected(true);
//							return false;
//						}
//					});								
				}		
			}
			// the view must be returned to our activity
			return v;			
		}
		
		@Override
		public Filter getFilter() {
		    if (filter == null)
		        filter = new SongFilter();

		    return filter;
		}
		
		private class SongFilter extends Filter
		{
		        @Override
		        protected FilterResults performFiltering(CharSequence constraint)
		        {   
		            FilterResults results = new FilterResults();
		            String prefix = constraint.toString().toLowerCase();

		            if (prefix == null || prefix.length() == 0)
		            {
		                ArrayList<Song> list = new ArrayList<Song>();
		                results.values = list;
		                results.count = list.size();
		            }
		            else
		            {
		                final ArrayList<Song> list = new ArrayList<Song>();
		                final ArrayList<Song> nlist = new ArrayList<Song>();
		                int count = list.size();

		                for (int i=0; i<count; i++)
		                {
		                    final Song pkmn = list.get(i);
		                    final String value = pkmn.getTitle().toLowerCase();

		                    if (value.contains(prefix))
		                    {
		                        nlist.add(pkmn);
		                    }
		                }
		                results.values = nlist;
		                results.count = nlist.size();
		            }
		            return results;
		        }

		        @SuppressWarnings("unchecked")
		        @Override
		        protected void publishResults(CharSequence constraint, FilterResults results) {
		            fitems = (ArrayList<Song>)results.values;

		            clear();
		            int count = fitems.size();
		            for (int i=0; i<count; i++)
		            {
		                Song pkmn = (Song)fitems.get(i);
		                add(pkmn);
		            }
		        }

		    }
}
