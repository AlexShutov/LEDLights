package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import android.content.Context;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.LogScannerListener;
import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 06/10/16.
 */

/**
 * Creates objects used by BtScanner port
 */

@Module
public class BtScannerModule {

    @Provides
    @Singleton
    LogScannerListener provideLogScannerListener(Context context){
        LogScannerListener listener = new LogScannerListener(context);
        return listener;
    }
}
