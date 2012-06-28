package com.paar.ch8;

import java.util.Iterator;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

public class Renderer implements GLSurfaceView.Renderer {
	
	private final Vector<Model3D> models;
	private final Vector3D cameraPosition = new Vector3D(0, 3, 50);
	
	long frame,time,timebase=0;
	
	public Renderer(Vector<Model3D> models) {
		this.models = models;
	}
	
	public void addModel(Model3D model) {
		if(!models.contains(model)) {
			models.add(model);
		}
	}
	
	
	public void onDrawFrame(GL10 gl) {
		if(ModelViewer.DEBUG) {
			frame++;
			time=System.currentTimeMillis();
			if (time - timebase > 1000) {
				Log.d("fps: ", String.valueOf(frame*1000.0f/(time-timebase)));
			 	timebase = time;		
				frame = 0;
			}
		}
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, cameraPosition.x, cameraPosition.y, cameraPosition.z,
				0, 0, 0, 0, 1, 0);
		for (Iterator<Model3D> iterator = models.iterator(); iterator.hasNext();) {
			Model3D model = iterator.next();
			model.draw(gl);
		}
	}

	
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0,0,width,height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();  
        GLU.gluPerspective(gl, 45.0f, ((float)width)/height, 0.11f, 100f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();        
	}

	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(1,1,1,1);
		
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);

		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glDisable(GL10.GL_COLOR_MATERIAL);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_LIGHTING);
		float[] ambientlight = {.6f, .6f, .6f, 1f};
		float[] diffuselight = {1f, 1f, 1f, 1f};
		float[] specularlight = {1f, 1f, 1f, 1f};
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, MemUtil.makeFloatBuffer(ambientlight));
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, MemUtil.makeFloatBuffer(diffuselight));
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, MemUtil.makeFloatBuffer(specularlight));
		gl.glEnable(GL10.GL_LIGHT0);
		
		for (Iterator<Model3D> iterator = models.iterator(); iterator.hasNext();) {
			Model3D model = iterator.next();
			model.init(gl);
		}
		
	}

}