package alex_shutov.com.ledlights.Bluetooth.di;

import android.content.Context;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.Bluetooth.BtConnectorPort.hex.BtConnAdapter;
import alex_shutov.com.ledlights.Bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.Bluetooth.BtScannerPort.hex.BtScanAdapter;
import alex_shutov.com.ledlights.Bluetooth.BtScannerPort.hex.BtScanPort;
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
        return null;
    }

}