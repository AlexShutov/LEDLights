package alex_shutov.com.ledlights.bluetoothmodule.bluetooth;

import android.util.Log;
import android.util.Pair;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.CommInterface;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnEsbStore;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.hex.BtStoragePort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManagerDataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManagerImpl;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManagerCallback;import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect.ReconnectManager;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect.ReconnectSchedulingStrategy;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect.strategies.FinitAttemptCountSameDelay;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect.strategies.RetryIndefinetely;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.transfer_data.TransferManagerBase;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.transfer_data.TransferManagerFeedback;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Alex on 11/6/2016.
 */
public class BtLogicCellFacade implements CommInterface, ConnectionManagerDataProvider {
    private static final String LOG_TAG = BtLogicCellFacade.class.getSimpleName();
    /**
     * DI component, responsible for creating all objects
     */
    private BtPortAdapterCreator diComponent;

    @Inject
    public EventBus eventBus;
    @Inject
    public BtConnPort connPort;
    @Inject
    public BtScanPort scanPort;
    @Inject
    public BtStoragePort storagePort;

    private BtCommPortListener commFeedback;
    private BtDevice connectedDevice;

    /**
     * Algorithms:
     */
    // connection manager section:
    @Inject
    public ConnectionManagerImpl connectManager;
    // reconnect manager (if connection is lost)
    @Inject
    public ReconnectManager reconnectManager;
    // strategies for scheduling reconnect attempts
    // strategy, trying to connect fixed number of times with the same delay
    @Inject
    @Named("FinitAttemptCountSameDelay")
    public ReconnectSchedulingStrategy reconnectFixedAttemptCountSameDelayStrategy;
    // strategy, trying to connect indefinetely with some fixed delay
    @Inject
    @Named("RetryIndefinetely")
    public ReconnectSchedulingStrategy reconnectIndefinitelyStrategy;

    // connect source of 'connection lost' events to 'reconnect' manager
    private Subscription reconnectManagerConnectionLostSubscription;

    // transfer managers section:
    // dummy implementation, used when there is no active connection (or connecion is lost)
    @Inject
    @Named("TransferManagerMock")
    public TransferManagerBase transferManagerMock;
    //
    @Inject
    @Named("TransferManagerImplementation")
    public TransferManagerBase transferManagerImpl;
    // reference to active transfer manager (mock or the real one).
    private TransferManagerBase currentTransferManager;
    // connect source of 'connection lost' events to data transfer manager
    private Subscription dataTransferConnectionLostSubscription;
    // Transitioning between transfer manager implementations and sending data is done in
    // background. App may crash due to NullPointerException during that transition.
    // Use this lock to avoid that
    private String transferManagerSelectionLock = "Transfer manafer transition lock";

    public BtLogicCellFacade(BtPortAdapterCreator diComponent) {
        this.diComponent = diComponent;
        connectedDevice = null;
    }

    /**
     * ESB event handling
     */

    /**
     * emit event if Bluetooth connection is lost
     */
    private PublishSubject<Boolean> connectionLostEventSource = PublishSubject.create();

    @Subscribe
    public void onEvent(BtConnEsbStore.ArgumentConnectionLostEvent event){
        connectionLostEventSource.onNext(true);
    }

    /**
     * Inherited from ConnectionManagerDataProvider
     */

    @Override
    public BtConnPort provideBtConnPort() {
        return connPort;
    }

    @Override
    public BtScanPort provideBtScanPort() {
        return scanPort;
    }


    @Override
    public EventBus provideEventBus() {
        return eventBus;
    }

    @Override
    public BtDeviceDao provideHistoryDatabase() {
        BtDeviceDao historyDatabase = storagePort.getHistoryDatabase();
        return historyDatabase;
    }

    @Override
    public BtPortAdapterCreator provideDiComponent() {
        return diComponent;
    }

    /**
     * Request all objects after everything is ready (BtLogicCell is initialized)
     */
    public void onInitialized(){
        Log.i(LOG_TAG, "onInitialized()");
        diComponent.injectBtLogicCellFacade(this);
        setupTransferManagers();
        setupConnectAndReconnectManagers();
        eventBus.register(this);
    }

    public void onDestroying(){
        Log.i(LOG_TAG, "onDestroying()");
        // unsubscribe from ESB events
        eventBus.unregister(this);
    }

    /**
     * Inherited from CommInterface
     */

    @Override
    public void startConnection() {
        Log.i(LOG_TAG, "startConnection()");
        connectManager.attemptToEstablishConnection();
    }

