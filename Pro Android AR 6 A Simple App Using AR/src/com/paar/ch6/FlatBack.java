package com.paar.ch6;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class FlatBack extends MapActivity{
	private MapView mapView;
    private MyLocationOverlay myLocationOverlay;
	final static String TAG = "PAAR";	
	SensorManager sensorManager;
	
	int orientationSensor;
	float headingAngle;
	float pitchAngle;
	float rollAngle;
/** Called when the activity is first created. */
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // main.xml contains a MapView
    setContentView(R.layout.map); 
    
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
			
			Log.d(TAG, "Heading: " + String.valueOf(headingAngle));
			Log.d(TAG, "Pitch: " + String.valueOf(pitchAngle));
			Log.d(TAG, "Roll: " + String.valueOf(rollAngle));
			
			if (pitchAngle > 7 || pitchAngle < -7 || rollAngle > 7 || rollAngle < -7)
			{
				launchCameraView();
			}
		}
}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
};

public void launchCameraView() {
	finish();
	//Intent cameraView = new Intent(this, ASimpleAppUsingARActivity.class);
	//startActivity(cameraView);
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
