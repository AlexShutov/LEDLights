package alex_shutov.com.ledlights.sensor;

import android.hardware.SensorEvent;

/**
 * Created by lodoss on 12/01/17.
 */

/**
 * Readings wrapper, substract drift from values
 */
public class Reading {
    public float values[];
    public long timeInterval;
    // rarely used
    public long timestamp;

    public Reading() {
        values = new float[3];
    }

    public Reading(SensorEvent event, long timeInterval) {
        values = new float[3];
        values[0] = event.values[0];
        values[1] = event.values[1];
        values[2] = event.values[2];
        this.timeInterval = timeInterval;
        timestamp = event.timestamp;
    }
}
