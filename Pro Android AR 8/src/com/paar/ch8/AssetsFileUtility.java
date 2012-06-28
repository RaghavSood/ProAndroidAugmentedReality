package com.paar.ch8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AssetsFileUtility extends BaseFileUtil {
	
	private AssetManager am;
	
	public AssetsFileUtility(AssetManager am) {
		this.am = am;
	}

	@Override
	public Bitmap getBitmapFromName(String name) {
		InputStream is = getInputStreamFromName(name);
		return (is==null)?null:BitmapFactory.decodeStream(is);
	}

	@Override
	public BufferedReader getReaderFromName(String name) {
		InputStream is = getInputStreamFromName(name);
		return (is==null)?null:new BufferedReader(new InputStreamReader(is));
	}
	
	private InputStream getInputStreamFromName(String name) {
		InputStream is;
		if(baseFolder != null) {
			try {
				is = am.open(baseFolder+name);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			try {
				is = am.open(name);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return is;
	}

}