    @Override
    public void disconnect() {
        Log.i(LOG_TAG, "disconnect()");
        reconnectManager.stopConnecting();
        if (isDeviceConnected()) {
            Log.i(LOG_TAG, "Device is connected: " + connectedDevice.getDeviceName() +
                ", disconnecting");
        }
    }

    /**
     * Disconnect from current device and tell connection algorithm
     * to select another
     */
    @Override
    public void selectAnotherDevice() {
       reconnectManager.selectDeviceByUi();
    }

    @Override
    public Observable<Boolean> hasDeviceHistory() {
        BtDeviceDao db = storagePort.getHistoryDatabase();
        Observable<Boolean> task =
        Observable.just(db)
                .subscribeOn(Schedulers.io())
                .map(database -> {
                    List<BtDevice> devicesFormHistory = database.getDeviceHistory();
                    return !devicesFormHistory.isEmpty();
                });
        return Observable.defer(() -> task);
    }

    @Override
    public boolean isDeviceConnected() {
        return null != connectedDevice;
    }

    @Override
    public void sendData(byte[] data) {
        Observable<byte[]> sendTask =
                Observable.just(data)
                .subscribeOn(Schedulers.io());
        Observable.defer(() -> sendTask)
                .subscribe(arg -> {
                    // send data and switch strategies synchronously
                    synchronized (transferManagerSelectionLock) {
                        currentTransferManager.sendData(arg);
                    }
            });
    }

    public void setCommFeedback(BtCommPortListener commFeedback) {
        this.commFeedback = commFeedback;
    }


    /**
     * Methods for changing parameters of retry strategies and selecting different
     * reconnection scheduling strategy.
     */

    public void setupIndefineteAttemptsStrategy(int timeInterval, TimeUnit timeUnit) {
        RetryIndefinetely strategy = (RetryIndefinetely) reconnectIndefinitelyStrategy;
        strategy.setReconnectDelay(timeInterval);
        strategy.setReconnectDelayTimeUnit(timeUnit);
    }

    public void setupFixAttemptsFixTimeStrategy(int numberOfAttempts, int timeInterval,
                                         TimeUnit timeUnit) {
        if (numberOfAttempts == 0) {
            Log.w(LOG_TAG, "Number of attempts can not be 0, using 1 instead");
            numberOfAttempts = 1;
        }
        FinitAttemptCountSameDelay strategy =
                (FinitAttemptCountSameDelay) reconnectFixedAttemptCountSameDelayStrategy;
        strategy.setAttemptLimit(numberOfAttempts);
        strategy.setReconnectDelay(timeInterval);
        strategy.setReconnectDelayTimeUnit(timeUnit);
    }

    public void selectRetryIndefinitelyStrategy() {
        reconnectManager.setReconnectStrategy(reconnectIndefinitelyStrategy);
    }

    public void selectFixNUmberOfAttemptsFixedTimeStrategy() {
        reconnectManager.setReconnectStrategy(reconnectFixedAttemptCountSameDelayStrategy);
    }

    /**
     * Initialization methods
     */

    /**
     * Callback for ReconnectManager, binding that manager to external entities
     */
    private final ConnectionManagerCallback reconnectCallback = new ConnectionManagerCallback() {
        @Override
        public void onConnectionEstablished(BtDevice connectedDevice) {
            String msg = "Connection established with device ";
            Log.i(LOG_TAG, connectedDevice == null ? msg : msg +
                    connectedDevice.getDeviceName());
            BtLogicCellFacade.this.connectedDevice = connectedDevice;
            // select real data transfer manager first
            handleNewConnectionByTransferManager(connectedDevice);
            // and then inform app of successful connection
            commFeedback.onConnectionStarted(connectedDevice);
        }

        @Override
        public void onAttemptFailed() {
            Log.i(LOG_TAG, "onAttemptFailed" );
            commFeedback.onConnectionFailed();
        }

        @Override
        public void onUnsupportedOperation() {
            commFeedback.onConnectionFailed();
        }
    };

    /**
     * By default application try to connect to device infinite number of times. It is
     * acceptable, because Bluetooth require not much power and it will not affect battery life
     * even if user forget to close app.
     * Application idea presume, that user will turn this app off when he or she will tire of
     * riding.
     */
    private void setupConnectAndReconnectManagers() {
        reconnectManager.setDecoreeManager(connectManager);
        // select strategy, always trying to connect by default.
        selectRetryIndefinitelyStrategy();
        // setup callback for reconnection
        reconnectManager.setReconnectCallback(device -> {
            String msg = "Device reconnected: ";
            Log.i(LOG_TAG, device == null ? msg : msg +
                    device.getDeviceName());
            // save device info
            BtLogicCellFacade.this.connectedDevice = device;
            // select data transfer algorithm
            handleNewConnectionByTransferManager(device);
            // Inform app of reconnection event.
            commFeedback.onReconnected(device);
        });
        // set callback, doing actual work. It is not implemented in this class, because
        // it will get messy
        reconnectManager.setCallback(reconnectCallback);
        //  initialize it.
        connectManager.init(this);
        if (reconnectManagerConnectionLostSubscription != null &&
                !reconnectManagerConnectionLostSubscription.isUnsubscribed()) {
            reconnectManagerConnectionLostSubscription.unsubscribe();
            reconnectManagerConnectionLostSubscription = null;
        }

        // createPipeline receiving ESB events
        connectionLostEventSource.asObservable()
                .observeOn(Schedulers.computation())
                .subscribe(t -> {
                    Log.i(LOG_TAG, "Connection lost");
                    reconnectManager.onConnectionLost();
                });
    }

