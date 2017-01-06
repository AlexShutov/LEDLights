package alex_shutov.com.ledlights.sensors.sensors;

import android.content.Context;

import alex_shutov.com.ledlights.sensors.SensorReading;
import rx.Observable;

/**
 * Created by lodoss on 06/01/17.
 */

public class SensorAcceleration extends Sensor {

    @Override
    public Observable<SensorReading> measureValue() {
        return null;
    }
}
