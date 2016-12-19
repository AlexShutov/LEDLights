package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect;

/**
 * Created by Alex on 12/18/2016.
 */

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManager;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManagerCallback;

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

    private ConnectionManager decoreeManager;
    private ConnectionManagerCallback decoreeCallback;


    /**
     * Called by wrapping entity whenever connection to device is lost.
     */
    public void onConnectionLost() {

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
        return decoreeManager.isAttemptingToConnect();
    }

    /**
     * Stop all currently active connection attempts and deactivate this manager
     */
    @Override
    public void stopConnecting() {
        deactivate();
        decoreeManager.stopConnecting();
    }

    @Override
    public void selectDeviceByUi() {
        deactivate();
        resetState();
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
        deactivate();
        resetState();
        decoreeCallback.onConnectionEstablished(connectedDevice);
    }

    /**
     * Process failed attempt. Here all logic begins.
     * Stub for now.
     */
    @Override
    public void onAttemptFailed() {
        decoreeCallback.onAttemptFailed();
    }

    /**
     * If operation not supported, stop this manager, reset its state and inform application of
     * failure
     */
    @Override
    public void onUnsupportedOperation() {
        deactivate();
        resetState();
        decoreeCallback.onUnsupportedOperation();

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

    /**
     * Own private methods
     */

    /**
     * State of manager gets reset when attempt limit is reached or if user
     * ask to choose device by UI.
     */
    private void resetState() {

    }

    /**
     *
     */
    private void deactivate() {

    }

    /**
     *
     */
    private void activate() {

    }


}
