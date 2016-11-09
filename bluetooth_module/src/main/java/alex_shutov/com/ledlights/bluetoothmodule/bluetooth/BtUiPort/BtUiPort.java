package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtUiPort;

import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.hex_general.Port;

/**
 * Created by Alex on 11/8/2016.
 */
public interface BtUiPort extends Port {

    /**
     * Tell this port to show UI, which will display list of devices so user can pick one.
     * All Bluetooth communications has to be cancelled if user decide to pick another
     * device. UI port must also start discovery of not paired devices, thus it need BtScanPort.
     * To stop communication, it need BtConnPort
    */
    void showUiForPickingDevice();

    /**
     * Take into account list of devices from history. UI should indicate in some way if
     * any of paired devices or newly discovered device belong to device history (some check mark
     * or something)
     * @param historyDEvices
     */
    void acceptListOfHistoryDevices(List<BtDevice> historyDEvices);

    /**
     * Bluetooth adapter instantly return set of all paired devices, so we can display those
     * devices
     * @param pairedDevices
     */
    void acceptPairedDevices(List<BtDevice> pairedDevices);

    /**
     * At a instance when some algorithm decides to show this UI, we must stop any
     * Bluetooth communication and start new discovery process as soon as UI is initialized.
     * Any newly discovered Bluetooth device must be added into device list of not paired devices.
     * @param device
     */
    void addNewlyDiscoveredDevice(BtDevice device);

    /**
     * UI serves as just 'face' to selection algorithm. All actuall work is done by
     * corresponding strategy.
     *
     */
    void clearUI();
}
