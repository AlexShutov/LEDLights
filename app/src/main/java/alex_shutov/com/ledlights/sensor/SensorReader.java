package alex_shutov.com.ledlights.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by lodoss on 12/01/17.
 */

public abstract class SensorReader {

    public interface SensorReadingCallback {
        /**
         * Even though we told SensorManager to poll sensor with some time interval, actual interval
         * may vary (not much). Here we measure actual interval (for integrating or averaging measured
         * values).
         * @param reading Sensor measurement
         */
        void processSensorReading(Reading reading);

        /**
         * Inform if sensor's accuracy has changed
         * @param newAccuracy
         */
        void onSensorAccuracyChanged(int newAccuracy);

        /**
         * Allow to perform some action by derived class just before callback is register in that
         * sensor. Should be used for initializing processing algorithm.
         * Called on background thread for avoiding blocking thread, which receives readings.
         */
        void onBeforeStartingReadingSensor();

        /**
         * SensorReader stops reading values from that sensor, suspend some code in derived
         * class, which is responsible for processing read values.
         */
        void onAfterStoppedReadingSensor();
    }


    private Context context;

    private SensorManager sensorManager;
    private Sensor sensor;
    // receives readings from sensor
    private PublishSubject<SensorEvent> accelerationReadingPipe;
    // callback, receiving new values and updated of accuracy changes
    private SensorReadingCallback callback;

    private Subscription accelerationTracking;
    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            accelerationReadingPipe.onNext(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Observable.defer(() -> Observable.just(accuracy))
            .subscribeOn(Schedulers.computation())
                    .subscribe(t -> callback.onSensorAccuracyChanged(t));
        }
    };

    public SensorReader(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    /**
     * Specify type of sensor you want to monitor in derived class
     * @return
     */
    protected abstract int getSensorType();

    /**
     * Get sampling period for this kind of sensor
     * @return
     */
    protected abstract int getSamplingPeriod();

    /**
     * Setup processing algorithm and start polling sensor.
     * @throws IllegalStateException if device doesn't have that sensor
     */
    public void startReadingSensors() throws IllegalStateException {
        if (!isSensorPresent()) {
            throw new IllegalStateException("Lack of sensor: " + getSensorType());
        }
        stopAccelerationTracking();
        // create source, receiving measured values
        accelerationReadingPipe = PublishSubject.create();
        // subscribe to that source for processing readings on a background thread
        Observable<SensorEvent> sourceOnBackground =
        accelerationReadingPipe.asObservable()
                .observeOn(Schedulers.computation());
        // take first value for initializing previous timestamp
        sourceOnBackground.take(1)
                .subscribe(reading -> {
                    lastUpdateTime = reading.timestamp;
                });
        // and process readings starting from second one
        accelerationTracking =
            sourceOnBackground
                    .skip(1)
                    .subscribe(reading -> processAccelerationReading(reading));

        // get type of sensor
        int sensorType = getSensorType();
        // register listener for that sensor
        sensor = sensorManager.getDefaultSensor(sensorType);
        // initialize additional features in derived class
        getCallback().onBeforeStartingReadingSensor();
        int samplingPeriod = getSamplingPeriod();
        sensorManager.registerListener(listener, sensor, samplingPeriod);
    }

    public void stopReadingSensors() {
        stopAccelerationTracking();
        sensorManager.unregisterListener(listener);
        getCallback().onAfterStoppedReadingSensor();
    }

    /**
     * Check if phone has that kind of sensor.
     * @return
     */
    public boolean isSensorPresent() {
        return sensorManager.getDefaultSensor(getSensorType()) != null;
    }

    // Accessors

    public SensorReadingCallback getCallback() {
        return callback;
    }

    public void setCallback(SensorReadingCallback callback) {
        this.callback = callback;
    }

    public Context getContext() {
        return context;
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }
    // Private methods

    private void stopAccelerationTracking(){
        if (null != accelerationTracking && !accelerationTracking.isUnsubscribed()) {
            accelerationTracking.unsubscribe();
            accelerationTracking = null;
        }
    }
    private long lastUpdateTime;
    /**
     * Compute actual interval between samples and let actual logic handle read value.
     * It is called on background thread.
     * @param event
     */
    private void processAccelerationReading(SensorEvent event) {
        long timeInterval = (event.timestamp - lastUpdateTime) / 1000000L;
        lastUpdateTime = event.timestamp;
        callback.processSensorReading(new Reading(event, timeInterval));
    }
}
