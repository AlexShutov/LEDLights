package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import android.content.Context;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.BtDeviceStorageManager;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDaoImpl;
import alex_shutov.com.ledlights.hex_general.db.StorageManager;
import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 11/10/16.
 */

/**
 * BtDeviceStorageManager and BtDeviceDaoImpl are Realm implementations, that is why
 * those two is in this module. Use another module if you want to use some other database.
 */
@Module
public class BtStorageModule {

    private static final String BLUETOOTH_DB_FILE_NAME = "bt_history.realm";

    /**
     * Create and return storage manger, managing Bluetooth device schema (realm).
     * We have to specify database file name explicitly or default database will be used instead.
     * @param context
     * @return
     */
    @Provides
    @Singleton
    StorageManager provideBtDeviceStorageManager(Context context){
        BtDeviceStorageManager storageManager = new BtDeviceStorageManager(context);
        storageManager.setDbFilename(BLUETOOTH_DB_FILE_NAME);
        storageManager.init();
        return storageManager;
    }

    /**
     * Provides Bluetooth device database implementation
     * @return
     */
    @Provides
    @Singleton
    BtDeviceDao provideBtDeviceDao(StorageManager storageManager){
        BtDeviceDao dao = new BtDeviceDaoImpl(storageManager);
        return dao;
    }

}
