package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices;

import android.content.Context;
import android.util.Log;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtStorageManagerModule;
import alex_shutov.com.ledlights.hex_general.db.StorageManager;
import io.realm.Realm;
import io.realm.RealmConfiguration;

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
            Log.i(StorageManager.LOG_TAG, "File name not specified, using default configuration");
            dbConfig = new RealmConfiguration.Builder(getContext()).build();
        } else {
            dbConfig = new RealmConfiguration.Builder(getContext())
                    .name(getDbFilename())
                    // specify modules here
                    .setModules(new BtStorageManagerModule())
                    .build();
        }
        return dbConfig;
    }
}
