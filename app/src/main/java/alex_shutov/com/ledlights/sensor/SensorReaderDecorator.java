package alex_shutov.com.ledlights.sensor;

import android.content.Context;

import static alex_shutov.com.ledlights.sensor.SensorReader.*;

/**
 * Created by Alex on 1/12/2017.
 */

public abstract class SensorReaderDecorator extends SensorReader implements SensorReadingCallback {

    private SensorReader decoree;

    public SensorReaderDecorator(Context context) {
        super(context);
    }

    /**
     * Inherited from SensorReader. Don't override methods for starting and stopping
     * reading sensor values, because those change very often and should be implemented
     * in a concrete decorator.
     */


    @Override
    protected int getSamplingPeriod() {
        return 0;
    }

    @Override
    protected void stopPollingHardwareSensor() {
    }

    @Override
    protected void startPollingHardwareSensor() {
    }

    /**
     * Inherited from SensorReadingCallback
     */

    @Override
    public void onSensorAccuracyChanged(int newAccuracy) {
        getCallback().onSensorAccuracyChanged(newAccuracy);
    }

    @Override
    public void onBeforeStartingReadingSensor() {
        getCallback().onBeforeStartingReadingSensor();
    }

    @Override
    public void onAfterStoppedReadingSensor() {
        getCallback().onAfterStoppedReadingSensor();
    }


    // Accessors


    public void setDecoree(SensorReader decoree) {
        this.decoree = decoree;
        // this is a decorator, use it as callback for redirecting calls to actual callback.
        decoree.setCallback(this);
    }

    public SensorReader getDecoree() {
        return decoree;
    }
}
