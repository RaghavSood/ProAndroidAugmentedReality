package com.paar.ch8;

import java.io.BufferedReader;
import android.graphics.Bitmap;

public abstract class BaseFileUtil {
	
	protected String baseFolder = null;

	public String getBaseFolder() {
		return baseFolder;
	}

	public void setBaseFolder(String baseFolder) {
		this.baseFolder = baseFolder;
	}

	public abstract BufferedReader getReaderFromName(String name);
	public abstract Bitmap getBitmapFromName(String name);

}