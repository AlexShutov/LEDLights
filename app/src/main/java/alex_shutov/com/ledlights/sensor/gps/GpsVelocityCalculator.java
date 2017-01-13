package alex_shutov.com.ledlights.sensor.gps;

import android.content.Context;
import android.location.Location;

import alex_shutov.com.ledlights.sensor.Reading;
import alex_shutov.com.ledlights.sensor.SensorReaderDecorator;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

/**
 * Created by lodoss on 13/01/17.
 */

/**
 * Calculates average velocity from two location values and time interval between those
 * locations
 */
public class GpsVelocityCalculator extends SensorReaderDecorator {

    private PublishSubject<Reading> inputPipe;
    private Subscription algorithmSubscriptions;

    public GpsVelocityCalculator(Context context) {
        super(context);
        inputPipe = PublishSubject.create();
    }

    /**
     * We need previous and current reading. Here I use .zip() operator,
     * applied to orginal sequence and that sequence, shifted by 1 element.
     * As result, we will always has two elements - previous and current one.
     * .zip() operator wait until both values is emitted by sources.
     */
    @Override
    public void onBeforeStartingReadingSensor() {
        super.onBeforeStartingReadingSensor();
        // setup algorithm, it is similar to the one in SensorReader
        algorithmSubscriptions =
                Observable.zip(inputPipe,
                        inputPipe.skip(1), (v1, v2) -> {
                            // get two consecutive samples and calculate speed from them
                            Reading speed = calculateAverageSpeed(v1, v2);
                            return speed;
                })
                .subscribe(speed -> {
                    // get speed to the caller
                    getCallback().processSensorReading(speed);
                });
    }

    @Override
    public void onAfterStoppedReadingSensor() {
        super.onAfterStoppedReadingSensor();
        algorithmSubscriptions.unsubscribe();
        algorithmSubscriptions = null;
    }



    @Override
    public void processSensorReading(Reading reading) {
        inputPipe.onNext(reading);
    }

    private Reading calculateAverageSpeed(Reading first, Reading second) {

        double latBeg = first.values[0];
        double lonBeg = first.values[1];
        double latEnd = second.values[0];
        double lonEnd = second.values[1];
        float[] tmp = new float[1];
        // magic stuff happens here (Earth modelled as WGS84 ellipsoid)
        Location.distanceBetween(latBeg, lonBeg, latEnd, lonEnd, tmp);
        float distance = tmp[0];
        // second reading is the last, get time interval from it (it is in milliseconds,
        // but we need a meters per seconds)
        double timeInterval = second.timeInterval / 1000d;
        // calculate average speed
        double averageSpeed = distance / timeInterval;
        Reading speedResult = new Reading();
        speedResult.timestamp = second.timestamp;
        speedResult.timeInterval = second.timeInterval;
        speedResult.values[0] = averageSpeed;
        return speedResult;
    }
}
