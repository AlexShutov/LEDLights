package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices;

import android.content.Context;
import android.util.Log;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.model.BtRealmModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtStorageModule;
import alex_shutov.com.ledlights.hex_general.db.StorageManager;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static alex_shutov.com.ledlights.hex_general.common.utils.impl.LogUtils.LOGI;

/**
 * Created by lodoss on 11/10/16.
 */
public class BtDeviceStorageManager extends StorageManager {

    public BtDeviceStorageManager(Context context){
        super(context);
    }

    @Override
    protected RealmConfiguration buildDbConfiguration() {
        RealmConfiguration dbConfig = null;
        if (getDbFilename() == null || getDbFilename().equals("")){
            // db file name is not specified, use default configuration
            LOGI(StorageManager.LOG_TAG, "File name not specified, using default configuration");
            dbConfig = new RealmConfiguration.Builder(getContext()).build();
        } else {
            dbConfig = new RealmConfiguration.Builder(getContext())
                    .name(getDbFilename())
                    // specify modules here
                    .setModules(new BtRealmModule())
                    .build();
        }
        return dbConfig;
    }
}
