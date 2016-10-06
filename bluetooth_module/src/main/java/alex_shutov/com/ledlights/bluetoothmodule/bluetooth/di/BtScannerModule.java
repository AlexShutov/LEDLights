package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.LogScannerListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.esb.BtScanListenerEsbReceiveMapper;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.esb.BtScanListenerEsbSendMapper;
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

    @Provides
    @Singleton
    BtScanListenerEsbSendMapper provideBtScanListenerSendMapper(EventBus eventBus){
        BtScanListenerEsbSendMapper sendMapper = new BtScanListenerEsbSendMapper(eventBus);
        return sendMapper;
    }

    @Provides
    @Singleton
    BtScanListenerEsbReceiveMapper provideBtScanReceiveMapper(EventBus eventBus){
        BtScanListenerEsbReceiveMapper receiveMapper = new BtScanListenerEsbReceiveMapper(eventBus);
        return receiveMapper;
    }
    
}
