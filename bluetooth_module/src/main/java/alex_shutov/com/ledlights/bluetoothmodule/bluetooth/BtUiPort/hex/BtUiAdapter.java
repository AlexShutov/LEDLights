package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtUiPort.hex;

import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtUiPort.BtUiPort;
import alex_shutov.com.ledlights.hex_general.Adapter;
import alex_shutov.com.ledlights.hex_general.PortInfo;

/**
 * Created by Alex on 11/8/2016.
 */

/**
 * this adapter Resembles UI port, which, in turn, is managed by
 * EstablishConnectionStrategy.
 * In that extend, strategy serve as Model, this Adapter- Presenter and actual UI -
 * View. View is using BtUiPortListener interface - this is analogy of Presenter
 * (port for incoming messages), but external world calls methods of this BtUiPort.
 * This adapter will store all device data and will return if when requested.
 * Here is a tricky part - Device might change orientation and UI has to be recreated and all
 * data in it will be lost. So, we have to store it somewhere else  - in this case in
 * this adapter. Actual View will implement the same interface, but, Adapter has all data
 * received from different methods and it will pass these values into View when View is
 * attached to it.
 */
public class BtUiAdapter extends Adapter implements BtUiPort {
    private static final String LOG_TAG = BtUiAdapter.class.getSimpleName();
    private static final PortInfo portInfo;
    static {
        portInfo = new PortInfo();
        portInfo.setPortCode(PortInfo.PORT_BLUETOOTH_UI_PORT);
        portInfo.setPortDescription("Port for selecting device by UI");
    }

    @Override
    public void initialize() {

    }

    @Override
    public PortInfo getPortInfo() {
        return portInfo;
    }

    /**
     * Inherited from BtUiPort
     */

    /**
     * Launch Activity from here
     */
    @Override
    public void showUiForPickingDevice() {

    }

    /**
     * Store list of device history and give it to UI.
     * @param historyDEvices
     */
    @Override
    public void acceptListOfHistoryDevices(List<BtDevice> historyDEvices) {

    }

    /**
     * Send list of paired devices via EventBus.
     * @param pairedDevices
     */
    @Override
    public void acceptPairedDevices(List<BtDevice> pairedDevices) {

    }

    @Override
    public void addNewlyDiscoveredDevice(BtDevice device) {

    }

    @Override
    public void clearUI() {

    }
}
