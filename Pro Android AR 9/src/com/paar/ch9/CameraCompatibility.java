package com.paar.ch9;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.Display;
import android.view.WindowManager;

public class CameraCompatibility {
	private static Method getSupportedPreviewSizes = null;
	private static Method mDefaultDisplay_getRotation = null;
	
	static {
		initCompatibility();
	};

	private static void initCompatibility() {
		try {
			getSupportedPreviewSizes = Camera.Parameters.class.getMethod("getSupportedPreviewSizes", new Class[] { } );
			mDefaultDisplay_getRotation = Display.class.getMethod("getRotation", new Class[] { } );
		} catch (NoSuchMethodException nsme) {
		}
	}

	public static int getRotation(Activity activity) {
	     int result = 1;
	     try {
    	     Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    	     Object retObj = mDefaultDisplay_getRotation.invoke(display);
    	     if(retObj != null) result = (Integer) retObj;
	     } catch (Exception ex) {
	         ex.printStackTrace();
	     }
	     return result;
	}

	public static List<Camera.Size> getSupportedPreviewSizes(Camera.Parameters params) {
		List<Camera.Size> retList = null;

		try {
			Object retObj = getSupportedPreviewSizes.invoke(params);
			if (retObj != null) {
				retList = (List<Camera.Size>)retObj;
			}
		} catch (InvocationTargetException ite) {
			Throwable cause = ite.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			} else if (cause instanceof Error) {
				throw (Error) cause;
			} else {
				throw new RuntimeException(ite);
			}
		} catch (IllegalAccessException ie) {
			ie.printStackTrace();
		}
		return retList;
	}
}