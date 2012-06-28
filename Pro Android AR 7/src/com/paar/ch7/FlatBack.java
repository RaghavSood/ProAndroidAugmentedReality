package com.paar.ch7;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class FlatBack extends MapActivity{
	private MapView mapView;
    private MyLocationOverlay myLocationOverlay;
	final static String TAG = "PAAR";	
	SensorManager sensorManager;
	
	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	int orientationSensor;
	float headingAngle;
	float pitchAngle;
	float rollAngle;
	String enteredAddress;
	boolean tapToSet;
/** Called when the activity is first created. */
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // main.xml contains a MapView
    setContentView(R.layout.map); 
    prefs = getSharedPreferences("PAARCH7", 0);
    editor = prefs.edit();
    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    orientationSensor = Sensor.TYPE_ORIENTATION;
    sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(orientationSensor), SensorManager.SENSOR_DELAY_NORMAL);

    // extract MapView from layout
            mapView = (MapView) findViewById(R.id.mapView);
            mapView.setBuiltInZoomControls(true);

            // create an overlay that shows our current location
            myLocationOverlay = new FixLocation(this, mapView);
            
            // add this overlay to the MapView and refresh it
            mapView.getOverlays().add(myLocationOverlay);
            mapView.postInvalidate();
            
            // call convenience method that zooms map on our location
            zoomToMyLocation();

            mapView.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View arg0, MotionEvent arg1) {

					if(tapToSet == true)
					{
					GeoPoint p = mapView.getProjection().fromPixels((int) arg1.getX(), (int) arg1.getY());

					Log.d(TAG,"Latitude:" + String.valueOf(p.getLatitudeE6()/1e6));
					Log.d(TAG,"Longitude:" + String.valueOf(p.getLongitudeE6()/1e6));
					float lat =(float) ((float) p.getLatitudeE6()/1e6);
					float lon = (float) ((float) p.getLongitudeE6()/1e6);
					editor.putFloat("SetLatitude", lat);
					editor.putFloat("SetLongitude", lon);
					editor.commit();
					return true;
					}
					return false;

				}
            	
            });

}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.map_toggle, menu);
    return true;
}

public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
    case R.id.map:
        if (mapView.isSatellite() == true) {
        	mapView.setSatellite(false);
        	mapView.setStreetView(true);
        }
        return true;
    case R.id.sat:
        if (mapView.isSatellite()==false){
        	mapView.setSatellite(true);
        	mapView.setStreetView(false);
        }
        return true;
    case R.id.both:
    	mapView.setSatellite(true);
    	mapView.setStreetView(true);
    case R.id.toggleSetDestination:
    	if(tapToSet == false)
    	{
    		tapToSet = true;
    		item.setTitle("Disable Tap to Set");
    	}
    	else if(tapToSet == true)
    	{
    		tapToSet = false;
    		item.setTitle("Enable Tap to Set");
    		mapView.invalidate();
    	}
    default:
        return super.onOptionsItemSelected(item);
    }
}

final SensorEventListener sensorEventListener = new SensorEventListener() {
	public void onSensorChanged(SensorEvent sensorEvent) {
		if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION)
		{
			headingAngle = sensorEvent.values[0];
			pitchAngle = sensorEvent.values[1];
			rollAngle = sensorEvent.values[2];
			
			//Log.d(TAG, "Heading: " + String.valueOf(headingAngle));
			//Log.d(TAG, "Pitch: " + String.valueOf(pitchAngle));
			//Log.d(TAG, "Roll: " + String.valueOf(rollAngle));
			
			if (pitchAngle > 7 || pitchAngle < -7 || rollAngle > 7 || rollAngle < -7)
			{
				launchCameraView();
			}
		}
}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}
};

public void launchCameraView() {
	finish();
}

@Override
    protected void onResume() {
            super.onResume();
            myLocationOverlay.enableMyLocation();
    }

    @Override
    protected void onPause() {
            super.onPause();
            myLocationOverlay.disableMyLocation();
    }
    

    private void zoomToMyLocation() {
            GeoPoint myLocationGeoPoint = myLocationOverlay.getMyLocation();
            if(myLocationGeoPoint != null) {
                    mapView.getController().animateTo(myLocationGeoPoint);
                    mapView.getController().setZoom(10);
            }
    }

    protected boolean isRouteDisplayed() {
            return false;
    }
}