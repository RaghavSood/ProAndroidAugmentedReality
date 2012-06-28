package com.paar.ch8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.exceptions.AndARException;

public class ModelViewer extends AndARActivity implements SurfaceHolder.Callback {

	public static final int TYPE_INTERNAL = 0;

	public static final int TYPE_EXTERNAL = 1;
	
	public static final boolean DEBUG = false;

	private final int MENU_SCALE = 0;
	private final int MENU_ROTATE = 1;
	private final int MENU_TRANSLATE = 2;
	private final int MENU_SCREENSHOT = 3;
	
	private int mode = MENU_SCALE;
	

	private Model model;
	private Model model2;
	private Model model3;
	private Model model4;
	private Model model5;
	private Model3D model3d;
	private Model3D model3d2;
	private Model3D model3d3;
	private Model3D model3d4;
	private Model3D model3d5;
	private ProgressDialog waitDialog;
	private Resources res;
	
	ARToolkit artoolkit;
	
	public ModelViewer() {
		super(false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setNonARRenderer(new LightingRenderer());
		res=getResources();
		artoolkit = getArtoolkit();		
		getSurfaceView().setOnTouchListener(new TouchEventHandler());
		getSurfaceView().getHolder().addCallback(this);
	}
	
	public void uncaughtException(Thread thread, Throwable ex) {
		System.out.println("");
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_TRANSLATE, 0, res.getText(R.string.translate))
    		.setIcon(R.drawable.translate);
        menu.add(0, MENU_ROTATE, 0, res.getText(R.string.rotate))
        	.setIcon(R.drawable.rotate);
        menu.add(0, MENU_SCALE, 0, res.getText(R.string.scale))
        	.setIcon(R.drawable.scale);     
        menu.add(0, MENU_SCREENSHOT, 0, res.getText(R.string.take_screenshot))
    		.setIcon(R.drawable.screenshoticon);     
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case MENU_SCALE:
	            mode = MENU_SCALE;
	            return true;
	        case MENU_ROTATE:
	        	mode = MENU_ROTATE;
	            return true;
	        case MENU_TRANSLATE:
	        	mode = MENU_TRANSLATE;
	            return true;
	        case MENU_SCREENSHOT:
	        	new TakeAsyncScreenshot().execute();
	        	return true;
        }
        return false;
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    	super.surfaceCreated(holder);

