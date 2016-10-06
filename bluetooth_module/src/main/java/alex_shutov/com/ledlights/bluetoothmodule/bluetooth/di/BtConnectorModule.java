package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.LogConnectorListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnListenerEsbReceiveMapper;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnListenerEsbSendMapper;
import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 06/10/16.
 */

/**
 * Creates objects used by BtConnector port
 */

@Module
public class BtConnectorModule {
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

    /**
     * Instantiates ESB send mapper for Bluetooth port, which maps port's listener's methods
     * and posts events on EventBus. EvenButs is constructor- injected by SystemModule.
     * @param eventBus
     * @return
     */
    @Provides
    @Singleton
    BtConnListenerEsbSendMapper provideSendMapper(EventBus eventBus){
        BtConnListenerEsbSendMapper sendMapper = new BtConnListenerEsbSendMapper(eventBus);
        return sendMapper;
    }

    /**
     * Create ESB receive mapper for Bluetooth port, listening for EventBus events and mapping
     * those events to registered lisener's calls.
     * @param eventBus
     * @return
     */
    @Provides
    @Singleton
    BtConnListenerEsbReceiveMapper provideReceiveMapper(EventBus eventBus){
        BtConnListenerEsbReceiveMapper receiveMapper = new BtConnListenerEsbReceiveMapper(eventBus);
        return receiveMapper;
    }

}
