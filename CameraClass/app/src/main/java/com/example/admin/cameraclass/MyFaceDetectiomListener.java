package com.example.admin.cameraclass;

import android.hardware.Camera;
import android.util.Log;


/**
 * Created by pg01harman on 11/29/2014.
 */

class MyFaceDetectionListener  implements Camera.FaceDetectionListener
{
    private FaceOverlayView mFaceView;
    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {

       String TAG = ("name");
        Log.i(TAG, "face" + String.valueOf(faces));

        if (faces.length == 0)
        {
             Log.i(TAG, "No faces detected");
        } else if (faces.length > 0)
        {
          //    mFaceView.setFaces(faces);

              Log.i(TAG, "Faces Detected = " +
            String.valueOf(faces.length));

        }
    }

}
