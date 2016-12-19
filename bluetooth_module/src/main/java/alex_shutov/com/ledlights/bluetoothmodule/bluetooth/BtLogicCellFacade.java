package alex_shutov.com.ledlights.bluetoothmodule.bluetooth;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.CommInterface;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnEsbStore;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.hex.BtStoragePort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManagerDataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionManager;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManagerCallback;
import rx.Observable;
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
    @Inject
    public EstablishConnectionManager connectManager;

    public BtLogicCellFacade(BtPortAdapterCreator diComponent){
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

        connectManager.setCallback(new ConnectionManagerCallback() {
            @Override
            public void onConnectionEstablished(BtDevice connectedDevice) {
                String msg = "Connection established with device ";
                Log.i(LOG_TAG, connectedDevice == null ? msg : msg +
                        connectedDevice.getDeviceName());
                BtLogicCellFacade.this.connectedDevice = connectedDevice;
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
        });
        connectManager.init(this);
        // createPipeline receiving ESB events
        connectionLostEventSource.asObservable()
                .observeOn(Schedulers.computation())
                .subscribe(t -> {
                    Log.i(LOG_TAG, "Connection lost");
                });
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
        if (isDeviceConnected()){
            Log.i(LOG_TAG, "Device is connected: " + connectedDevice.getDeviceName() +
                ", disconnecting");
            connPort.close();
        }
    }

    /**
     * Disconnect from current device and tell connection algorithm
     * to select another
     */
    @Override
    public void selectAnotherDevice() {
       connectManager.selectDeviceByUi();
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

    }


    public void setCommFeedback(BtCommPortListener commFeedback) {
        this.commFeedback = commFeedback;
    }

    private void initializeConnectManager() {

    }


}
