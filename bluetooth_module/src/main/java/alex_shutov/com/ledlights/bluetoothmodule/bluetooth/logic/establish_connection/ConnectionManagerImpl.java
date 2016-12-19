package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.BtAlgorithm;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionCallbackReactive.CallbackSubscriptionManager;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.EstablishConnectionStrategy;

/**
 * Created by Alex on 10/26/2016.
 */

/**
 *  Algorithm, responsible for establishing connection with Bluetooth device when
 *  external port demands it, or when connection was lost by some reason (perhaps, lost of power
 *  on the other end).
 */
public class ConnectionManagerImpl extends BtAlgorithm implements
        ConnectionManager {
    private static final String LOG_TAG = ConnectionManagerImpl.class.getSimpleName();
    private ConnectionManagerDataProvider dataProvider;
    /**
     * Used to notify caller about results of algorithms (success or failure)
     */
    private ConnectionManagerCallback callback;

    /**
     * Currently connected device - result of this algorithm
     */
    private BtDevice connectedDevice;
    /**
     * Access object for database, storing device connection history.
     */
    private BtDeviceDao deviceDatabase;
    private BtConnPort connPort;
    private BtScanPort scanPort;
    EventBus eventBus;

    /**
     * Strategies for establishing connection
     */
    private EstablishConnectionStrategy reconnectStrategy;
    private EstablishConnectionStrategy anotherDeviceStrategy;

    private EstablishConnectionStrategy currentStrategy;

    private EstablishConnectionCallbackReactive currentStrategyCallbackWrapper;
    private CallbackSubscriptionManager currentStrategySubscriptions;

    public ConnectionManagerImpl(EstablishConnectionStrategy reconnectStrategy,
                                 EstablishConnectionStrategy anotherDeviceStrategy,
                                 EstablishConnectionCallbackReactive callbackWrapper) {
        this.reconnectStrategy = reconnectStrategy;
        this.anotherDeviceStrategy = anotherDeviceStrategy;
        currentStrategyCallbackWrapper = callbackWrapper;
        currentStrategySubscriptions = new CallbackSubscriptionManager();
    }

    @Override
    public void suspend() {
        reconnectStrategy.setCallback(null);
        reconnectStrategy.suspend();
        anotherDeviceStrategy.setCallback(null);
        anotherDeviceStrategy.suspend();
    }

    /**
     * Use DataProvider we saved to initialize strategies and wire up
     * strategies
     */
    @Override
    protected void start() {
        connectStrategyCallbackToExternalCallback();
        chooseStrategy(reconnectStrategy);
    }

    @Override
    protected void getDependenciesFromFacade(DataProvider dp) {
        dataProvider = (ConnectionManagerDataProvider) dp;
        // get connection history database
        deviceDatabase = dataProvider.provideHistoryDatabase();
        eventBus = dataProvider.provideEventBus();
        connPort = dataProvider.provideBtConnPort();
        scanPort = dataProvider.provideBtScanPort();
    }

    /**
     * Inherited from ConnectionManager
     */

    @Override
    public boolean isAttemptingToConnect() {
        return currentStrategy.isAttemptingToConnect();
    }

    @Override
    public void stopConnecting() {
        currentStrategy.stopConnecting();
    }

    /**
     * Connect algorithm has two strategies - for selecting another device by UI and
     * reconnecting to the last device. When app want to connect seamlessly, it is
     * using 'reconnect' strategy and (in this case) 'anotherDeviceStrategy' for
     * picking device by UI
     */
    @Override
    public void selectDeviceByUi() {
        chooseStrategy(anotherDeviceStrategy);
        currentStrategy.selectDeviceByUi();
    }

    /**
     *  Connect to the last known device
     */
    @Override
    public void attemptToEstablishConnection() {
        chooseStrategy(reconnectStrategy);
        currentStrategy.attemptToEstablishConnection();
    }

    public void setCallback(ConnectionManagerCallback reconnectCallback) {
        this.callback = reconnectCallback;
    }

    public BtDevice getConnectedDevice() {
        return connectedDevice;
    }


    /**
     * Only one strategy can be active at a time, so we have to choose it.
     * Active strategy have to pass messages to this algorithm, so it should
     * be connected to our callback, which tunnel those messages into rx pipeline inn this
     * algorithm.
     * @param strategy
     */
    private void chooseStrategy(EstablishConnectionStrategy strategy) {
        // pic strategy if it not active yet
        if (currentStrategy == strategy){
            return;
        } else if (null != currentStrategy) {
            // suspend the current one
            currentStrategy.suspend();
        }
        strategy.init(dataProvider);
        strategy.setCallback(currentStrategyCallbackWrapper);
        currentStrategy = strategy;
    }

    /**
     * When some strategy cannot perform well, we need to try another one.
     * Previous strategy need to be disconnected from callback and all activitis inside
     * of it has top be suspended.
     * @param strategy
     */
    private void releaseStrategy(EstablishConnectionStrategy strategy) {
        strategy.setCallback(null);
        strategy.suspend();
        currentStrategy = null;
    }

    private void connectStrategyCallbackToExternalCallback(){
        currentStrategySubscriptions.unsubscribe();
        currentStrategySubscriptions.successSubscription =
                currentStrategyCallbackWrapper.getConnectedSource()
                        .subscribe(connectedDevice -> {
                            Log.i(LOG_TAG, "device reconnected(): " + connectedDevice.getDeviceName());
                            ConnectionManagerImpl.this.connectedDevice = connectedDevice;
                            if (null != callback){
                                callback.onConnectionEstablished(connectedDevice);
                            }
                        });
        currentStrategySubscriptions.failureSubscription =
                currentStrategyCallbackWrapper.getFailureSource()
                        .subscribe(t -> {
                            Log.w(LOG_TAG, "Connection attempt have failed");
                            if (null != callback){
                                callback.onAttemptFailed();
                            }
                        });
        currentStrategySubscriptions.unsupportedOperationSubscription =
                currentStrategyCallbackWrapper.getUnsupportedOperationSource()
                        .subscribe(t -> {
                            Log.i(LOG_TAG, "This is an unsupported operation");
                            callback.onUnsupportedOperation();
                        });
    }


}
