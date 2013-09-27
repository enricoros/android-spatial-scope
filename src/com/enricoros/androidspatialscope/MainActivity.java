
package com.enricoros.androidspatialscope;


import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static class RotationMatrix {
        float[] M = new float[16];

        public RotationMatrix() {
            Matrix.setIdentityM(M, 0);
        }

        public RotationMatrix duplicate() {
            RotationMatrix copy = new RotationMatrix();
            copy.M = M.clone();
            return copy;
        }
    };

    private SensorManager mSensorManager;
    private MyRenderer mRenderer;
    private GLSurfaceView mGLSurfaceView;

    private Sensor mRotationVectorSensor;

    private int mDisplayRotation;

    private RotationMatrix mLastRotation = new RotationMatrix();
    private RotationMatrix mStartRotation = null;

    private RotationMatrix mInteMatrix = new RotationMatrix();
    private TextView mOverlayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mSensorsManager = new SensorsManager(this);

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // if (Build.VERSION.SDK_INT >= 16)
        // mRotationVectorSensor =
        // mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        if (mRotationVectorSensor == null)
            mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // initialize the rotation matrix to identity

        mRenderer = new MyRenderer();
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setRenderer(mRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartRotation = mLastRotation.duplicate();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mStartRotation = null;
                        break;
                }
                return true;
            }
        });

        setContentView(R.layout.activity_main);
        RelativeLayout mainLay = (RelativeLayout) findViewById(R.id.dasMain);
        mOverlayText = (TextView) mainLay.findViewById(R.id.coordsView);
        mainLay.addView(mGLSurfaceView);
        mainLay.bringChildToFront(mOverlayText);

        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        mDisplayRotation = defaultDisplay.getRotation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mCentralSensorsReceiver, mRotationVectorSensor, SensorManager.SENSOR_DELAY_GAME);
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mCentralSensorsReceiver);
        mGLSurfaceView.onPause();
    }


    private final SensorEventListener mCentralSensorsReceiver = new SensorEventListener() {
        private final RotationMatrix mTempMatrix = new RotationMatrix();

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_GAME_ROTATION_VECTOR:
                case Sensor.TYPE_ROTATION_VECTOR:
                    // convert the rotation-vector to a 4x4 matrix. the matrix
                    // is interpreted by Open GL as the inverse of the
                    // rotation-vector, which is what we want.
                    switch (mDisplayRotation) {
                        case Surface.ROTATION_0:
                            SensorManager.getRotationMatrixFromVector(mLastRotation.M, event.values);
                            break;
                        case Surface.ROTATION_90:
                            SensorManager.getRotationMatrixFromVector(mTempMatrix.M, event.values);
                            SensorManager.remapCoordinateSystem(mTempMatrix.M, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, mLastRotation.M);
                            break;
                        case Surface.ROTATION_180:
                            SensorManager.getRotationMatrixFromVector(mTempMatrix.M, event.values);
                            SensorManager.remapCoordinateSystem(mTempMatrix.M, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, mLastRotation.M);
                            break;
                        case Surface.ROTATION_270:
                            SensorManager.getRotationMatrixFromVector(mTempMatrix.M, event.values);
                            SensorManager.remapCoordinateSystem(mTempMatrix.M, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, mLastRotation.M);
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

            gl.glPushMatrix();
            gl.glMultMatrixf(mLastRotation.M, 0);
            if (mStartRotation == null)
                gl.glColor4f(1, 1, 1, 1);
            else
                gl.glColor4f(0, 0, 0, 1);
            mCube.drawGL10(gl, true, true);
            gl.glPopMatrix();

            if (mStartRotation != null) {
                gl.glPushMatrix();

                float[] delta = null;
                delta = new float[3];
                SomeMath.decomposeEulerRotation(delta, mLastRotation.M, mStartRotation.M);
                final String rString = "x: " + Math.round(delta[0] * 100) / 100.0f + ", y: " + Math.round(delta[1] * 100) / 100.0f + ", z: " + Math.round(delta[2] * 100) / 100.0f;
                // Log.w("ROT", rString);
                mOverlayText.post(new Runnable() {
                    @Override
                    public void run() {
                        mOverlayText.setText(rString);
                    }
                });

                gl.glMultMatrixf(mStartRotation.M, 0);
                gl.glColor4f(1, 1, 1, 1);
                mCube.drawGL10(gl, false, true);
                gl.glPopMatrix();
            }

            gl.glPushMatrix();
            gl.glMultMatrixf(mInteMatrix.M, 0);
            if (mStartRotation != null) {

                // Matrix.rot

            }
            gl.glColor4f(0, 0.5f, 0.5f, 0.2f);
            // mCube.drawGL10(gl, false, true);
            gl.glPopMatrix();
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
