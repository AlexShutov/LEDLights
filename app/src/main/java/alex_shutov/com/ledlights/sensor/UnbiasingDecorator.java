package alex_shutov.com.ledlights.sensor;

import android.content.Context;
import android.util.Log;

import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static alex_shutov.com.ledlights.sensor.SensorReader.*;

/**
 * Created by lodoss on 12/01/17.
 */

/**
 * As it has turned out, that some devices has offset of readings (because of miscalibration,
 * perhaps). It is crucial for acceleration, because, when integrated, it will lead to
 * linear increase in speed.
 * I will tackle that problem by measuring offset when process transient process of
 * filter is complete and then will substract that value from all readings.
 */
public class UnbiasingDecorator extends SensorReaderDecorator implements SensorReadingCallback {
    /**
     * Assume that transient process will end in high pass filter after this number of readings.
     */
    private static final int NUMBER_OF_READINGS_FOR_TRANSIENT_PROCESS = 10;
    /**
     * How many samples we should average for getting not fluctuating bias
     */
    private static final int BIAS_AVERAGING_COUNT = 5;


    private SensorReader decoree;

    // source of readings with error
    private PublishSubject<Reading> biasedReadingSource;
    // source of calibrated values
    private PublishSubject<Reading> notBiasedReadingsSource;
    // connects input and ouput pipes
    private CompositeSubscription algorithmConnections;
    private Reading bias;

    public UnbiasingDecorator(Context context) {
        super(context);
        biasedReadingSource = PublishSubject.create();
        notBiasedReadingsSource = PublishSubject.create();
    }

    /**
     * Inherited from SensorReadingCallback
     */

    /**
     * Decorated reader pass reading to this method. Tunnel it to the
     * 'biased pipe' so it will be processed further down the pipe.
     * @param reading Sensor measurement
     */
    @Override
    public void processSensorReading(Reading reading) {
        biasedReadingSource.onNext(reading);
    }

    /**
     * Reset bias- related algorithms (determining and substraction of bias) by unsubscribing
     * from corresponding subscriptions first and then start determining bias, and, when
     * it is known, start substracting that bias and dispatching messages.
     * @throws IllegalStateException
     */
    @Override
    public void startReadingSensors() throws IllegalStateException {
        resetBiasCalibration();
        startWaitingForEndOfTransientProcess();
        decoree.startReadingSensors();
    }

    @Override
    public void stopReadingSensors() {
        decoree.stopReadingSensors();
        resetBiasCalibration();
    }

    // accessors

    public SensorReader getDecoree() {
        return decoree;
    }

    public void setDecoree(SensorReader decoree) {
        this.decoree = decoree;
        // this is a decorator, use it as callback for redirecting calls to actual callback.
        decoree.setCallback(this);
    }

    // private methods

    /**
     * Assume that filter, substracting gravity need some time for establishing stable mode
     */
    private void startWaitingForEndOfTransientProcess() {
        algorithmConnections = new CompositeSubscription();
        Subscription biasDeterminingSubscription = biasedReadingSource
                .asObservable()
                // average bias by 5 readings
                .buffer(BIAS_AVERAGING_COUNT, NUMBER_OF_READINGS_FOR_TRANSIENT_PROCESS)
                .take(1)
                .subscribe(biasReadings -> {
                    // calculate average bias
                    bias = new Reading();
                    for (Reading r : biasReadings) {
                        bias.values[0] += r.values[0];
                        bias.values[1] += r.values[1];
                        bias.values[2] += r.values[2];
                    }
                    bias.values[0] /= BIAS_AVERAGING_COUNT;
                    bias.values[1] /= BIAS_AVERAGING_COUNT;
                    bias.values[2] /= BIAS_AVERAGING_COUNT;
                    startSubstractingBias();
                });
        algorithmConnections.add(biasDeterminingSubscription);
        // connect pipes so we can receive incoming readings
        connectOutputPipeToCallback();
    }

    /**
     * Check if there is connection between biased and not biased sources and sever it.
     * Clear bias value too.
     */
    private void resetBiasCalibration() {
        if (null != algorithmConnections) {
            algorithmConnections.unsubscribe();
        }
        bias = null;
    }

    private void startSubstractingBias() {
        Subscription biasRemovalSubscription =
                biasedReadingSource.asObservable()
                // remove bias from sensor reading
                .map(biasedReading -> {
                    Reading notBiased = new Reading();
                    notBiased.values[0] = biasedReading.values[0] - bias.values[0];
                    notBiased.values[1] = biasedReading.values[1] - bias.values[1];
                    notBiased.values[2] = biasedReading.values[2] - bias.values[2];
                    notBiased.timeInterval = biasedReading.timeInterval;
                    return notBiased;
                })
                // and feed process reading to output source
                .subscribe(notBiasedReadingsSource);
        algorithmConnections.add(biasRemovalSubscription);
    }

    private void connectOutputPipeToCallback() {
        Subscription inputPipeConnection = notBiasedReadingsSource
                .asObservable()
                // it is unknown which thread reading came from
                .observeOn(Schedulers.computation())
                .subscribe(reading -> {
                    getCallback().processSensorReading(reading);
                });
        algorithmConnections.add(inputPipeConnection);
    }

    /**
     * Substract bias
     * @param reading
     */
    void subsctractDrift (Reading reading) {
        reading.values[0] = reading.values[0] - bias.values[0];
        reading.values[1] = reading.values[1] - bias.values[1];
        reading.values[2] = reading.values[2] - bias.values[2];
    }

}
