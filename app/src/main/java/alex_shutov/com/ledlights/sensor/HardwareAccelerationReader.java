package alex_shutov.com.ledlights.sensor;

import android.content.Context;
import android.hardware.Sensor;

/**
 * Created by lodoss on 12/01/17.
 */

/**
 * This is a SensorReader, which can either use Sensor.TYPE_LINEAR_ACCELERATION or
 * Sensor.TYPE_ACCELEROMETER. It ordinary accelerometer is used, it won't be substracting
 * gravity.
 */
public class HardwareAccelerationReader extends SensorManagerReader {
    private static final String LOG_ACCELERATION = "Acceleration sensor";

    // sample acceleration once in 50 milliseconds
    private static final int SAMPLE_PERIOD = 50 * 1000;

    // Linear accelerometer exclude gravity from readings - it is, actually, a combination of
    // accelerometer and gravity sensor
    private final boolean isLinearAcceleration;

    public HardwareAccelerationReader(Context context, boolean isLinear) {
        super(context);
        isLinearAcceleration = isLinear;
    }

    @Override
    protected int getSensorType() {
        return isLinearAcceleration ? Sensor.TYPE_LINEAR_ACCELERATION : Sensor.TYPE_ACCELEROMETER;
    }

    @Override
    protected int getSamplingPeriod() {
        return SAMPLE_PERIOD;
    }
}
