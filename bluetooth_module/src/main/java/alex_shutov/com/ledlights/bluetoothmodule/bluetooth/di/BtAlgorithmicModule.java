package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import javax.inject.Named;
import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManagerImpl;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionCallbackReactive;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect.ReconnectManager;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.EstablishConnectionStrategy;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.ReconnectStrategy;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.select_another_device_strategy.SelectAnotherDeviceStrategy;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Alex on 10/27/2016.
 */
@Module
public class BtAlgorithmicModule {

    @Provides
    @Singleton
    ConnectionManagerImpl provideEstablishConnectionAlgorithm(
            @Named("ReconnectStrategy") EstablishConnectionStrategy reconnect,
            @Named("AnotherDeviceStrategy") EstablishConnectionStrategy anotherDevice,
            EstablishConnectionCallbackReactive currentStrategyWrapper){
        ConnectionManagerImpl algorithm =
                new ConnectionManagerImpl(reconnect, anotherDevice,
                        currentStrategyWrapper);
        return algorithm;
    }

    @Provides
    @Singleton
    ReconnectManager provideReconnectManager(){
        ReconnectManager reconnectManager = new ReconnectManager();
        return reconnectManager;
    }

    /**
     * Strategies for establishing connection
     */

    @Provides
    @Singleton
    @Named("ReconnectStrategy")
    EstablishConnectionStrategy provideReconnectStrategy( ) {
        ReconnectStrategy reconnectStrategy = new ReconnectStrategy();
        return reconnectStrategy;
    }

    @Provides
    @Singleton
    @Named("AnotherDeviceStrategy")
    EstablishConnectionStrategy provideAnotherDeviceSelectionStrategy(){
        SelectAnotherDeviceStrategy strategy = new SelectAnotherDeviceStrategy();
        return strategy;
    }


    @Provides
    EstablishConnectionCallbackReactive provideCallbackReactiveWrapper(){
        EstablishConnectionCallbackReactive callbackWrapper =
                new EstablishConnectionCallbackReactive();
        return callbackWrapper;
    }
}
