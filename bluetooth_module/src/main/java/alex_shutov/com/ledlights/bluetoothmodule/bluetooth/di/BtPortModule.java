package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import android.content.Context;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.LogConnectorListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.LogScannerListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 25/08/16.
 */
@Module
public class BtPortModule {

    /**
     * Return Bluetooth connectivity port
     * @param context
     * @return
     */
    @Provides
    @Singleton
    BtConnAdapter provideBtConnPort(Context context){
        BtConnAdapter connAdapter = new BtConnAdapter(context);
        return connAdapter;
    }

    /**
     * Return Bluetoot scanning port
     * @param context
     * @return
     */
    @Provides
    @Singleton
    BtScanAdapter provideScanPort(Context context){
        BtScanAdapter scanAdapter = new BtScanAdapter(context);
        return scanAdapter;
    }

    /**
     *
     * @param context
     * @return
     */
    @Provides
    @Singleton
    LogConnectorListener provideLogConnectorListener(Context context){
        LogConnectorListener listener = new LogConnectorListener(context);
        return listener;
    }

    @Provides
    @Singleton
    LogScannerListener provideLogScannerListener(Context context){
        LogScannerListener listener = new LogScannerListener(context);
        return listener;
    }

}
