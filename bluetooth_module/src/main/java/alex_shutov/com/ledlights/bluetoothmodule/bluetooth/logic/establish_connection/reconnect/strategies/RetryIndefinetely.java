package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect.strategies;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect.ReconnectSchedulingStrategy;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by lodoss on 20/12/16.
 */

/**
 * Strategy, which will try to connect over and over and over again until connection is established.
 */
public class RetryIndefinetely extends ReconnectSchedulingStrategy {
    private static final String LOG_TAG = RetryIndefinetely.class.getSimpleName();

    // delay between reconnection attempts
    // By default wait 10 seconds and then try to connect again
    private static final int DEFAULT_RECONNECT_DELAY = 10;
    private static final TimeUnit DEFAULT_RECONNECT_TIME_UNITS = TimeUnit.SECONDS;

    // Reconnection delay
    private int reconnectDelay = DEFAULT_RECONNECT_DELAY;
    private TimeUnit reconnectDelayTimeUnit = DEFAULT_RECONNECT_TIME_UNITS;

    /**
     * Here is no state to be cleared.
     */
    @Override
    public void onRestarted() {}

    /**
     * This strategy doesn't count attempts
     */
    @Override
    public void clearAttemptCounter() {
    }

    /**
     * This strategy will run indefinitely.
     * @return
     */
    @Override
    public boolean shouldContinue() {
        return true;
    }

    /**
     * Schedule another attempt.
     * @return
     */
    @Override
    public Subscription startReconnectAttempt() {
        Observable<Boolean> task = Observable.just(true)
                .subscribeOn(Schedulers.computation())
                .delay(reconnectDelay, reconnectDelayTimeUnit);
        Subscription s = Observable.defer(() -> task)
                .subscribe(t -> {
                    Log.i(LOG_TAG, "Attempting to connect again.");
                    getDecoreeManager().attemptToEstablishConnection();
                });
        return s;
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
