package com.paar.ch6;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ASimpleAppUsingARActivity extends Activity {
	SurfaceView cameraPreview;
	SurfaceHolder previewHolder;
	Camera camera;
	boolean inPreview;
	
	final static String TAG = "PAAR";	
	SensorManager sensorManager;
	
	int orientationSensor;
	float headingAngle;
	float pitchAngle;
	float rollAngle;
	
	int accelerometerSensor;
	float xAxis;
	float yAxis;
	float zAxis;
	
	LocationManager locationManager;
	double latitude;
	double longitude;
	double altitude;
	
	TextView xAxisValue;
	TextView yAxisValue;
	TextView zAxisValue;
	TextView headingValue;
	TextView pitchValue;
	TextView rollValue;
	TextView altitudeValue;
	TextView latitudeValue;
	TextView longitudeValue;
	
	Button button;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, locationListener);
        
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        orientationSensor = Sensor.TYPE_ORIENTATION;
        accelerometerSensor = Sensor.TYPE_ACCELEROMETER;
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(orientationSensor), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(accelerometerSensor), SensorManager.SENSOR_DELAY_NORMAL);
        
        inPreview = false;
        
        cameraPreview = (SurfaceView)findViewById(R.id.cameraPreview);
        previewHolder = cameraPreview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        xAxisValue = (TextView) findViewById(R.id.xAxisValue);
        yAxisValue = (TextView) findViewById(R.id.yAxisValue);
        zAxisValue = (TextView) findViewById(R.id.zAxisValue);
        headingValue = (TextView) findViewById(R.id.headingValue);
        pitchValue = (TextView) findViewById(R.id.pitchValue);
        rollValue = (TextView) findViewById(R.id.rollValue);
        altitudeValue = (TextView) findViewById(R.id.altitudeValue);
        longitudeValue = (TextView) findViewById(R.id.longitudeValue);
        latitudeValue = (TextView) findViewById(R.id.latitudeValue);
        button = (Button) findViewById(R.id.helpButton);
        button.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
              showHelp();
            }
        });
    }
    
    LocationListener locationListener = new LocationListener() {
    	public void onLocationChanged(Location location) {
    		latitude = location.getLatitude();
    		longitude = location.getLongitude();
    		altitude = location.getAltitude();
    		
    		Log.d(TAG, "Latitude: " + String.valueOf(latitude));
    		Log.d(TAG, "Longitude: " + String.valueOf(longitude));
    		Log.d(TAG, "Altitude: " + String.valueOf(altitude));
    		
    		latitudeValue.setText(String.valueOf(latitude));
    		longitudeValue.setText(String.valueOf(longitude));
    		altitudeValue.setText(String.valueOf(altitude));
    	}

		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
    };
    
    public void launchFlatBack() {
    	Intent flatBackIntent = new Intent(this, FlatBack.class);
    	startActivity(flatBackIntent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.help:
        	showHelp();
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void showHelp() {
    	final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.help);
        dialog.setTitle("Help");
        dialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

        //set up text
        TextView text = (TextView) dialog.findViewById(R.id.TextView01);
        text.setText(R.string.help);


        //set up button
        Button button = (Button) dialog.findViewById(R.id.Button01);
        button.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
                dialog.cancel();
            }
        });
        //now that the dialog is set up, it's time to show it    
        dialog.show();
    }
    
    final SensorEventListener sensorEventListener = new SensorEventListener() {
    	public void onSensorChanged(SensorEvent sensorEvent) {
    		if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION)
    		{
    			headingAngle = sensorEvent.values[0];
    			pitchAngle = sensorEvent.values[1];
    			rollAngle = sensorEvent.values[2];
    			
    			Log.d(TAG, "Heading: " + String.valueOf(headingAngle));
    			Log.d(TAG, "Pitch: " + String.valueOf(pitchAngle));
    			Log.d(TAG, "Roll: " + String.valueOf(rollAngle));
    			
    			headingValue.setText(String.valueOf(headingAngle));
    			pitchValue.setText(String.valueOf(pitchAngle));
    			rollValue.setText(String.valueOf(rollAngle));	
    			
    			if (pitchAngle < 7 && pitchAngle > -7 && rollAngle < 7 && rollAngle > -7)
    			{
    				launchFlatBack();
    			}
    		}
    		
    		else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
    		{
    			xAxis = sensorEvent.values[0];
    			yAxis = sensorEvent.values[1];
    			zAxis = sensorEvent.values[2];
    			
    			Log.d(TAG, "X Axis: " + String.valueOf(xAxis));
    			Log.d(TAG, "Y Axis: " + String.valueOf(yAxis));
    			Log.d(TAG, "Z Axis: " + String.valueOf(zAxis));
    			
    			xAxisValue.setText(String.valueOf(xAxis));
    			yAxisValue.setText(String.valueOf(yAxis));
    			zAxisValue.setText(String.valueOf(zAxis));
    		}
    	}
    	
    	public void onAccuracyChanged (Sensor senor, int accuracy) {
    		//Not used
    	}
    };
    
    @Override
    public void onResume() {
      super.onResume();
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, locationListener);
      sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(orientationSensor), SensorManager.SENSOR_DELAY_NORMAL);
      sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(accelerometerSensor), SensorManager.SENSOR_DELAY_NORMAL);
      //Camera camera;
    }
      
    @Override
    public void onPause() {
      if (inPreview) {
        camera.stopPreview();
      }
      locationManager.removeUpdates(locationListener);
      sensorManager.unregisterListener(sensorEventListener);
      if (camera != null)
      {
    	  camera.release();
    	  camera=null;
      }
      inPreview=false;
            
      super.onPause();
    }
 
    @Override
    public void onDestroy() {
    	camera.release();
        camera=null;
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
    		if (camera == null) {
    	        camera = Camera.open();
    		}
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
    		if (camera != null) {
    	        camera.stopPreview();
    	        camera.setPreviewCallback(null);
    	        camera.release();
    	        camera = null;
    	    }
    	}
    	
    };
}