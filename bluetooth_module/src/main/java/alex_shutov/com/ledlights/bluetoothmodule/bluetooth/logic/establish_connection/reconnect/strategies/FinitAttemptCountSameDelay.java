package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect.strategies;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect.ReconnectSchedulingStrategy;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import static alex_shutov.com.ledlights.hex_general.common.utils.impl.LogUtils.LOGI;

/**
 * Created by lodoss on 20/12/16.
 */

public class FinitAttemptCountSameDelay extends ReconnectSchedulingStrategy {
    private static final String LOG_TAG = FinitAttemptCountSameDelay.class.getSimpleName();

    // number of reconnect attempts before failure
    private static final int DEFAULT_ATTEMPT_LIMIT = 3;
    // delay between reconnection attempts
    private static final int DEFAULT_RECONNECT_DELAY = 5;
    private static final TimeUnit DEFAULT_RECONNECT_TIME_UNITS = TimeUnit.SECONDS;

    private int attemptLimit = DEFAULT_ATTEMPT_LIMIT;
    // Reconnection delay
    private int reconnectDelay = DEFAULT_RECONNECT_DELAY;
    private TimeUnit reconnectDelayTimeUnit = DEFAULT_RECONNECT_TIME_UNITS;
    // number of attempts made
    private int currentAttemptCount;

    @Override
    public void onRestarted() {
    }

    @Override
    public void clearAttemptCounter() {
        currentAttemptCount = 0;
    }

    @Override
    public boolean shouldContinue() {
        return currentAttemptCount < attemptLimit;
    }

    /**
     * Schedule reconnect attempt after some delay.
     */
    @Override
    public Subscription startReconnectAttempt() {
        currentAttemptCount++;
        Observable<Boolean> task = Observable.just(true)
                .subscribeOn(Schedulers.computation())
                .delay(reconnectDelay, reconnectDelayTimeUnit);
        Subscription s = Observable.defer(() -> task)
                .subscribe(t -> {
                    LOGI(LOG_TAG, "Trying to connect again (" + currentAttemptCount +  " time)");
                    getDecoreeManager().attemptToEstablishConnection();
                });
        return s;
    }

    public int getAttemptLimit() {
        return attemptLimit;
    }

    public void setAttemptLimit(int attemptLimit) {
        this.attemptLimit = attemptLimit;
    }

    public int getReconnectDelay() {
        return reconnectDelay;
    }

    public void setReconnectDelay(int reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
    }

    public TimeUnit getReconnectDelayTimeUnit() {
        return reconnectDelayTimeUnit;
    }

    public void setReconnectDelayTimeUnit(TimeUnit reconnectDelayTimeUnit) {
        this.reconnectDelayTimeUnit = reconnectDelayTimeUnit;
    }

}
