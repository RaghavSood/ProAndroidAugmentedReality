package com.paar.ch8;

import java.io.File;
import java.net.URI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class CheckFileManagerActivity extends Activity {

	private final int PICK_FILE = 1;
	private final int VIEW_MODEL = 2;
	public static final int RESULT_ERROR = 3;
	
	private final int INSTALL_INTENT_DIALOG=1;
	
	private PackageManager packageManager;
	private Resources res;
	private TextView infoText;

	private final int TOAST_TIMEOUT = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);
        Context context = this;
        packageManager= context.getPackageManager();
        res = this.getResources();
        infoText = (TextView) findViewById(R.id.InfoText);        
        if (isPickFileIntentAvailable()) {
        	selectFile();
        } else {
        	installPickFileIntent();
        }
    }
    

    @Override
    protected void onResume() {
    	super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	switch (requestCode) {
    		default:
	    	case PICK_FILE:
		    	switch(resultCode) {
			    	case Activity.RESULT_OK:
			    		//does file exist??
			    		File file =  new File(URI.create(data.getDataString()));
			    		if (!file.exists()) {
			    			//notify user that this file doesn't exist
			    			Toast.makeText(this, res.getText(R.string.file_doesnt_exist), TOAST_TIMEOUT).show();
			    			selectFile();
			    		} else {
			    			String fileName = data.getDataString();
			    			if(!fileName.endsWith(".obj")) {
			    				Toast.makeText(this, res.getText(R.string.wrong_file), TOAST_TIMEOUT).show();
			    				selectFile();
			    			} else {
				    			//hand over control to the model viewer
					    		Intent intent = new Intent(CheckFileManagerActivity.this, ModelViewer.class);
					            intent.putExtra("name", data.getDataString());
					            intent.putExtra("type", ModelViewer.TYPE_EXTERNAL);
					            intent.setAction(Intent.ACTION_VIEW);
					            startActivityForResult(intent, VIEW_MODEL);
			    			}
			    		}
			    		break;
			    	default:
			    	case Activity.RESULT_CANCELED:
			    		//back to the main activity
			    		Intent intent = new Intent(CheckFileManagerActivity.this, ModelChooser.class);
			            startActivity(intent);
			    		break;
		    	}
		    	break;
	    	case VIEW_MODEL:
	    		switch(resultCode) {
			    	case Activity.RESULT_OK:
			    		//model viewer returned...let the user view a new file
			    		selectFile();
			    		break;
			    	case Activity.RESULT_CANCELED:
			    		selectFile();
			    		break;
			    	case RESULT_ERROR:
			    		//something went wrong ... notify the user
			    		if(data != null) {
				    		Bundle extras = data.getExtras();
				    		String errorMessage = extras.getString("error_message");
				    		if(errorMessage != null)
				    			Toast.makeText(this, extras.getString("error_message"), TOAST_TIMEOUT).show();
			    		}
			    		selectFile();
			    		break;	
	    		}
    	}
    }
    
    private void selectFile() {    	
    	//let the user select a model file
        Intent intent = new Intent("org.openintents.action.PICK_FILE");
        intent.setData(Uri.parse("file:///sdcard/"));
        intent.putExtra("org.openintents.extra.TITLE", res.getText(
        		R.string.select_model_file));
        startActivityForResult(intent, PICK_FILE);
    }
    
    

    private boolean isPickFileIntentAvailable() {
	    return packageManager.queryIntentActivities(
	    		new Intent("org.openintents.action.PICK_FILE"), 0).size() > 0;
    }
    

    private boolean installPickFileIntent() {
    	Uri marketUri = Uri.parse("market://search?q=pname:org.openintents.filemanager");
        Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(marketUri);
        if (!(packageManager
        		.queryIntentActivities(marketIntent, 0).size() > 0)) {
        	//no Market available
        	//show info to user and exit 
        	infoText.setText(res.getText(R.string.android_markt_not_avail));
        	return false;
        } else {
        	//notify user and start Android market    	    	
            
            showDialog(INSTALL_INTENT_DIALOG);	
        	return true;
        }        
    }
    

    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog = null;
    	switch(id){
    		case INSTALL_INTENT_DIALOG:
	    		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	            alertDialog.setMessage(res.getText(R.string.pickfile_intent_required));
	            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	              public void onClick(DialogInterface dialog, int which) {
	            	 //launch android market
	        	     Uri marketUri = Uri.parse("market://search?q=pname:org.openintents.filemanager");
	                 Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(marketUri);
	            	 startActivity(marketIntent);
	                return;
	              } });  
	            dialog = alertDialog;
	    		break;
    	}
    	return dialog;
    }
    
}