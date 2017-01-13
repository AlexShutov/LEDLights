package alex_shutov.com.ledlights.sensor;

import android.content.Context;

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

    // receives readings from sensor
    private PublishSubject<Reading> inputReadingPipe;
    // callback, receiving new values and updated of accuracy changes
    private SensorReadingCallback callback;

    private Subscription accelerationTracking;


    public SensorReader(Context context) {
        this.context = context;
    }


    protected abstract void startPollingHardwareSensor();

    protected abstract void stopPollingHardwareSensor();

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
        stopAccelerationTracking();
        // create source, receiving measured values
        inputReadingPipe = PublishSubject.create();
        // subscribe to that source for processing readings on a background thread
        Observable<Reading> sourceOnBackground =
        inputReadingPipe.asObservable()
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

        startPollingHardwareSensor();
    }

    public void stopReadingSensors() {
        stopAccelerationTracking();
        stopPollingHardwareSensor();
        getCallback().onAfterStoppedReadingSensor();
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
     * @param reading
     */
    private void processAccelerationReading(Reading reading) {
        long timeInterval = (reading.timestamp - lastUpdateTime) / 1000000L;
        lastUpdateTime = reading.timestamp;
        // set time interval to reading
        reading.timeInterval = timeInterval;
        callback.processSensorReading(reading);
    }

    protected PublishSubject<Reading> getReadingPipe() {
        return inputReadingPipe;
    }
}
