package alex_shutov.com.ledlights.Bluetooth.BtScannerPort.hex;

import alex_shutov.com.ledlights.HexGeneral.Port;

/**
 * Created by lodoss on 27/07/16.
 */
public interface BtScanPort extends Port {

    boolean isBluetoothEnabled();

    void turnOnBluetooth() throws IllegalStateException;

    void turnOffBluetooth() throws IllegalStateException;

    /**
     * Ask Android to make this device visible for another not paired devices. Android will
     * show dialog asking permission from user.
     */
    void makeDeviceDiscoverable();

    /**
     * BluetoothAdapter return 'Set<BluetoothDevice>' of paired devices.
     * After operation is complete, 'BtScanPort' will pass resulting set into 'BtScanPortListener'
     * This implementation use rxJava internally.
     */
    void getPairedDevices();

    /**
     * Start scan for available BT devices
     */
    void startDiscovery();

    /**
     * Stop scanning for devices
     */
    void stopDiscovery();

}
