package alex_shutov.com.ledlights.sensor.filtering;

import rx.Observable;

/**
 * Created by lodoss on 12/01/17.
 */

public interface Filter {
    /**
     * Give filter next vaue and read modified response
     * @param value input value
     * @return response
     */
    double feedValue(double value);

    /**
     * Clear current estimate
     */
    void reset();

}
