package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtUiPort;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.hex_general.PortListener;

/**
 * Created by Alex on 11/8/2016.
 */
public interface BtUiPortListener extends PortListener {

    /**
     * User choose some device from one of device list - either from connection history,
     * or from newly discovered devices.
     * @param device
     */
    void onUserChooseDevice(BtDevice device);

    /**
     * Take some action if user refused to pick device from list or closed the Activity.
     */
    void onCancelledByUser();

    /**
     * UI is shown and it need to start discovery for Bluetooth devices. But we need to
     * stop any data transfer or ongoing connection requests first.
     */
    void stopBluetoothCommunication();


}
