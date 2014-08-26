package com.example.flashlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.*;
import android.hardware.Camera.*;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.*;
import android.util.*;


public class MainActivity extends Activity {
	private Camera camera;
	private boolean hasFlash;
	private Button btn_switch;
    private Parameters params;
    
    private boolean strobeOn;
    
    private Handler strobeHandler = new Handler();
    
	
	private int counter = 0;
	
		
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_switch = (Button) findViewById(R.id.btn_switch);
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
         
        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support the Flashlight! App!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }
        btn_switch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				action();
			}
		});
    }
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
            }
        }
    }
    private void turnOnTorch(){
    	params = camera.getParameters();
    	params.setFlashMode(Parameters.FLASH_MODE_TORCH);
    	camera.setParameters(params);
    	camera.startPreview();
    }
    private void turnOnStrobe(){
    	final Runnable mRunnable = new Runnable() {

            public void run() {         
                if (strobeOn) {
                    turnOnTorch();
                    try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    turnOff();
                }           
            }
        };
    }
    private void turnOff(){
    	params = camera.getParameters();
    	params.setFlashMode(Parameters.FLASH_MODE_OFF);
    	camera.setParameters(params);
    	camera.startPreview();
    }
    private void action(){
    	switch(counter){
    		case 0:
    			strobeOn = false;
    			counter++;
    			btn_switch.setText("Off");
    			turnOff();
    			break;
    		case 1:
    			strobeOn = false;
    			counter++;
    			btn_switch.setText("On");
    			turnOnTorch();
    			break;
    		case 2:
    			strobeOn = true;
    			counter = 0;
    			btn_switch.setText("Strobe");
    			turnOnStrobe();
    			break;
    	}
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
     
    @Override
    protected void onPause() {
        super.onPause();
         
        // on pause turn off the flash
        turnOff();
    }
     
    @Override
    protected void onRestart() {
        super.onRestart();
    }
     
    @Override
    protected void onResume() {
        super.onResume();
         
        // on resume turn on the flash
        if(hasFlash)
            turnOnTorch();
    }
     
    @Override
    protected void onStart() {
        super.onStart();
         
        // on starting the app get the camera params
        getCamera();
    }
     
    @Override
    protected void onStop() {
        super.onStop();
         
        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}