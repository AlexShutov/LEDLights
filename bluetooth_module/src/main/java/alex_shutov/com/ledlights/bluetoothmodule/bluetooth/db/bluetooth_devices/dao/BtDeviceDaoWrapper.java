package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.dao;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.BtDeviceStorageManager;

/**
 * Created by lodoss on 11/10/16.
 */

/**
 * It uses BtDeviceStorageManager storage manager, managing Realm instance and keeping
 * Realm configuration. It is necessary, because database will be accessed from ThreadPool by
 * rxJava library, but Realm instances are Thread - confined (we have to create new Realm
 * instance for each thread). That is why in every method we obtain Realm instance first, do
 * some work and close that instance (by using storageManager) later.
 */
public class BtDeviceDaoWrapper {

    private BtDeviceStorageManager storageManager;
    private BtDeviceDao btDeviceDao;

    public BtDeviceDaoWrapper(BtDeviceStorageManager storageManager,
                              BtDeviceDao btDeviceDao) {
        this.storageManager = storageManager;
        this.btDeviceDao = btDeviceDao;

    }


}