    	if(model == null) {
			waitDialog = ProgressDialog.show(this, "", 
	                getResources().getText(R.string.loading), true);
			waitDialog.show();
			new ModelLoader().execute();
		}
    }
    
    class TouchEventHandler implements OnTouchListener {
    	
    	private float lastX=0;
    	private float lastY=0;


		public boolean onTouch(View v, MotionEvent event) {
			if(model!=null) {
				switch(event.getAction()) {
					default:
					case MotionEvent.ACTION_DOWN:
						lastX = event.getX();
						lastY = event.getY();
						break;
					case MotionEvent.ACTION_MOVE:
						float dX = lastX - event.getX();
						float dY = lastY - event.getY();
						lastX = event.getX();
						lastY = event.getY();
						if(model != null) {
							switch(mode) {
								case MENU_SCALE:
									model.setScale(dY/100.0f);
						            break;
						        case MENU_ROTATE:
						        	model.setXrot(-1*dX);
									model.setYrot(-1*dY);
						            break;
						        case MENU_TRANSLATE:
						        	model.setXpos(dY/10f);
									model.setYpos(dX/10f);
						        	break;
							}		
						}
						break;
					case MotionEvent.ACTION_CANCEL:	
					case MotionEvent.ACTION_UP:
						lastX = event.getX();
						lastY = event.getY();
						break;
				}
			}
			return true;
		}
    	
    }
    
	private class ModelLoader extends AsyncTask<Void, Void, Void> {
		
		
		private String modelName2patternName (String modelName) {
			String patternName = "android";
			
			if (modelName.equals("plant.obj")) {
				patternName = "marker_rupee16";
			} else if (modelName.equals("chair.obj")) {
				patternName = "marker_fisch16";
			} else if (modelName.equals("tower.obj")) {
				patternName = "marker_peace16";
			} else if (modelName.equals("bench.obj")) {
				patternName = "marker_at16";
			} else if (modelName.equals("towergreen.obj")) {
				patternName = "marker_hand16";
			}
			
			return patternName;
		}
		
		
    	@Override
    	protected Void doInBackground(Void... params) {
    		
			Intent intent = getIntent();
			Bundle data = intent.getExtras();
			int type = data.getInt("type");
			String modelFileName = data.getString("name");
			BaseFileUtil fileUtil= null;
			File modelFile=null;
			switch(type) {
			case TYPE_EXTERNAL:
				fileUtil = new SDCardFileUtil();
				modelFile =  new File(URI.create(modelFileName));
				modelFileName = modelFile.getName();
				fileUtil.setBaseFolder(modelFile.getParentFile().getAbsolutePath());
				break;
			case TYPE_INTERNAL:
				fileUtil = new AssetsFileUtility(getResources().getAssets());
				fileUtil.setBaseFolder("models/");
				break;
			}
			
			if(modelFileName.endsWith(".obj")) {
				ObjParser parser = new ObjParser(fileUtil);
				try {
					if(Config.DEBUG)
						Debug.startMethodTracing("AndObjViewer");
					if(type == TYPE_EXTERNAL) {
						BufferedReader modelFileReader = new BufferedReader(new FileReader(modelFile));
						String shebang = modelFileReader.readLine();				
						if(!shebang.equals("#trimmed")) {
							File trimmedFile = new File(modelFile.getAbsolutePath()+".tmp");
							BufferedWriter trimmedFileWriter = new BufferedWriter(new FileWriter(trimmedFile));
							Util.trim(modelFileReader, trimmedFileWriter);
							if(modelFile.delete()) {
								trimmedFile.renameTo(modelFile);
							}					
						}
					}
					if(fileUtil != null) {
						BufferedReader fileReader = fileUtil.getReaderFromName(modelFileName);
						if(fileReader != null) {
							model = parser.parse("Model", fileReader);
							Log.w("ModelLoader", "model3d = new Model3D(model, " + modelName2patternName(modelFileName) + ".patt");
							model3d = new Model3D(model, modelName2patternName(modelFileName) + ".patt");
						}
						String modelFileName2 = "chair.obj";
						BufferedReader fileReader2 = fileUtil.getReaderFromName(modelFileName2);
						if(fileReader2 != null) {
							model2 = parser.parse("Chair", fileReader2);
							Log.w("ModelLoader", "model3d = new Model3D(model2, " + modelName2patternName(modelFileName2) + ".patt");
							model3d2 = new Model3D(model2, modelName2patternName(modelFileName2) + ".patt");
						} else {
							Log.w("ModelLoader", "no file reader");
						}
						String modelFileName3 = "towergreen.obj";
						BufferedReader fileReader3 = fileUtil.getReaderFromName(modelFileName3);
						if(fileReader3 != null) {
							model3 = parser.parse("towergreen", fileReader3);
							Log.w("ModelLoader", "model3d = new Model3D(model3, " + modelName2patternName(modelFileName3) + ".patt");
							model3d3 = new Model3D(model3, modelName2patternName(modelFileName3) + ".patt");
						} else {
							Log.w("ModelLoader", "no file reader");
						}
						String modelFileName4 = "tower.obj";
						BufferedReader fileReader4 = fileUtil.getReaderFromName(modelFileName4);
						if(fileReader4 != null) {
							model4 = parser.parse("tower", fileReader4);
							Log.w("ModelLoader", "model3d = new Model3D(model4, " + modelName2patternName(modelFileName4) + ".patt");
							model3d4 = new Model3D(model4, modelName2patternName(modelFileName4) + ".patt");
						} else {
							Log.w("ModelLoader", "no file reader");
						}
						String modelFileName5 = "plant.obj";
						BufferedReader fileReader5 = fileUtil.getReaderFromName(modelFileName5);
						if(fileReader5 != null) {
							model5 = parser.parse("Plant", fileReader5);
							Log.w("ModelLoader", "model3d = new Model3D(model5, " + modelName2patternName(modelFileName5) + ".patt");
							model3d5 = new Model3D(model5, modelName2patternName(modelFileName5) + ".patt");
						} else {
							Log.w("ModelLoader", "no file reader");
						}
					}
					if(Config.DEBUG)
						Debug.stopMethodTracing();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
    		return null;
    	}
    	@Override
    	protected void onPostExecute(Void result) {
    		super.onPostExecute(result);
    		waitDialog.dismiss();
    		
    		try {
    			if(model3d!=null) {
    				artoolkit.registerARObject(model3d);
    				artoolkit.registerARObject(model3d2);
    				artoolkit.registerARObject(model3d3);
    				artoolkit.registerARObject(model3d4);
    				artoolkit.registerARObject(model3d5);
    			}
			} catch (AndARException e) {
				e.printStackTrace();
			}
			startPreview();
    	}
    }
	
	class TakeAsyncScreenshot extends AsyncTask<Void, Void, Void> {
		
		private String errorMsg = null;

		@Override
		protected Void doInBackground(Void... params) {
			Bitmap bm = takeScreenshot();
			FileOutputStream fos;
			try {
				fos = new FileOutputStream("/sdcard/AndARScreenshot"+new Date().getTime()+".png");
				bm.compress(CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();					
			} catch (FileNotFoundException e) {
				errorMsg = e.getMessage();
				e.printStackTrace();
			} catch (IOException e) {
				errorMsg = e.getMessage();
				e.printStackTrace();
			}	
			return null;
		}
		
		protected void onPostExecute(Void result) {
			if(errorMsg == null)
				Toast.makeText(ModelViewer.this, getResources().getText(R.string.screenshotsaved), Toast.LENGTH_SHORT ).show();
			else
				Toast.makeText(ModelViewer.this, getResources().getText(R.string.screenshotfailed)+errorMsg, Toast.LENGTH_SHORT ).show();
		};
		
	}
	
	
}