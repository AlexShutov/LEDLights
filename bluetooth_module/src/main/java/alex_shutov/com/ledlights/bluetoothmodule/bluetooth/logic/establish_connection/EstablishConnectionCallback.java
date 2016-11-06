package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection;

/**
 * Created by Alex on 11/4/2016.
 */

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;

/**
 * Interface for notifying logic, external to EstablishConnectionAlgorithm,
 * that connection is established or connection attempt have failed.
 */
public interface EstablishConnectionCallback {

    /**
     * Bluetooth subsystem successefully connected to some device:
     * - last connected device in case in power malfunction on device
     * - last device from device history, if last device is unavailable, or,
     * - new device was selected from UI and connection established with it.
     */
    void onConnectionEstablished(BtDevice connectedDevice);

    /**
     * Failed to connect to Bluetooth device, because none of above conditions
     * (see comment above) is satisfied, or, if adapter is off
     */
    void onAttemptFailed();
}
