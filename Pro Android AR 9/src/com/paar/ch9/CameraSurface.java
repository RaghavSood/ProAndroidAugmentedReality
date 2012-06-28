package com.paar.ch9;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
    private static SurfaceHolder holder = null;
    private static Camera camera = null;

    public CameraSurface(Context context) {
        super(context);

        try {
            holder = getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera != null) {
                try {
                    camera.stopPreview();
                } catch (Exception ex) {
                	ex.printStackTrace();
                }
                try {
                    camera.release();
                } catch (Exception ex) {
                	ex.printStackTrace();
                }
                camera = null;
            }

            camera = Camera.open();
            camera.setPreviewDisplay(holder);
        } catch (Exception ex) {
            try {
                if (camera != null) {
                    try {
                        camera.stopPreview();
                    } catch (Exception ex1) {
                    	ex.printStackTrace();
                    }
                    try {
                        camera.release();
                    } catch (Exception ex2) {
                    	ex.printStackTrace();
                    }
                    camera = null;
                }
            } catch (Exception ex3) {
            	ex.printStackTrace();
            }
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if (camera != null) {
                try {
                    camera.stopPreview();
                } catch (Exception ex) {
                	ex.printStackTrace();
                }
                try {
                    camera.release();
                } catch (Exception ex) {
                	ex.printStackTrace();
                }
                camera = null;
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            try {
                List<Camera.Size> supportedSizes = null;

                supportedSizes = CameraCompatibility.getSupportedPreviewSizes(parameters);

                float ff = (float)w/h;

                float bff = 0;
                int bestw = 0;
                int besth = 0;
                Iterator<Camera.Size> itr = supportedSizes.iterator();

                while(itr.hasNext()) {
                    Camera.Size element = itr.next();
                    float cff = (float)element.width/element.height;

                    if ((ff-cff <= ff-bff) && (element.width <= w) && (element.width >= bestw)) {
                        bff=cff;
                        bestw = element.width;
                        besth = element.height;
                    }
                } 

                if ((bestw == 0) || (besth == 0)){
                    bestw = 480;
                    besth = 320;
                }
                parameters.setPreviewSize(bestw, besth);
            } catch (Exception ex) {
                parameters.setPreviewSize(480 , 320);
            }

            camera.setParameters(parameters);
            camera.startPreview();
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
}