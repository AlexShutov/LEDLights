package alex_shutov.com.ledlights.sensors.poller;

/**
 * Created by lodoss on 06/01/17.
 */

import alex_shutov.com.ledlights.sensors.SensorReading;

/**
 * Decides if we need to adjust polling interval based on previous readings
 */
public interface PollingIntervalAdjustmentStrategy {

    /**
     * Call this method first for providing actual data to this strategy
     * @param lastImmediateValue
     * @param averageValue
     * @param previousInterval
     */
    void adjust(SensorReading lastImmediateValue,
                SensorReading averageValue,
                long previousInterval);

    /**
     * Let strategy decide if values changed big enough so adjustment is needed
     * @return
     */
    boolean isAdjustmentNeeded();

    /**
     * Calculate new time interval between measurements based on provided values.
     * @return  New interval between measurements.
     */
    long getAdjustedInterval();

}
