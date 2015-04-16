package com.example.admin.cameraclass;

import android.app.ActionBar;
import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;



public class CameraActivity extends Activity
{

    private FrameLayout _cameraLayout;    // intializing a framelayout to create a camera surface on it.
    private CameraSurface _cameraSurface;   /// refrence to the cameraSurface class.
    private boolean flashOnOff = false;    /// bool to triger the flash button
    private static FaceOverlayView mFaceView;   /// a static refrence to the faceoverlayview class to draw the canvas.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        _cameraLayout = (FrameLayout) findViewById(R.id.camera_preview);   /// giving the ui element to the cameralayout
        setupCamera();     ////function call

        //create the overlayView
        mFaceView = new FaceOverlayView(this);     ///// creating the canvas
        addContentView(mFaceView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));    /// adding the content to  the canvas

        addWhiteBalance();   ///// function calls
        addFilterView();
        addSceneMode();

     }

    private void setupCamera()
    {
        _cameraSurface = new CameraSurface(this);     //// add the view to the camera surface created.
        _cameraLayout.addView(_cameraSurface);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void takePictureClick(View view){
        _cameraSurface.autoFocusThenTakePicture();     //// take the photo functions.

    }

    public void flashOnOff(View view)       ///// making the flash button turn on/off the flash.
    {
        Button myButton = (Button)findViewById(R.id.flash);
        if(flashOnOff)
        {
             myButton.setText("Flash Off");
            _cameraSurface.flashOffOnSurface();
            flashOnOff = false;
        }
        else
        {
            myButton.setText("Flash On");
            _cameraSurface.flashOnOnSurface();
            flashOnOff = true;
        }

    }

    public void addWhiteBalance()       ///// getting the white balance objects
    {
        Spinner spinner = (Spinner) findViewById(R.id.whiteBalance);     //// accesing the spinner form the view adding it into spinner variable.

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,      ////   spinner get the array list index
                R.array.WhiteBalance, android.R.layout.simple_spinner_item);                     /////     making the spinner whitebalance to the list from the camera surface
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()      ///// creating the onclick listner.
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //System.out.println("h" + i);
                _cameraSurface.changeWhiteBalance(i);            /////////////the method in the callled with refrence form the class and index in the list.

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                    _cameraSurface.changeWhiteBalance(0);
            }


        });
    }

    public void addFilterView ()       ///////////////repeative action for the other filter and scene mode  spinners
    {
        Spinner spinner = (Spinner) findViewById(R.id.filterMode);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.FilterEffect, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                _cameraSurface.changeFilterEffect(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                  _cameraSurface.changeFilterEffect(0);
            }
        });
    }

    public void addSceneMode ()
    {
        Spinner spinner = (Spinner) findViewById(R.id.sceneMode);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.SceneMode, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                _cameraSurface.changeSceneMode(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                _cameraSurface.changeSceneMode(0);
            }
        });
    }

    public static Camera.FaceDetectionListener faceDetectionListener = new Camera.FaceDetectionListener() {
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {      ////// listener for the facedetection .


            // Update the view now!
            mFaceView.setFaces(faces);     //// refrence to the faceoverlayview class where canvas is drawn, and faces are saved from the array from hardware.
        }
    };



}
