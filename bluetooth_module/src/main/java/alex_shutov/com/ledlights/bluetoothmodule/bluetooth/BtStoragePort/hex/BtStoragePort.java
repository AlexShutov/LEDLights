package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.hex;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.hex_general.Port;

/**
 * Created by lodoss on 12/10/16.
 */
public interface BtStoragePort extends Port {

    /**
     * Return concrete implementation of database, containing history of connected devices as
     * well as information about last connected device, last connection createPipeline and end time.
     * @return
     */
    BtDeviceDao getHistoryDatabase();

}
