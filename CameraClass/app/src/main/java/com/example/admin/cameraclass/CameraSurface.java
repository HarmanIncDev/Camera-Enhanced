package com.example.admin.cameraclass;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;



public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback
{
    private Context _context;
    private Camera _camera;
    private SurfaceHolder _surfaceHolder;
    private float moveDistance;
    private CameraActivity _cameraActivity;


    String[] whiteBalanceList;    ////////string array to add list of the whitebalance parameter from android library.
    String[] filterEffect;      ////////string array to add list of the filterefffect parameter from android library.
    String[] sceneEffect;       ////////string array to add list of the scenefileter parameter from android library.



    public CameraSurface(Context context)
    {
        super(context);
        _context = context;
        _surfaceHolder = getHolder();       // get the SurfaceHolder from the SurfaceView
        _surfaceHolder.addCallback(this);   // set the SurfaceHolder's callbacks to call this class
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        _camera = _camera.open(); // open the camera

        _camera.setFaceDetectionListener(_cameraActivity.faceDetectionListener);      ////call the facedetectionlistner.


           // set the camera's preview to use our surfaceHolder - that gives it access to the Surface
        try
        {
            _camera.setPreviewDisplay(surfaceHolder);

        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        whiteBalanceList = new String[]                 ////// addin to list of array
        {
                Camera.Parameters.WHITE_BALANCE_AUTO,
                Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT,
                Camera.Parameters.WHITE_BALANCE_DAYLIGHT,
                Camera.Parameters.WHITE_BALANCE_FLUORESCENT,
                Camera.Parameters.WHITE_BALANCE_INCANDESCENT,
                Camera.Parameters.WHITE_BALANCE_SHADE,
                Camera.Parameters.WHITE_BALANCE_TWILIGHT,
                Camera.Parameters.WHITE_BALANCE_WARM_FLUORESCENT

        };

        sceneEffect = new String[]
        {
                Camera.Parameters.SCENE_MODE_AUTO,
                Camera.Parameters.SCENE_MODE_ACTION,
                Camera.Parameters.SCENE_MODE_BARCODE,
                Camera.Parameters.SCENE_MODE_BEACH,
                Camera.Parameters.SCENE_MODE_CANDLELIGHT,
                Camera.Parameters.SCENE_MODE_FIREWORKS,
                Camera.Parameters.SCENE_MODE_LANDSCAPE,
                Camera.Parameters.SCENE_MODE_NIGHT,
                Camera.Parameters.SCENE_MODE_NIGHT_PORTRAIT,
                Camera.Parameters.SCENE_MODE_PARTY,
                Camera.Parameters.SCENE_MODE_PORTRAIT,
                Camera.Parameters.SCENE_MODE_SNOW,
                Camera.Parameters.SCENE_MODE_SPORTS,
                Camera.Parameters.SCENE_MODE_STEADYPHOTO,
                Camera.Parameters.SCENE_MODE_SUNSET,
                Camera.Parameters.SCENE_MODE_THEATRE

        };

        filterEffect = new String[]
        {
                Camera.Parameters.EFFECT_NONE,
                Camera.Parameters.EFFECT_AQUA,
                Camera.Parameters.EFFECT_MONO,
                Camera.Parameters.EFFECT_NEGATIVE,
                Camera.Parameters.EFFECT_POSTERIZE,
                Camera.Parameters.EFFECT_SEPIA,
                Camera.Parameters.EFFECT_SOLARIZE
        };


    }

    @Override
    // if the size or the type of the surface changes, this gets called
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height)
    {

        // change camera orientation (by default it is landscape)
        if (height > width) _camera.setDisplayOrientation(90);
        else _camera.setDisplayOrientation(0);
        _camera.startPreview();

        if (surfaceHolder.getSurface() == null)
        {
            // preview surface does not exist
            return;
        }

        try
        {
            _camera.stopPreview();

        } catch (Exception e)
        {
            // ignore: tried to stop a non-existent preview
        }
        // Get the supported preview sizes:
        Camera.Parameters parameters = _camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = previewSizes.get(0);
        // And set them:
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        _camera.setParameters(parameters);

        _camera.startPreview();
        _camera.setFaceDetectionListener(_cameraActivity.faceDetectionListener);
        _camera.startFaceDetection();

     }

    public void autoFocusThenTakePicture()
    {
        _camera.autoFocus(new Camera.AutoFocusCallback()
        {
            @Override
            public void onAutoFocus(boolean success, Camera camera)
            {
                takeMyPicture();
            }
        });
    }

    private void takeMyPicture()
    {
        _camera.takePicture( new Camera.ShutterCallback()
                {
                    @Override
                    public void onShutter()
                    {

                    }
                },
                new Camera.PictureCallback()
                {
                    @Override
                    public void onPictureTaken(byte[] bytes, Camera camera)
                    {

                    }
                },
                new Camera.PictureCallback()
                {

                    @Override
                    public void onPictureTaken(byte[] bytes, Camera camera)
                    {
                        onJpegPictureTaken(bytes);
                    }
                });

    }

    private void onJpegPictureTaken(byte[] data)
    {
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        try
        {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(data);
            fo.close();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
        _context.startActivity(Intent.createChooser(shareIntent, "Share Image"));

        // lastly, reset the camera so that it can take more pictures
        _camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        _camera.stopPreview();
        _camera.setFaceDetectionListener(null);
        _camera.release();
        _camera = null;
    }

    public void flashOnOnSurface()      //////////// setting the flash parameters
    {
        Camera.Parameters param = _camera.getParameters();
        param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        _camera.setParameters(param);
    }
    public  void  flashOffOnSurface()
    {
        Camera.Parameters param = _camera.getParameters();
        param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        _camera.setParameters(param);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)     /////////// gettting the ontouch event on the view
    {
        //get the pointer id
        Camera.Parameters params = _camera.getParameters();
        int action = event.getAction();


        if (event.getPointerCount() > 1)        ///////////// touch hadler for the focus
        {
            // multitouch
            if (action == MotionEvent.ACTION_POINTER_DOWN)
            {
                moveDistance = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                _camera.cancelAutoFocus();
                handleZoom(event, params);
            }
        } else {
            // handle single touch events
            if (action == MotionEvent.ACTION_UP) {
                handleFocus(event, params);
            }
        }

        return  true;
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {    ///// zoom handler
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > moveDistance) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < moveDistance) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        moveDistance = newDist;
        params.setZoom(zoom);
        _camera.setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {    //// focus handler
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            _camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    //  set to auto-focus on single touch
                }
            });
        }
    }

    private float getFingerSpacing(MotionEvent event) {     ////////// gettin the figer spacing for zooming and passin it in zoom
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    public void changeWhiteBalance(int i)     /////////// contructor to send the index list. add from the string list
    {
      Camera.Parameters params = _camera.getParameters();
        params.setWhiteBalance(whiteBalanceList[i]);
        _camera.setParameters(params);
    }

    public void changeSceneMode(int i)
    {
        Camera.Parameters params = _camera.getParameters();
        params.setSceneMode(sceneEffect[i]);
        _camera.setParameters(params);
    }

    public void changeFilterEffect(int i)
    {
        Camera.Parameters params = _camera.getParameters();
        params.setColorEffect(filterEffect[i]);
        _camera.setParameters(params);
    }


}

