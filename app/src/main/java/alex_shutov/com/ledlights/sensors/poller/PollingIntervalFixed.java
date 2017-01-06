package alex_shutov.com.ledlights.sensors.poller;

/**
 * Created by lodoss on 06/01/17.
 */


import alex_shutov.com.ledlights.sensors.SensorReading;

/**
 * Starategy, which use the same time interval for all values - it is actually a stub.
 */
public class PollingIntervalFixed implements PollingIntervalAdjustmentStrategy {

    // remains the same
    private long currentInterval;

    @Override
    public void adjust(SensorReading lastImmediateValue, SensorReading averageValue,
                       long previousInterval) {
        currentInterval = previousInterval;
    }

    @Override
    public boolean isAdjustmentNeeded() {
        return false;
    }

    /**
     * get saved value
     * @return measurement interval
     */
    @Override
    public long getAdjustedInterval() {
        return currentInterval;
    }
}
