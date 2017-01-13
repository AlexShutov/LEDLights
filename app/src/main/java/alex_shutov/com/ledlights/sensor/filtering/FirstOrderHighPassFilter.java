package alex_shutov.com.ledlights.sensor.filtering;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by lodoss on 12/01/17.
 */

public class FirstOrderHighPassFilter implements Filter {
    // stores filter's lowFrequencyComponent value
    private double lowFrequencyComponent;
    private double highFrequencyComponent;

    // alpha is calculated as t / (t + dT)
    // with t, the low-pass filter's time-constant
    // and dT, the event delivery rate
    final float alpha = 0.8f;


    public FirstOrderHighPassFilter() {
        reset();
    }

    @Override
    public double feedValue(double value) {
        lowFrequencyComponent = alpha * lowFrequencyComponent + (1 - alpha) * value;
        highFrequencyComponent = value - lowFrequencyComponent;
        return highFrequencyComponent;
    }

    @Override
    public void reset() {
        lowFrequencyComponent = 0;
        highFrequencyComponent = 0;
    }
}
