package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.BluetoothChatService;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManagerDataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.EstablishConnectionStrategy;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.events.PresenterInstanceEvent;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp.AnotherDeviceModel;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp.AnotherDevicePresenter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Alex on 11/7/2016.
 */
public class SelectAnotherDeviceStrategy extends EstablishConnectionStrategy
        implements AnotherDeviceModel,
        BtScanPortListener {
    private static final String LOG_TAG = SelectAnotherDeviceStrategy.class.getSimpleName();

    private BtScanPort scanPort;
    private BtConnPort connPort;
    @Inject
    AnotherDevicePresenter presenter;

    private BtDevice selectedDevice;

    public SelectAnotherDeviceStrategy(){
        super();
    }

    @Override
    public void suspend() {
        // unregister this strategy from presenter
        presenter.detachView();
        // remove event with instance of presenter from a bus
        eventBus.removeStickyEvent(PresenterInstanceEvent.class);
        super.suspend();
    }

    @Override
    protected void start() {
        super.start();
        // register this strategy as a view in Presenter
        presenter.attachModel(this);
        // send reference to presenter in sticky event so View can get it at any time
        PresenterInstanceEvent event = new PresenterInstanceEvent();
        event.setPresenter(presenter);
        eventBus.removeStickyEvent(PresenterInstanceEvent.class);
        eventBus.postSticky(event);
    }

    @Override
    protected void getDependenciesFromFacade(DataProvider dataProvider) {
        super.getDependenciesFromFacade(dataProvider);
        ConnectionManagerDataProvider partsProvider = (ConnectionManagerDataProvider) dataProvider;
        scanPort = partsProvider.provideBtScanPort();
        connPort = partsProvider.provideBtConnPort();
        partsProvider.provideDiComponent().injectSelectAnotherDeviceStrategy(this);
    }

    /**
     * Callback, called whenever app is connected to another Bluetooth device. Connection
     * to device has to be initiated by createPendingConnectTask(BtDevice device) method in
     * base class (EstablishConnectionStrategy).
     * Notify external callback of success
     * @param device
     */
    @Override
    protected void doOnConnectionSuccessful(BtDevice device) {
        String message = "Device successfully connected: " + device.getDeviceName();
        Log.i(LOG_TAG, message);
        // close Ui
        presenter.hideUi();
    }

    /**
     * If app can't connect to device (perhaps, it doesn't support UUID, used in application, or,
     * it is a unknown phone), show user dialog, allowing to agree with failure, or to try
     * connecting again
     */
    @Override
    protected void doOnConnectionAttemptFailed() {
        Log.w(LOG_TAG, "Connection attempt failed");
        presenter.suggestConnectingAgainAfterAttemptFailed(selectedDevice);
    }

    /**
     * User tell this strategy to createPipeline working on establishing connection by this method.
     */
    @Override
    public void attemptToEstablishConnection() {
        triggerUi();
    }

    /**
     * Strategy is being told to stop connection attempt
     */
    @Override
    public void stopConnecting() {
        cancelOngoingConnectionAttemptsAndDiscovery();
    }

    /**
     * Start selectinf device by UI. In case of this strategy it is the same, as
     * attemptToEstablishConnection() method.
     */
    @Override
    public void selectDeviceByUi() {
        triggerUi();
    }


    /**
     * Inherited from PortListener (ui port listener)
     */

    @Override
    public void onPortReady(int portID) {
        Log.i(LOG_TAG, "onPortReady(" + portID + ")");

    }

    @Override
    public void onCriticalFailure(int portID, Exception e) {
        Log.i(LOG_TAG, "onCriticalFailure( portID: " + portID + ", error: " + e.getMessage());
    }

    /**
     * Inherited from AnotherDeviceModel
     */

    /** Vortex, receiving list of paired devices, which reference can be returned as result of
     * operation
     */
    private PublishSubject<List<BtDevice>> pairedDevicesVortex = PublishSubject.create();

    /** Vortex for discovered devices. Notice, it will get corrupted once discovery completes
     * (.onCompleted() method, so we need to create new every time.
     */
    private PublishSubject<BtDevice> discoveredDeviceVortex;

    @Override
    public Observable<List<BtDevice>> getDevicesFromConnectionHistory() {

        Observable<List<BtDevice>> historyDevicesTask = Observable.just("")
                .observeOn(Schedulers.io())
                .map(t -> historyDb.getDeviceHistory());
        return Observable.defer(() -> historyDevicesTask);
    }

    @Override
    public Observable<List<BtDevice>> getPairedSystemDevices() {
        BtScanAdapter scanAdapter = (BtScanAdapter) scanPort;
        scanAdapter.setPortListener(this);
        scanPort.getPairedDevices();
        return pairedDevicesVortex.asObservable()
                .take(1);
    }


    @Override
    public Observable<BtDevice> discoverDevices() {
        Observable<BtDevice> task = Observable.just("")
                .subscribeOn(Schedulers.computation())
                .flatMap(t -> {
                    BtScanAdapter scanAdapter = (BtScanAdapter) scanPort;
                    scanAdapter.setPortListener(this);
                    discoveredDeviceVortex = PublishSubject.create();
                    scanPort.startDiscovery();
                    // create new vortex
                    return discoveredDeviceVortex.asObservable();
                });
        return Observable.defer(() -> task);
    }

    @Override
    public void stopDiscovery() {
        scanPort.stopDiscovery();
    }

    @Override
    public void connectToDevice(BtDevice device) {
        Observable.just(device)
                .subscribeOn(Schedulers.computation())
                .subscribe(d -> {
                    selectedDevice = device;
                    createPendingConnectTask(d);
                    // UI doesn't know UUID of new device, so we have to provide it.
                    d.setDeviceUuIdSecure(getUUID(d));
                    d.setDeviceUuIdInsecure(getUUID(d));
                    connPort.connect(d);
                });
    }

    /**
     * Close UI and infom outer listener of failure
     */
    @Override
    public void onFailedToSelectAnotherDevice() {
        // close Ui
        presenter.hideUi();
        // notify callback of a failure
        getCallback().onAttemptFailed();
    }

    /**
     * Inherited from BtScanPortListener
     */

    @Override
    public void onPairedDevicesReceived(Set<BtDevice> devices) {
        Observable<List<BtDevice>> passDevicesTask =
                Observable.just(devices)
                        .observeOn(Schedulers.computation())
                .map(deviceSet -> new ArrayList<BtDevice>(devices));
        Observable.defer(() -> passDevicesTask)
                .subscribe(listOfDevices -> {
                    pairedDevicesVortex.onNext(listOfDevices);
                }, e -> {});
    }

    /**
     * BtScanPort will call this method when Bluetooth device is discovered
     * @param device
     */
    @Override
    public void onDeviceFound(BtDevice device) {
        Log.i(LOG_TAG, "Bluetooth device found: " + device.getDeviceName());
        discoveredDeviceVortex.onNext(device);
    }

    /**
     * Is called by BtScanPort when discovery is over.
     */
    @Override
    public void onScanCompleted() {
        Log.i(LOG_TAG, "Bluetooth discovery complete");
        discoveredDeviceVortex.onCompleted();
    }

    /**
     * Private logic methods
     */

    private void cancelOngoingConnectionAttemptsAndDiscovery() {
        Log.i(LOG_TAG, "Cancelling all Bluetooth activities before attempting to select another " +
                "device");
        // cancel ongoing connection attempts
        if (isAttemptingToConnect()) {
            cancelConnectionPendingRequest();
        }
        scanPort.stopDiscovery();
        connPort.stopConnecting();
        connPort.close();
    }

    /**
     *  Each strategy need UUID for connecting to device. UI cannot provide UUID.
     *  Application is supposed to be used with HC-05 modules, having known UUID;
     * @param device
     * @return
     */
    protected String getUUID(BtDevice device) {
        Log.i(LOG_TAG, "Using UUID for HC-05 Bluetooth module");
        String uuid = BluetoothChatService.HC_05_UUID.toString();
        return uuid;
    }

    private void triggerUi() {
        BtDisconnectDelayer delayLatch = new BtDisconnectDelayer(eventBus, connPort);
        delayLatch.getEventSource()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                    presenter.showUiForSelectingAnotherBluetoothDevice();
                });
        delayLatch.start();
    }

}



