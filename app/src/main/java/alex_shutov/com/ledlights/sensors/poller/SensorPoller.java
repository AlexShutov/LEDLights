package alex_shutov.com.ledlights.sensors.poller;

/**
 * Created by lodoss on 06/01/17.
 */

import alex_shutov.com.ledlights.sensors.SensorReading;
import rx.Observable;

/**
 * Decides when to poll the sensor next time. We could just poll sensor with
 * uniform time intervals, but, when readings doesn't change, we don't need to do
 * useless work - it drains battery. Instead, we could use value - dependent poller.
 * Settings of that poller can be specified for each sensor.
 * For example, when user move with small speed, we don't need to measure speed tens times per
 * second - once in a few second is enough. But, when speed is high, we need to have as much
 * relevant readings as possible, so that interval decrease.
 */
public abstract class SensorPoller {

    /**
     * Start polling with current settings.
     * @return triggering source, emitting value every time we need to measure next value.
     */
    public abstract Observable<Boolean> startPolling();


    /**
     * * .startPolling() method return source, emitting triggering values.
     * When next that value is emitter, app start measurement. After measure is complete,
     * it call this method.
     * This is a safeguard for the case, when measurement take longer than time interval
     * between measurements, specified by that SensorPoller.
     * If that happens, poller just skips values until measurement is done.
     * @param measuredValue Value, obtained as result of previous measurement.
     *                      Poller might use it for correcting interval between measurements.
     */
    public abstract void onMeasureComplete(SensorReading measuredValue);



}
