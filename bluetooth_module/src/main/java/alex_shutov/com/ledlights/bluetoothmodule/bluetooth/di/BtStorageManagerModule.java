package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import android.content.Context;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.BtDeviceStorageManager;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.dao.BtDeviceDaoImpl;
import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 11/10/16.
 */
@Module
public class BtStorageManagerModule  {

    /**
     * Create and return storage manger, managing Bluetooth device schema (realm)
     * @param context
     * @return
     */
    @Provides
    @Singleton
    BtDeviceStorageManager provideBtDeviceStorageManager(Context context){
        BtDeviceStorageManager storageManager = new BtDeviceStorageManager(context);
        return storageManager;
    }

    /**
     * Provides Bluetooth device database implementation
     * @return
     */
    @Provides
    @Singleton
    BtDeviceDao provideBtDeviceDao(BtDeviceStorageManager storageManager){
        BtDeviceDao dao = new BtDeviceDaoImpl(storageManager);
        return dao;
    }
}
