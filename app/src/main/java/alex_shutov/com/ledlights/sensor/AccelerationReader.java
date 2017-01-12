package alex_shutov.com.ledlights.sensor;

/**
 * Created by lodoss on 12/01/17.
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import alex_shutov.com.ledlights.sensor.filtering.FirstOrderHighPassFilter;

import static alex_shutov.com.ledlights.sensor.SensorReader.*;

/**
 * Decorator / Strategy for measuring acceleration without gravity.
 * It use Sensor.TYPE_LINEAR_ACCELERATION if device support it, or, if it not
 * (as my small HTC desire C), use ordinary acceleration sensor Sensor.TYPE_ACCELEROMETER
 * and remove gravity by using high pass filter (see HighPassFilterSensorDecorator class).
 */
public class AccelerationReader extends SensorReaderDecorator implements SensorReadingCallback {

    public AccelerationReader(Context context) {
        super(context);
        initialize();
    }


    /**
     * Inherited from SensorReader
     */

    @Override
    public void startReadingSensors() throws IllegalStateException {
        if (!hasAccelerometer()) {
            throw new IllegalStateException("Phone has no accelerometer");
        }
        getDecoree().startReadingSensors();
    }


    @Override
    public void stopReadingSensors() {
        getDecoree().stopReadingSensors();
    }

    /**
     * Inherited from SensorReadingCallback
     */

    @Override
    public void processSensorReading(Reading reading) {
        getCallback().processSensorReading(reading);
    }


    /**
     * Check if this device has a accelerometer
     * @return
     */
    private boolean hasAccelerometer() {
        SensorManager sensorManager = getSensorManager();
        return null != sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * Check if device has linear accelerometer
     * @return
     */
    private boolean hasLinearAccelerometer() {
        SensorManager sensorManager = getSensorManager();
        return null != sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    private void initialize() {
        if (!hasAccelerometer()) {
            return;
        }
        if (hasLinearAccelerometer()) {
            useDefaultLinearAccelerometer();
        } else {
            useOwnCustomAccelerometer();
        }
        // get rid of bias in measurements
        UnbiasingDecorator unbiasingDecorator = new UnbiasingDecorator(getContext());
        unbiasingDecorator.setDecoree(getDecoree());
        setDecoree(unbiasingDecorator);
    }

    /**
     * Phone support linear accelerometer, use it
     */
    private void useDefaultLinearAccelerometer() {
        SensorReader sensorReader = new HardwareAccelerationReader(getContext(), true);
        setDecoree(sensorReader);
    }

    /**
     * Phone doesn't have default linear accelerometer, use ordinary one and filter data to
     * filter out gravity
     */
    private void useOwnCustomAccelerometer() {
        Context context = getContext();
        // create sensor reader, reading acceleration with gravity
        SensorReader hardwareSensor = new HardwareAccelerationReader(context, false);
        // crate filtering frame
        HighPassFilterSensorDecorator filteringSensor =
                new HighPassFilterSensorDecorator(context, new FirstOrderHighPassFilter(),
                        new FirstOrderHighPassFilter(), new FirstOrderHighPassFilter());
        // set sensor into the frame
        filteringSensor.setDecoree(hardwareSensor);
        // and use it as our sensor
        setDecoree(filteringSensor);
    }
}
