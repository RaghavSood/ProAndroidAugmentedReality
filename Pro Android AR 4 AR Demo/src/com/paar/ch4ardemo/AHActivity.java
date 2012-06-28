package com.paar.ch4ardemo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AHActivity extends Activity {
	float[] aValues = new float[3];
	float[] mValues = new float[3];
	HorizonView horizonView;
	SensorManager sensorManager;
	LocationManager locationManager;
	
	Button updateAltitudeButton;
	TextView altitudeValue;
	
	SurfaceView cameraPreview;
	SurfaceHolder previewHolder;
	Camera camera;
	boolean inPreview;
	
	final static String TAG = "PAAR";	
	
	double currentAltitude;
	double pitch;
	double newAltitude;
	double changeInAltitude;
	double thetaTan;
	
	@Override
	public void onCreate(Bundle icicle) {
	  super.onCreate(icicle); 
	  setContentView(R.layout.main);
	  
      inPreview = false;
      
      cameraPreview = (SurfaceView)findViewById(R.id.cameraPreview);
      previewHolder = cameraPreview.getHolder();
      previewHolder.addCallback(surfaceCallback);
      previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);	
	  
      altitudeValue = (TextView) findViewById(R.id.altitudeValue);
      
      updateAltitudeButton = (Button) findViewById(R.id.altitudeUpdateButton);
      updateAltitudeButton.setOnClickListener(new OnClickListener() {

		public void onClick(View arg0) {
			updateAltitude();
		}
    	  
      });
      
      locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, locationListener);
      
	  horizonView = (HorizonView)this.findViewById(R.id.horizonView);
	  sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
	  updateOrientation(new float[] {0, 0, 0});
	}
	
    LocationListener locationListener = new LocationListener() {
    	public void onLocationChanged(Location location) {
    		currentAltitude = location.getAltitude();
    	}
    	
		public void onProviderDisabled(String arg0) {
			//Not Used
		}

		public void onProviderEnabled(String arg0) {
			//Not Used
		}

		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			//Not Used
		}
    };
    
	public void updateAltitude() {
		int time = 300;
		float speed = 4.5f;
		
		double distanceMovedParallelToGround = (speed*time)*0.3048;
		if(pitch != 0 && currentAltitude != 0)
		{
			thetaTan = Math.tan(pitch);
			changeInAltitude = thetaTan * distanceMovedParallelToGround;
			newAltitude = currentAltitude + changeInAltitude;
			altitudeValue.setText(String.valueOf(newAltitude));
		}
		else
		{
			altitudeValue.setText("Try Again");
		}
	}
	
    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
    	Camera.Size result=null;

    	for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
    		if (size.width<=width && size.height<=height) {
    			if (result==null) {
    				result=size;
    			}
    			else {
    				int resultArea=result.width*result.height;
    				int newArea=size.width*size.height;

    				if (newArea>resultArea) {
    					result=size;
    				}
    			}
    		}
    	}

    	return(result);
    }
    
    SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
    	public void surfaceCreated(SurfaceHolder holder) {
    		try {
    			camera.setPreviewDisplay(previewHolder);	
    		}
    		catch (Throwable t) {
    			Log.e(TAG, "Exception in setPreviewDisplay()", t);
    		}
    	}
    	
    	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
    		Camera.Parameters parameters=camera.getParameters();
    		Camera.Size size=getBestPreviewSize(width, height, parameters);

    		if (size!=null) {
    			parameters.setPreviewSize(size.width, size.height);
    			camera.setParameters(parameters);
    			camera.startPreview();
    			inPreview=true;
    		}
	}
    	
    	public void surfaceDestroyed(SurfaceHolder holder) {
    		// not used
    	}
    	
    };
    private void updateOrientation(float[] values) {
      if (horizonView!= null) {
        horizonView.setBearing(values[0]);
        horizonView.setPitch(values[1]);
   	    horizonView.setRoll(-values[2]);
   	    horizonView.invalidate();
   	  }
   	}
    
    private float[] calculateOrientation() {
      float[] values = new float[3];
      float[] R = new float[9];
      float[] outR = new float[9];

      SensorManager.getRotationMatrix(R, null, aValues, mValues);
      SensorManager.remapCoordinateSystem(R, 
                                          SensorManager.AXIS_X, 
                                          SensorManager.AXIS_Z, 
                                          outR);

      SensorManager.getOrientation(outR, values);

      values[0] = (float) Math.toDegrees(values[0]);
      values[1] = (float) Math.toDegrees(values[1]);
      values[2] = (float) Math.toDegrees(values[2]);
      
      pitch = values[1];
      
      return values;
    }
    
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
      public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
          aValues = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
          mValues = event.values;

        updateOrientation(calculateOrientation());
      }

      public void onAccuracyChanged(Sensor sensor, int accuracy) {}
   	};
   	
   	@Override
   	protected void onResume() {
   	  super.onResume();

   	  Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
   	  Sensor magField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

   	  sensorManager.registerListener(sensorEventListener, 
   	                                 accelerometer, 
   	                                 SensorManager.SENSOR_DELAY_FASTEST);
   	  sensorManager.registerListener(sensorEventListener, 
   	                                 magField,
   	                                 SensorManager.SENSOR_DELAY_FASTEST);
   	camera=Camera.open();
   	}

    @Override
    public void onPause() {
      if (inPreview) {
        camera.stopPreview();
      }
      sensorManager.unregisterListener(sensorEventListener);
      camera.release();
      camera=null;
      inPreview=false;
            
      super.onPause();
    }
}