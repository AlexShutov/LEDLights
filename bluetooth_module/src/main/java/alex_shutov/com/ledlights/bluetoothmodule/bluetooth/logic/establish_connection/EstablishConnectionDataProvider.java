package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection;

import android.provider.ContactsContract;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtUiPort.BtUiPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;

/**
 * Created by Alex on 10/27/2016.
 */
public interface EstablishConnectionDataProvider extends DataProvider {

    /**
     * Manages connection to device, needed of coarse
     * @return
     */
    BtConnPort  provideBtConnPort();

    /**
     * In case there is no devices in database history, we have to show UI, listing
     * all currently available devices so user can select one. To do the scanning, we need
     * BtScanPort. According strategy will subscribe to 'device found' event and update UI
     * @return
     */
    BtScanPort provideBtScanPort();

    /**
     * Get reference to implementation of UI port. Ui port is needed to select
     * device from history or newly, not paired device.
     * @return
     */
    BtUiPort provideBtUiPort();
}