    /**
     * Setup both mock and real transfer managers
     */

    /**
     * Feedback interface, binding TransferManager to the rest of app.
     */
    private TransferManagerFeedback transferManagerFeedback = new TransferManagerFeedback() {
        @Override
        public void onDataSent() {
            Log.i(LOG_TAG, "Data sent");
            // inform feedback interface of successful data sending
            // do it in background
            Observable.defer(() -> Observable.just(true))
                    .subscribeOn(Schedulers.io())
                    .subscribe(t -> {
                        commFeedback.onDataSent();
                    });
        }

        @Override
        public void onDataSendFailed() {
            Log.i(LOG_TAG, "Data sending failed");
        }

        @Override
        public void receiveData(byte[] data, int size) {
            Log.i(LOG_TAG, "Data received: " + data + " size: " + size);
            Pair<byte[], Integer> msgRead = new Pair<>(data, size);
            // Inform feedback interface of received message in background
            Observable.defer(() -> Observable.just(msgRead))
                    .subscribeOn(Schedulers.computation())
                    .subscribe(pair -> {
                        commFeedback.receiveData(pair.first, pair.second);
                    });
        }
    };

    /**
     * Set the same feedback interface to both mock and real TransferManager and pick
     * mock implementation by default, because device is not connected during initialization
     * and using real implementation will cause an exception.
     */
    private void setupTransferManagers() {
        transferManagerMock.setFeedback(transferManagerFeedback);
        transferManagerImpl.setFeedback(transferManagerFeedback);
        // use mock transfer manager during initialization
        selectMockTransferManager();
        // rid of previous connection to 'connection lost' event source
        if (dataTransferConnectionLostSubscription != null &&
                !dataTransferConnectionLostSubscription.isUnsubscribed()) {
            dataTransferConnectionLostSubscription.unsubscribe();
            dataTransferConnectionLostSubscription = null;
        }
        dataTransferConnectionLostSubscription =
                connectionLostEventSource.asObservable()
                .observeOn(Schedulers.computation())
                .subscribe(t -> {
                    handleConnectionLossByTransferManager();
                });
    }

    /**
     * Select mock transfer manager. It is necessary when device isn't connected yet.
     * Check if mock implementation is active and select it if it not. Selection presume
     * passing all needed reference by DataProvider interface (in 'init()' method).
     */
    private void selectMockTransferManager() {
        if (currentTransferManager != null && currentTransferManager == transferManagerMock) {
            return;
        }
        // check if it is a first start
        if (null != currentTransferManager) {
            currentTransferManager.suspend();
        }
        currentTransferManager = transferManagerMock;
        transferManagerMock.init(this);
        // inform external listener that mock transfer manager is selected
        if (null != commFeedback) {
            commFeedback.onDummyDeviceSelected();
        }
    }

    /**
     * Select real implementation of TransferManager. Call this method after device is connected.
     * Method has the same logic, as .selecMockTransferManager() method.
     */
    private void selectRealTransferManafer() {
        if (currentTransferManager != null && currentTransferManager == transferManagerImpl) {
            return;
        }
        // ignore on first start
        if (null != currentTransferManager) {
            currentTransferManager.suspend();
        }
        currentTransferManager = transferManagerImpl;
        transferManagerImpl.init(this);
    }

    /**
     * Call this method from callback, informing on connection loss
     */
    private void handleConnectionLossByTransferManager() {
        Log.i(LOG_TAG, "Processing loss of connection by transfer manager");
        synchronized (transferManagerSelectionLock) {
            selectMockTransferManager();
        }
    }

    /**
     * Call this when BT device is connected
     * @param device
     */
    private void handleNewConnectionByTransferManager(BtDevice device) {
        Log.i(LOG_TAG, "Picking real data transfer manager (not mock implementation).");
        synchronized (transferManagerSelectionLock) {
            selectRealTransferManafer();
        }
    }

}
