package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import javax.inject.Named;
import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManagerImpl;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionCallbackReactive;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect.ReconnectManager;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.EstablishConnectionStrategy;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.ReconnectStrategy;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.SelectAnotherDeviceStrategy;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect.ReconnectSchedulingStrategy;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect.strategies.FinitAttemptCountSameDelay;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect.strategies.RetryIndefinetely;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.transfer_data.TransferManagerBase;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.transfer_data.TransferManagerImpl;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.transfer_data.TransferManagerMock;
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
            @Named("ReconnectSchedulingStrategy") EstablishConnectionStrategy reconnect,
            @Named("AnotherDeviceStrategy") EstablishConnectionStrategy anotherDevice,
            EstablishConnectionCallbackReactive currentStrategyWrapper){
        ConnectionManagerImpl algorithm =
                new ConnectionManagerImpl(reconnect, anotherDevice,
                        currentStrategyWrapper);
        return algorithm;
    }

    /**
     * Create and provide reconnect manager.
     * @return
     */
    @Provides
    @Singleton
    ReconnectManager provideReconnectManager(){
        ReconnectManager reconnectManager = new ReconnectManager();
        return reconnectManager;
    }

    /**
     * Strategies for reconnect manager.
     */

    /**
     * Create instance of strategy, which will schedule connection attempts fixed number of
     * times after the same delay
     * @return
     */
    @Provides
    @Singleton
    @Named("FinitAttemptCountSameDelay")
    ReconnectSchedulingStrategy provideFinitAttemptCountStrategy() {
        ReconnectSchedulingStrategy strategy = new FinitAttemptCountSameDelay();
        return strategy;
    }

    /**
     * Create strategy, which will be trying to connect indefinetely until device is connected or
     * user decide to stop trying.
     * @return
     */
    @Provides
    @Singleton
    @Named("RetryIndefinetely")
    ReconnectSchedulingStrategy provideRetryIndefinetelyStrategy() {
        ReconnectSchedulingStrategy strategy = new RetryIndefinetely();
        return strategy;
    }
    
    /**
     * Strategies for establishing connection
     */

    @Provides
    @Singleton
    @Named("ReconnectSchedulingStrategy")
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

    /**
     * Create stup, used when app has no connection to device (for robustness)
     * @return
     */
    @Provides
    @Singleton
    @Named("TransferManagerMock")
    TransferManagerBase provideMockTransferManager() {
        TransferManagerMock m = new TransferManagerMock();
        return m;
    }

    /**
     * Create actual implementation of data transfer manager.
     * @return
     */
    @Provides
    @Singleton
    @Named("TransferManagerImplementation")
    TransferManagerBase provideTransferManagerImplementation() {
        TransferManagerImpl m = new TransferManagerImpl();
        return m;
    }
}
