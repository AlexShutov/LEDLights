package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import javax.inject.Named;
import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionAlgorithm;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionCallbackReactive;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.EstablishConnectionStrategy;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.ReconnectStrategy;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.SelectAnotherDeviceStrategy;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Alex on 10/27/2016.
 */
@Module
public class BtAlgorithmicModule {

    @Provides
    @Singleton
    EstablishConnectionAlgorithm provideEstablishConnectionAlgorithm(
            @Named("ReconnectStrategy") EstablishConnectionStrategy reconnect,
            @Named("AnotherDeviceStrategy") EstablishConnectionStrategy anotherDevice,
            EstablishConnectionCallbackReactive currentStrategyWrapper){
        EstablishConnectionAlgorithm algorithm =
                new EstablishConnectionAlgorithm(reconnect, anotherDevice,
                        currentStrategyWrapper);
        return algorithm;
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
