
package com.enricoros.androidspatialscope;


import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;

public class MainActivity extends Activity {

    //private SensorsManager mSensorsManager;

    private SensorManager mSensorManager;
    private MyRenderer mRenderer;
    private GLSurfaceView mGLSurfaceView;

    private Sensor mRotationVectorSensor;

    private final float[] mRotationMatrix = new float[16];
    private int mDisplayRotation;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mSensorsManager = new SensorsManager(this);

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        mRotationVectorSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR);

        // initialize the rotation matrix to identity
        mRotationMatrix[0] = 1;
        mRotationMatrix[4] = 1;
        mRotationMatrix[8] = 1;
        mRotationMatrix[12] = 1;

        mRenderer = new MyRenderer();
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setRenderer(mRenderer);
        setContentView(mGLSurfaceView);

        Display getOrient = getWindowManager().getDefaultDisplay();
        mDisplayRotation = getOrient.getRotation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mCentralSensorsReceiver, mRotationVectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mCentralSensorsReceiver);
        mGLSurfaceView.onPause();
    }


    private final SensorEventListener mCentralSensorsReceiver = new SensorEventListener() {
        private final float[] mTempMatrix = new float[16];

        @Override
        public void onSensorChanged(SensorEvent event) {

            switch (event.sensor.getType()) {
                case Sensor.TYPE_ROTATION_VECTOR:
                    // convert the rotation-vector to a 4x4 matrix. the matrix
                    // is interpreted by Open GL as the inverse of the
                    // rotation-vector, which is what we want.
                    switch (mDisplayRotation) {
                        case Surface.ROTATION_0:
                            SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
                            break;
                        case Surface.ROTATION_90:
                            SensorManager.getRotationMatrixFromVector(mTempMatrix, event.values);
                            SensorManager.remapCoordinateSystem(mTempMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, mRotationMatrix);
                            break;
                        case Surface.ROTATION_180:
                            SensorManager.getRotationMatrixFromVector(mTempMatrix, event.values);
                            SensorManager.remapCoordinateSystem(mTempMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, mRotationMatrix);
                            break;
                        case Surface.ROTATION_270:
                            SensorManager.getRotationMatrixFromVector(mTempMatrix, event.values);
                            SensorManager.remapCoordinateSystem(mTempMatrix, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, mRotationMatrix);
                            break;
                    }
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

    };

    class MyRenderer implements GLSurfaceView.Renderer {
        private final GLUnitCube mCube = new GLUnitCube();

        @Override
        public void onDrawFrame(GL10 gl) {
            // clear screen
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

            // set-up modelview matrix
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -1f);
            gl.glMultMatrixf(mRotationMatrix, 0);

            mCube.drawGL10(gl);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);

            float ratio = (float) width / height;
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glFrustumf(-ratio, ratio, -1, 1, 0.7f, 10); // (0.7 = 1/tan(110/2), 110 being the FOV)
        }

        @Override
        public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
            gl.glDisable(GL10.GL_DITHER);
            gl.glClearColor(1, 1, 1, 0.05f);
        }

    }

}
