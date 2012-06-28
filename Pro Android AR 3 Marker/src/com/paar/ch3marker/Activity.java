package com.paar.ch3marker;

import android.os.Bundle;
import android.util.Log;
import edu.dhbw.andar.ARObject;
import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.exceptions.AndARException;

public class Activity extends AndARActivity {

	private ARObject someObject;
	private ARToolkit artoolkit;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		CustomRenderer renderer = new CustomRenderer();
		setNonARRenderer(renderer);
		try {
			artoolkit = getArtoolkit();

			
			someObject = new CustomObject1
			("test", "marker_at16.patt", 80.0, new double[]{0,0});
			artoolkit.registerARObject(someObject);
			
			someObject = new CustomObject2
			("test", "marker_peace16.patt", 80.0, new double[]{0,0});
			artoolkit.registerARObject(someObject);
			
			someObject = new CustomObject3
			("test", "marker_rupee16.patt", 80.0, new double[]{0,0});
			artoolkit.registerARObject(someObject);
			
			someObject = new CustomObject4
			("test", "marker_hand16.patt", 80.0, new double[]{0,0});
			artoolkit.registerARObject(someObject);
			
		} catch (AndARException ex){
			System.out.println("");
		}		
		startPreview();
	}
	
	public void uncaughtException(Thread thread, Throwable ex) {
		Log.e("AndAR EXCEPTION", ex.getMessage());
		finish();
	}
}