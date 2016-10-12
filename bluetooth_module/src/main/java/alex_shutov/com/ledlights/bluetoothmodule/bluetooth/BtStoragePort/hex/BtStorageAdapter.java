package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.hex;

import android.content.Context;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.hex_general.Adapter;
import alex_shutov.com.ledlights.hex_general.PortInfo;

/**
 * Created by lodoss on 12/10/16.
 */

public class BtStorageAdapter extends Adapter implements BtStoragePort {
    private static final String LOG_TAG = BtStorageAdapter.class.getSimpleName();

    // Actual implementation of bluetooth history database

    private BtDeviceDao historyDatabase;

    /**
     * Use constructor DI so we can use
     * @param database
     */
    public BtStorageAdapter(BtDeviceDao database){
        historyDatabase = database;
    }

    /**
     * Inherited from Adapter
     */

    @Override
    public void initialize() {

    }

    @Override
    public PortInfo getPortInfo() {
        PortInfo portInfo = new PortInfo();
        portInfo.setPortCode(PortInfo.PORT_BLUETOOTH_STORAGE);
        portInfo.setPortDescription("Stores history of all connected devices and info about last" +
                "device connection");
        return portInfo;
    }

    /**
     * Inherited from BtStoragePort
     */

    @Override
    public BtDeviceDao getHistoryDatabase() {
        return historyDatabase;
    }
}
