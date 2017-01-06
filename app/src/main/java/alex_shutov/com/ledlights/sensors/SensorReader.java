package alex_shutov.com.ledlights.sensors;

/**
 * Created by lodoss on 06/01/17.
 */

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Class, organizing reading sensor values. It use Sensor class for making actual reading and
 * SensorPoller for schediling reading interval.
 * PublishSubject run independently from Subscriber (hot Observable), so we use it to work with
 * sensors.
 */
public class SensorReader {

    private static final String LOG_TAG = SensorReader.class.getSimpleName();

    /**
     * Whenever value is read, it is given to this pipe first
     */
    private PublishSubject<SensorReading> sourceOfRawValues;
    /**
     * Source, emitting process values (average value or average value over time window).
     */
    private PublishSubject<SensorReading> sourceOfProcessedValues;
    /**
     * Remember last processed value. It is needed, because app may not want
     * to wait for a next value.
     */
    private BehaviorSubject<SensorReading> sourceOfLastValue;




    public SensorReader() {
        sourceOfRawValues = PublishSubject.create();
    }


    // accessors

    public Observable<SensorReading> getSourceOfRawValues() {
        return sourceOfRawValues;
    }

    public Observable<SensorReading> getSourceOfProcessedValues() {
        return sourceOfProcessedValues;
    }

    public Observable<SensorReading> getSourceOfLastValue() {
        return sourceOfLastValue;
    }


}
