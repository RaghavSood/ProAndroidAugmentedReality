package com.paar.ch8;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ModelChooser extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AssetManager am = getAssets();
		Vector<Item> models = new Vector<Item>();
		Item item = new Item();
		item.text = getResources().getString(R.string.choose_a_model);
		item.type = Item.TYPE_HEADER;
		models.add(item);
		
		try {
			String[] modelFiles = am.list("models");
			List<String> modelFilesList = Arrays.asList(modelFiles);
			for (int i = 0; i < modelFiles.length; i++) {
				String currFileName = modelFiles[i];
				if(currFileName.endsWith(".obj")) { 
					item = new Item();
					String trimmedFileName = currFileName.substring(0,currFileName.lastIndexOf(".obj"));
					item.text = trimmedFileName;
					models.add(item);
					if(modelFilesList.contains(trimmedFileName+".jpg")) {
						InputStream is = am.open("models/"+trimmedFileName+".jpg");
						item.icon=(BitmapFactory.decodeStream(is));
					} else if(modelFilesList.contains(trimmedFileName+".png")) {
						InputStream is = am.open("models/"+trimmedFileName+".png");
						item.icon=(BitmapFactory.decodeStream(is));
					} 
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		item = new Item();
		item.text = getResources().getString(R.string.custom_model);
		item.type = Item.TYPE_HEADER;
		models.add(item);
		item = new Item();
		item.text = getResources().getString(R.string.choose_custom_model);
		item.icon = new Integer(R.drawable.open);
		models.add(item);
		item = new Item();
		item.text = getResources().getString(R.string.help);
		item.type = Item.TYPE_HEADER;
		models.add(item);
		item = new Item();
		item.text = getResources().getString(R.string.instructions);
		item.icon = new Integer(R.drawable.help);
		models.add(item);
		
		setListAdapter(new ModelChooserListAdapter(models));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Item item = (Item) this.getListAdapter().getItem(position);
		String str = item.text;
		if(str.equals(getResources().getString(R.string.choose_custom_model))) {
			//start oi file manager activity
			Intent intent = new Intent(ModelChooser.this, CheckFileManagerActivity.class);
            startActivity(intent);
		} else if(str.equals(getResources().getString(R.string.instructions))) {
			//show the instructions activity
			startActivity(new Intent(ModelChooser.this, Instructions.class));
		} else {
			//load the selected internal file
			Intent intent = new Intent(ModelChooser.this, ModelViewer.class);
            intent.putExtra("name", str+".obj");
            intent.putExtra("type", ModelViewer.TYPE_INTERNAL);
            intent.setAction(Intent.ACTION_VIEW);
            startActivity(intent);
		}
	}

	
	class ModelChooserListAdapter extends BaseAdapter{
		
		private Vector<Item> items;
		
		public ModelChooserListAdapter(Vector<Item> items) {
			this.items = items;
		}
		

		
		public int getCount() {
			return items.size();
		}

		
		public Object getItem(int position) {
			return items.get(position);
		}

		
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public int getViewTypeCount() {
			//normal items, and the header
			return 2;
		}
		
		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}
		
		@Override
		public boolean isEnabled(int position) {
			return !(items.get(position).type==Item.TYPE_HEADER);
		}
		
		@Override
		public int getItemViewType(int position) {
			return items.get(position).type;
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			Item item = items.get(position);
            if (v == null) {
            	LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            	switch(item.type) {            	
            	case Item.TYPE_HEADER:            		
                    v = vi.inflate(R.layout.list_header, null);
            		break;
            	case Item.TYPE_ITEM:
            		v = vi.inflate(R.layout.choose_model_row, null);
            		break;
            	}                
            }   
            if(item != null) {
	            switch(item.type) {            	
	        	case Item.TYPE_HEADER: 
	        		TextView headerText = (TextView) v.findViewById(R.id.list_header_title);
	        		if(headerText != null) {
	        			headerText.setText(item.text);
	        		}
	        		break;
	        	case Item.TYPE_ITEM:
	        		Object iconImage = item.icon;
	            	ImageView icon = (ImageView) v.findViewById(R.id.choose_model_row_icon);
	            	if(icon!=null) {
	            		if(iconImage instanceof Integer) {
	            			icon.setImageResource(((Integer)iconImage).intValue());
	            		} else if(iconImage instanceof Bitmap) {
	            			icon.setImageBitmap((Bitmap)iconImage);
	            		}
	            	}
	            	TextView text = (TextView) v.findViewById(R.id.choose_model_row_text);
	            	if(text!=null)
	            		text.setText(item.text);   
	        		break;
	        	}      
            }
			return v;
		}
		
	}
	
	class Item {
		private static final int TYPE_ITEM=0;
		private static final int TYPE_HEADER=1;
		private int type = TYPE_ITEM;
		private Object icon = new Integer(R.drawable.missingimage);
		private String text;
	}
	
}