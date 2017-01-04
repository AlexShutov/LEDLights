package alex_shutov.com.ledlights.service.device_comm;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;

/**
 * Created by lodoss on 04/01/17.
 */

public interface DeviceControlFeedback  {
    /**
     * Bluetooth device connected
     * @param device
     */
    void onConnected(BtDevice device);

    /**
     * Connection to bluetooth device is lost or attempt failed. In response app use
     * fake device.
     */
    void onDummyDeviceSelected();

}
