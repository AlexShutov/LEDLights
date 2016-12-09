package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy;

import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionDataProvider;
import alex_shutov.com.ledlights.hex_general.Port;

/**
 * Created by Alex on 11/8/2016.
 */
public interface BtUiDeviceSelectionInterface extends Port {

    /**
     * Tell this port to show UI, which will display list of devices so user can pick one.
     * All Bluetooth communications has to be cancelled if user decide to pick another
     * device. UI port must also createPipeline discovery of not paired devices, thus it need BtScanPort.
     * To stop communication, it need BtConnPort
    */
    void showUiForPickingDevice();

    /**
     * This Ui port follows a convention - UI can't close itself (Activity.finish()).
     * It can only tell Strategy or any other logic entity that user refused to select a device
     * and that logic has to decide what to do next.
     */
    void closeUiForPickingDevice();

    /**
     * Take into account list of devices from history. UI should indicate in some way if
     * any of paired devices or newly discovered device belong to device history (some check mark
     * or something)
     * @param historyDevices
     */
    void acceptListOfHistoryDevices(List<BtDevice> historyDevices);

    /**
     * Bluetooth adapter instantly return set of all paired devices, so we can display those
     * devices
     * @param pairedDevices
     */
    void acceptPairedDevices(List<BtDevice> pairedDevices);

    /**
     * At a instance when some algorithm decides to show this UI, we must stop any
     * Bluetooth communication and createPipeline new discovery process as soon as UI is initialized.
     * Any newly discovered Bluetooth device must be added into device list of not paired devices.
     * @param device
     */
    void addNewlyDiscoveredDevice(BtDevice device);

    /**
     * UI serves as just 'face' to selection algorithm. All actuall work is done by
     * corresponding strategy.
     * Use this method for clearing all previously saved state of UI (optional)
     */
    void clearUI();
}
