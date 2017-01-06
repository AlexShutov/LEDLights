package alex_shutov.com.ledlights.sensors.sensors;

import android.content.Context;

import alex_shutov.com.ledlights.sensors.SensorReading;
import rx.Observable;

/**
 * Created by lodoss on 06/01/17.
 */

public abstract class Sensor {

    // use Context for accessing sensors in Android
    private Context context;

    public Sensor(Context context) {

    }

    /**
     * Measure value in background and emit it when measurement complete.
     * @return read sensor data.
     */
    Observable<SensorReading> measureValue();
}
