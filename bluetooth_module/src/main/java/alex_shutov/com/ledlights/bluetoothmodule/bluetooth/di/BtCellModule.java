package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import android.content.Context;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.hex.BtStorageAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.hex.BtStoragePort;
import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 25/08/16.
 */

/**
 * creates ports used by BtLogicCell class
 */

@Module
public class BtCellModule {

    /**
     * Return Bluetooth connectivity port
     * @param context
     * @return
     */
    @Provides
    @Singleton
    BtConnAdapter provideBtConnAdapter(Context context){
        BtConnAdapter connAdapter = new BtConnAdapter(context);
        return connAdapter;
    }

    @Provides
    @Singleton
    BtConnPort provideBtConnPort(BtConnAdapter adapter){
        return adapter;
    }

    /**
     * Return Bluetooth scanning port
     * @param context
     * @return
     */
    @Provides
    @Singleton
    BtScanAdapter provideScanAdapter(Context context){
        BtScanAdapter scanAdapter = new BtScanAdapter(context);
        return scanAdapter;
    }

    @Provides
    @Singleton
    BtScanPort provideScanPort(BtScanAdapter scanAdapter){
        return scanAdapter;
    }

    /**
     * Create and return Bluetooth persistence port.
     * @param database Database implementation, created by another module (BtStorageModule).
     * Maybe it is not good, that this more abstract module knows about database, but doing this
     * way prevents creating another 'di' layer inside BtStoragePort. 
     * @return
     */
    @Provides
    @Singleton
    BtStorageAdapter provideBtStorageAdapter(BtDeviceDao database){
        BtStorageAdapter btStorageAdapter = new BtStorageAdapter(database);
        return btStorageAdapter;
    }

    @Provides
    @Singleton
    BtStoragePort provideBtStoragePort(BtStorageAdapter adapter){
        return adapter;
    }

    /**
     *
     * @return
     */
    @Provides
    @Singleton
    BtCommAdapter provideBtCommAdapter(){
        BtCommAdapter commAdapter = new BtCommAdapter();
        return commAdapter;
    }

    @Provides
    @Singleton
    BtCommPort provideBtCommPort(BtCommAdapter commAdapter){
        return commAdapter;
    }

}
