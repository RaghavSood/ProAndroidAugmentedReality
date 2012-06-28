package com.paar.ch8;

import java.io.Serializable;
import java.nio.FloatBuffer;

import android.graphics.Bitmap;



public class Material implements Serializable {

	private float[] ambientlightArr = {0.2f, 0.2f, 0.2f, 1.0f};
	private float[] diffuselightArr = {0.8f, 0.8f, 0.8f, 1.0f};
	private float[] specularlightArr = {0.0f, 0.0f, 0.0f, 1.0f};

	public transient FloatBuffer ambientlight = MemUtil.makeFloatBuffer(4);
	public transient FloatBuffer diffuselight = MemUtil.makeFloatBuffer(4);
	public transient FloatBuffer specularlight = MemUtil.makeFloatBuffer(4);
	public float shininess = 0;
	public int STATE = STATE_DYNAMIC;
    public static final int STATE_DYNAMIC = 0;
    public static final int STATE_FINALIZED = 1;
	
	private transient Bitmap texture = null;
	private String bitmapFileName = null;
	private transient BaseFileUtil fileUtil = null;
	
	private String name = "defaultMaterial";
	
	public Material() {
		
	}
	
	public Material(String name) {
		this.name = name;
		//fill with default values
		ambientlight.put(new float[]{0.2f, 0.2f, 0.2f, 1.0f});
		ambientlight.position(0);
		diffuselight.put(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
		diffuselight.position(0);
		specularlight.put(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
		specularlight.position(0);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public void setFileUtil(BaseFileUtil fileUtil) {
		this.fileUtil = fileUtil;
	}

	public String getBitmapFileName() {
		return bitmapFileName;
	}

	public void setBitmapFileName(String bitmapFileName) {
		this.bitmapFileName = bitmapFileName;
	}

	public void setAmbient(float[] arr) {
		ambientlightArr = arr;
	}
	
	public void setDiffuse(float[] arr) {
		diffuselightArr = arr;
	}
	
	public void setSpecular(float[] arr) {
		specularlightArr = arr;
	}
	
	public void setShininess(float ns) {
		shininess = ns;
	}
	
	public void setAlpha(float alpha) {
		ambientlight.put(3, alpha);
		diffuselight.put(3, alpha);
		specularlight.put(3, alpha);
	}
	
	public Bitmap getTexture() {
		return texture;
	}

	public void setTexture(Bitmap texture) {
		this.texture = texture;
	}
	
	public boolean hasTexture() {
		if(STATE == STATE_DYNAMIC) 
			return this.bitmapFileName != null;
		else if(STATE == STATE_FINALIZED)
				return this.texture != null;
		else 
			return false;
	}
	

	public void finalize() {
		ambientlight = MemUtil.makeFloatBuffer(ambientlightArr);
		diffuselight = MemUtil.makeFloatBuffer(diffuselightArr);
		specularlight = MemUtil.makeFloatBuffer(specularlightArr);
		ambientlightArr = null;
		diffuselightArr = null;
		specularlightArr = null;
		if(fileUtil != null && bitmapFileName != null) {
			texture = fileUtil.getBitmapFromName(bitmapFileName);
		}
	}
	

	
}
