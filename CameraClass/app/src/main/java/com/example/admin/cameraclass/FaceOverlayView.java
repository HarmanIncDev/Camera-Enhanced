package com.example.admin.cameraclass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.view.View;

/**
 * Created by pg01harman on 11/29/2014.
 */
public class FaceOverlayView extends View {
    private Paint mPaint;                     ////// paint class refrence
    private Camera.Face[] mFaces;              ////// hardware camera to get the arrray from the surface and add it to the canvas. to draw.

    public FaceOverlayView(Context context) {
        super(context);      //////////// the faceoverlayview open at launch
        initialize();
    }

    private void initialize()
    {
        // green box drawing tools
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setAlpha(128);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setFaces(Camera.Face[] faces)
    {
        mFaces = faces;          ////add faces from the hardware camera surface to the canvas files
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)                      //// on draw on the canvas called at launch.
    {
        super.onDraw(canvas);
        if (mFaces != null && mFaces.length > 0) {
            Matrix matrix = new Matrix();
            prepareMatrix(matrix, getWidth(), getHeight() );
            canvas.save();
            RectF rectF = new RectF();
            for (Camera.Face face : mFaces) {
                rectF.set(face.rect);
                matrix.mapRect(rectF);
                canvas.drawRect(rectF, mPaint);
            }
            canvas.restore();
        }
    }

    public static void prepareMatrix(Matrix matrix,
                                     int viewWidth, int viewHeight) {
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
    }
}
