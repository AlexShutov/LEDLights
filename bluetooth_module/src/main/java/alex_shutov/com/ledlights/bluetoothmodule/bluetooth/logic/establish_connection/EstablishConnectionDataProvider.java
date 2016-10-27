package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection;

import android.provider.ContactsContract;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;

/**
 * Created by Alex on 10/27/2016.
 */
public interface EstablishConnectionDataProvider extends DataProvider {

    BtDeviceDao provideHistoryDatabase();
}
