package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection;

import android.provider.ContactsContract;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;

/**
 * Created by Alex on 10/27/2016.
 */
public interface EstablishConnectionDataProvider extends DataProvider {
    /**
     * Connection algorithm need database, storing history connection for attempting to
     * connect to the last connected device
     * @return
     */
    BtDeviceDao provideHistoryDatabase();

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
}
