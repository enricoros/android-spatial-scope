package com.enricoros.androidspatialscope;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class SensorsCollection {

    private Activity mActivity;
    private SensorManager mSensorManager;

    private List<Sensor> mRegisteredSensors;

    public SensorsCollection(Activity activity) {
        mActivity = activity;
        mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        mRegisteredSensors = new ArrayList<Sensor>();
        for (Sensor sensor : deviceSensors) {
            switch (sensor.getType()) {
            case Sensor.TYPE_ROTATION_VECTOR:
                registerRotationVectorSensor(sensor);
                break;
            }
        }

    }

    private void registerRotationVectorSensor(Sensor sensor) {

    }

}
