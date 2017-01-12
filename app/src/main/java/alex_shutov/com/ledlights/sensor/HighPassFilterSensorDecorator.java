package alex_shutov.com.ledlights.sensor;

import android.content.Context;
import android.hardware.SensorEvent;

import alex_shutov.com.ledlights.sensor.filtering.Filter;

import static alex_shutov.com.ledlights.sensor.SensorReader.*;

/**
 * Created by lodoss on 12/01/17.
 */

/**
 * Decorator, using high pass filter. It is meant to be used with accelerometer for filtering
 * lowFrequencyComponent values and should be used on phones, not having Sensor.TYPE_LINEAR_ACCELERATION
 * accelerometer (prior to API 18, if I'm right).
 */
public class HighPassFilterSensorDecorator extends SensorReader implements SensorReadingCallback {
    // SensorReader implementation, doing all actual work.
    private SensorReader decoree;

    /**
     * High- pass filters, used for substracting gravity component from accelerometer readings
     */
    private Filter filterX;
    private Filter filterY;
    private Filter filterZ;

    /**
     * @param context
     */
    public HighPassFilterSensorDecorator(Context context,
                                         Filter filterX,
                                         Filter filterY,
                                         Filter filterZ) {
        super(context);
        this.filterX = filterX;
        this.filterY = filterY;
        this.filterZ = filterZ;
    }

    /**
     * Do nothing, actual work is done by decoree
     * @return
     */
    @Override
    protected int getSensorType() {
        return 0;
    }

    /**
     * Do nothing, actual work is done by decoree
     * @return
     */
    @Override
    protected int getSamplingPeriod() {
        return 0;
    }

    /**
     * Redirect call to decoree
     * @throws IllegalStateException
     */
    @Override
    public void startReadingSensors() throws IllegalStateException {
        decoree.startReadingSensors();
    }

    @Override
    public void stopReadingSensors() {
        decoree.stopReadingSensors();
    }

    /**
     * Inherited from SensorReadingCallback
     */

    /**
     * Decorated instance think that this reader is a callback. When this method gets called
     * call actual callback.
     */
    @Override
    public void onBeforeStartingReadingSensor() {
        getCallback().onBeforeStartingReadingSensor();
    }

    @Override
    public void onAfterStoppedReadingSensor() {
        getCallback().onAfterStoppedReadingSensor();
    }

    @Override
    public void onSensorAccuracyChanged(int newAccuracy) {
        getCallback().onSensorAccuracyChanged(newAccuracy);
    }

    /**
     * pass measured acceleration to inputs of high pass filters (one for each axis) and
     * update measured acceleration with filtered value.
     * @param reading Sensor measurement
     */
    @Override
    public void processSensorReading(Reading reading) {
        reading.values[0] = filterX.feedValue(reading.values[0]);
        reading.values[1] = filterY.feedValue(reading.values[1]);
        reading.values[2] = filterZ.feedValue(reading.values[2]);
        // pass result up to the chain
        getCallback().processSensorReading(reading);
    }




    // Accessors

    public SensorReader getDecoree() {
        return decoree;
    }

    public void setDecoree(SensorReader decoree) {
        this.decoree = decoree;
        decoree.setCallback(this);
    }
}
