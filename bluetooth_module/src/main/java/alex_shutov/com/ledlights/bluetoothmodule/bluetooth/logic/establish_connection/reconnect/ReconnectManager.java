package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect;

/**
 * Created by Alex on 12/18/2016.
 */

import android.util.Log;

import java.util.concurrent.TimeUnit;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManager;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManagerCallback;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * ReconnectManager is a decorator for ConnectionManager, which purpose is to intercept
 * callback events from decoree and modify those events in some way. For example, assume
 * that user asked to ConnectionManager to connect to some Bluetooth device. But, that device
 * is unavailable and ConnectionManager could not connect to it. onConnectAttemptFailed()
 * method is called on callback by ConnectionManager. That ReconnectManager decorator has
 * to intercept that event and try to connect again after some time (second or two).
 * If that attempt fail too, manager might try again after some time (perhaps, exponential
 * back off). Finally, if attempt limit is reached, ReconnectManager inform appp by
 * calling decorated callback.
 */
public class ReconnectManager implements ConnectionManager, ConnectionManagerCallback {
    private static final String LOG_TAG = ReconnectManager.class.getSimpleName();
    // number of reconnect attempts before failure
    private static final int DEFAULT_ATTEMPT_LIMIT = 3;
    // delay bettween reconnection attempts
    private static final int DEFAULT_RECONNECT_DELAY = 1;
    private static final TimeUnit DEFAULT_RECONNECT_TIME_UNITS = TimeUnit.SECONDS;
    private ConnectionManager decoreeManager;
    private ConnectionManagerCallback decoreeCallback;

    private int attemptLimit = DEFAULT_ATTEMPT_LIMIT;
    // Reconnection delay
    private int reconnectDelay = DEFAULT_RECONNECT_DELAY;
    private TimeUnit reconnectDelayTimeUnit = DEFAULT_RECONNECT_TIME_UNITS;
    // number of attempts made
    private int currentAttemptCount;
    // if manager is active right now. It may not if user want to select device manually.
    private boolean isActive;

    private boolean isOnPause;
    // active if reconnect attempt is pending
    private Subscription retryDelayConnection;

    /**
     * Called by wrapping entity whenever connection to device is lost.
     */
    public void onConnectionLost() {
        Log.i(LOG_TAG, "Connection lost event received by ReconnectManager");
        if (!isOnPause) {
            activate();
            // start reconnect attempt after some delay
            suspendReconnectAttempt();
            retryDelayConnection = startReconnectAttempt();
        }
    }

    /**
     * Inherited from ConnectionManager
     */

    /**
     * Activate this reconnect manager first and then inform decorated manager
     * that it can start attempting to connect.
     */
    @Override
    public void attemptToEstablishConnection() {
        activate();
        decoreeManager.attemptToEstablishConnection();
    }

    /**
     * decorated method return true if adapter itself is connecting right now, not
     * state of decorated strategy
     * @return
     */
    @Override
    public boolean isAttemptingToConnect() {
        return isActive && decoreeManager.isAttemptingToConnect();
    }

    /**
     * Stop all currently active connection attempts and deactivate this manager
     */
    @Override
    public void stopConnecting() {
        deactivate();
        isOnPause = false;
        decoreeManager.stopConnecting();
    }

    @Override
    public void selectDeviceByUi() {
        deactivate();
        isOnPause = true;
        decoreeManager.selectDeviceByUi();
    }

    /**
     * Inherited from ConnectionManagerCallback
     */

    /**
     * Connection established successfully, no need in retying. Deactivate this manager  and
     * reset its state
     * @param connectedDevice
     */
    @Override
    public void onConnectionEstablished(BtDevice connectedDevice) {
        if (!isOnPause) {
            deactivate();
        } else {
            isOnPause = false;
        }
        decoreeCallback.onConnectionEstablished(connectedDevice);
    }

    /**
     * Process failed attempt. Here all logic begins.
     * Stub for now.
     */
    @Override
    public void onAttemptFailed() {
        // manager is active if connection was lost or app requested to establish connection
        // check if we not reached attempt count limit too.
        if (isActive && !isOnPause) {
            if (currentAttemptCount < attemptLimit) {
                 suspendReconnectAttempt();
                 retryDelayConnection = startReconnectAttempt();
            } else {
                deactivate();
                decoreeCallback.onAttemptFailed();
            }
        } else {
            isOnPause = false;
            // in case user picked device by UI
            decoreeCallback.onAttemptFailed();
        }
    }
    /**
     * If operation not supported, stop this manager, reset its state and inform application of
     * failure
     */
    @Override
    public void onUnsupportedOperation() {
        deactivate();
        decoreeCallback.onUnsupportedOperation();
    }

    /**
     * Schedule reconnect attempt after some delay.
     */
    private Subscription startReconnectAttempt() {
        currentAttemptCount++;
        Observable<Boolean> task = Observable.just(true)
                .subscribeOn(Schedulers.computation())
                .delay(reconnectDelay, reconnectDelayTimeUnit);
        Subscription s = Observable.defer(() -> task)
                .subscribe(t -> {
                    Log.i(LOG_TAG, "Trying to connect again (" + currentAttemptCount +  " time)");
                    decoreeManager.attemptToEstablishConnection();
                });
        return s;
    }

    /**
     * Accessors
     */

    /**
     * When application set decorated manager, this retry manager need to intercept callbacks
     * from that manager. To do so, it stores original callback and set itself as callback to
     * decorated manager.
     * @param decoreeManager
     */
    public void setDecoreeManager(ConnectionManager decoreeManager) {
        this.decoreeManager = decoreeManager;
        this.decoreeManager.setCallback(this);
    }

    @Override
    public void setCallback(ConnectionManagerCallback callback) {
        this.decoreeCallback = callback;
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

    /**
     * Own private methods
     */

    /**
     *
     */
    private void deactivate() {
        Log.i(LOG_TAG, "Deactivating ReconnectManager");
        resetState();
        isActive = false;
    }

    /**
     *
     */
    private void activate() {
        Log.i(LOG_TAG, "Activating ReconnectManager");
        resetState();
        isActive = true;
    }

    /**
     * State of manager gets reset when attempt limit is reached or if user
     * ask to choose device by UI.
     */
    private void resetState() {
        Log.i(LOG_TAG, "Resetting state of ReconnectManager");
        currentAttemptCount = 0;
        suspendReconnectAttempt();
    }

    private void suspendReconnectAttempt() {
        if (retryDelayConnection != null && !retryDelayConnection.isUnsubscribed()) {
            retryDelayConnection.unsubscribe();
            retryDelayConnection = null;
        }
    }




}
