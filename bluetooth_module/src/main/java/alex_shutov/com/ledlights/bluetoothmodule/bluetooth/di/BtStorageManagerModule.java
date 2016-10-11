package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import android.content.Context;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.BtDeviceStorageManager;
import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 11/10/16.
 */
@Module
public class BtStorageManagerModule  {

    @Provides
    @Singleton
    BtDeviceStorageManager provideBtDeviceStorageManager(Context context){
        BtDeviceStorageManager storageManager = new BtDeviceStorageManager(context);
        return storageManager;
    }



}
