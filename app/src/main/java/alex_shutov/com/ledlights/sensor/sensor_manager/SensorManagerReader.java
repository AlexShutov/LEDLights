package alex_shutov.com.ledlights.sensor.sensor_manager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import alex_shutov.com.ledlights.sensor.Reading;
import alex_shutov.com.ledlights.sensor.SensorReader;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by lodoss on 13/01/17.
 */

public abstract class SensorManagerReader extends SensorReader {

    private SensorManager sensorManager;
    private Sensor sensor;

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Observable.defer(() -> Observable.just(event))
                    .subscribeOn(Schedulers.computation())
                    .subscribe(t -> {
                        Reading reading = new Reading(event, 0);
                        getReadingPipe().onNext(new Reading(event, 0));
                    });
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Observable.defer(() -> Observable.just(accuracy))
                    .subscribeOn(Schedulers.computation())
                    .subscribe(t -> getCallback().onSensorAccuracyChanged(t));
        }
    };

    /**
     * Specify type of sensor you want to monitor in derived class
     * @return
     */
    protected abstract int getSensorType();

    public SensorManagerReader(Context context) {
        super(context);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }


    @Override
    public void startReadingSensors() throws IllegalStateException {
        if (!isSensorPresent()) {
            throw new IllegalStateException("Lack of sensor: " + getSensorType());
        }
        super.startReadingSensors();
    }

    /**
     * Check if phone has that kind of sensor.
     * @return
     */
    public boolean isSensorPresent() {
        return sensorManager.getDefaultSensor(getSensorType()) != null;
    }


    public SensorManager getSensorManager() {
        return sensorManager;
    }

    @Override
    protected void stopPollingHardwareSensor() {
        sensorManager.unregisterListener(listener);
    }

    @Override
    protected void startPollingHardwareSensor() {
        // get type of sensor
        int sensorType = getSensorType();
        // register listener for that sensor
        sensor = sensorManager.getDefaultSensor(sensorType);
        // initialize additional features in derived class
        getCallback().onBeforeStartingReadingSensor();
        int samplingPeriod = getSamplingPeriod();
        sensorManager.registerListener(listener, sensor, samplingPeriod);
    }

}
