package com.example.flashlight;

import android.hardware.*;
import android.hardware.Camera.*;
import android.util.Log;

public class FlashlightThread implements Runnable{
	
	private Camera camera;
	private Parameters params;
	protected boolean strobeOn = false;
	
	public FlashlightThread(){
		getCamera();
	}
	public void run(){
		while(true){
			if(strobeOn){
				params = camera.getParameters();
		    	params.setFlashMode(Parameters.FLASH_MODE_TORCH);
		    	camera.setParameters(params);
		    	camera.startPreview();
		    	
		    	try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    	
		    	params = camera.getParameters();
		    	params.setFlashMode(Parameters.FLASH_MODE_OFF);
		    	camera.setParameters(params);
		    	camera.startPreview();
			}
		}
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
}