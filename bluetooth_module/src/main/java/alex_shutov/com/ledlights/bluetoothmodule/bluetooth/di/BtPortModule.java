package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import android.content.Context;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 25/08/16.
 */
@Module
public class BtPortModule {

    @Provides
    @Singleton
    BtConnPort provideBtConnPort(Context context){
        BtConnAdapter connAdapter = new BtConnAdapter(context);
        return connAdapter;
    }

    @Provides
    @Singleton
    BtScanPort provideScanPort(Context context){
        BtScanPort scanPort = new BtScanAdapter(context);
        return scanPort;
    }

}
