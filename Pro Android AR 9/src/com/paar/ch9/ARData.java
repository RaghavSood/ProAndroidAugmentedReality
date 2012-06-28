package com.paar.ch9;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.location.Location;
import android.util.Log;

public abstract class ARData {
    private static final String TAG = "ARData";
	private static final Map<String,Marker> markerList = new ConcurrentHashMap<String,Marker>();
    private static final List<Marker> cache = new CopyOnWriteArrayList<Marker>();
    private static final AtomicBoolean dirty = new AtomicBoolean(false);
    private static final float[] locationArray = new float[3];
    
    public static final Location hardFix = new Location("ATL");
    static {
        hardFix.setLatitude(0);
        hardFix.setLongitude(0);
        hardFix.setAltitude(1);
    }
    
    private static final Object radiusLock = new Object();
    private static float radius = new Float(20);
    private static String zoomLevel = new String();
    private static final Object zoomProgressLock = new Object();
    private static int zoomProgress = 0;
    private static Location currentLocation = hardFix;
    private static Matrix rotationMatrix = new Matrix();
    private static final Object azimuthLock = new Object();
    private static float azimuth = 0;
    private static final Object pitchLock = new Object();
    private static float pitch = 0;
    private static final Object rollLock = new Object();
    private static float roll = 0;

    public static void setZoomLevel(String zoomLevel) {
    	if (zoomLevel==null) throw new NullPointerException();
    	
    	synchronized (ARData.zoomLevel) {
    	    ARData.zoomLevel = zoomLevel;
    	}
    }
    
    public static void setZoomProgress(int zoomProgress) {
        synchronized (ARData.zoomProgressLock) {
            if (ARData.zoomProgress != zoomProgress) {
                ARData.zoomProgress = zoomProgress;
                if (dirty.compareAndSet(false, true)) {
                    Log.v(TAG, "Setting DIRTY flag!");
                    cache.clear();
                }
            }
        }
    }
    
    public static void setRadius(float radius) {
        synchronized (ARData.radiusLock) {
            ARData.radius = radius;
        }
    }

    public static float getRadius() {
        synchronized (ARData.radiusLock) {
            return ARData.radius;
        }
    }

    public static void setCurrentLocation(Location currentLocation) {
    	if (currentLocation==null) throw new NullPointerException();
    	
    	Log.d(TAG, "current location. location="+currentLocation.toString());
    	synchronized (currentLocation) {
    	    ARData.currentLocation = currentLocation;
    	}
        onLocationChanged(currentLocation);
    }
    
    public static Location getCurrentLocation() {
        synchronized (ARData.currentLocation) {
            return ARData.currentLocation;
        }
    }

    public static void setRotationMatrix(Matrix rotationMatrix) {
        synchronized (ARData.rotationMatrix) {
            ARData.rotationMatrix = rotationMatrix;
        }
    }

    public static Matrix getRotationMatrix() {
        synchronized (ARData.rotationMatrix) {
            return rotationMatrix;
        }
    }
    
    public static List<Marker> getMarkers() {
        if (dirty.compareAndSet(true, false)) {
            Log.v(TAG, "DIRTY flag found, resetting all marker heights to zero.");
            for(Marker ma : markerList.values()) {
                ma.getLocation().get(locationArray);
                locationArray[1]=ma.getInitialY();
                ma.getLocation().set(locationArray);
            }

            Log.v(TAG, "Populating the cache.");
            List<Marker> copy = new ArrayList<Marker>();
            copy.addAll(markerList.values());
            Collections.sort(copy,comparator);
            cache.clear();
            cache.addAll(copy);
        }
        return Collections.unmodifiableList(cache);
    }

    public static void setAzimuth(float azimuth) {
        synchronized (azimuthLock) {
            ARData.azimuth = azimuth;
        }
    }

    public static float getAzimuth() {
        synchronized (azimuthLock) {
            return ARData.azimuth;
        }
    }

    public static void setPitch(float pitch) {
        synchronized (pitchLock) {
            ARData.pitch = pitch;
        }
    }

    public static float getPitch() {
        synchronized (pitchLock) {
            return ARData.pitch;
        }
    }

    public static void setRoll(float roll) {
        synchronized (rollLock) {
            ARData.roll = roll;
        }
    }

    public static float getRoll() {
        synchronized (rollLock) {
            return ARData.roll;
        }
    }
    
    private static final Comparator<Marker> comparator = new Comparator<Marker>() {
        public int compare(Marker arg0, Marker arg1) {
            return Double.compare(arg0.getDistance(),arg1.getDistance());
        }
    };

    public static void addMarkers(Collection<Marker> markers) {
    	if (markers==null) throw new NullPointerException();

    	if (markers.size()<=0) return;
    	
    	Log.d(TAG, "New markers, updating markers. new markers="+markers.toString());
    	for(Marker marker : markers) {
    	    if (!markerList.containsKey(marker.getName())) {
    	        marker.calcRelativePosition(ARData.getCurrentLocation());
    	        markerList.put(marker.getName(),marker);
    	    }
    	}

    	if (dirty.compareAndSet(false, true)) {
    	    Log.v(TAG, "Setting DIRTY flag!");
    	    cache.clear();
    	}
    }
    
    private static void onLocationChanged(Location location) {
        Log.d(TAG, "New location, updating markers. location="+location.toString());
        for(Marker ma: markerList.values()) {
            ma.calcRelativePosition(location);
        }

        if (dirty.compareAndSet(false, true)) {
            Log.v(TAG, "Setting DIRTY flag!");
            cache.clear();
        }
    }
}