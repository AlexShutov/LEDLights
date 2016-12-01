package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionCallback;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionDataProvider;
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
        implements BtUiDeviceSelectionViewPartial, AnotherDeviceModel ,
        BtScanPortListener {
    private static final String LOG_TAG = SelectAnotherDeviceStrategy.class.getSimpleName();

    private BtScanPort scanPort;
    private BtConnPort connPort;
    @Inject
    AnotherDevicePresenter presenter;

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
        // send reference to presenter in sticjy event so View can get it at any time
        PresenterInstanceEvent event = new PresenterInstanceEvent();
        event.setPresenter(presenter);
        eventBus.removeStickyEvent(PresenterInstanceEvent.class);
        eventBus.postSticky(event);
    }

    @Override
    protected void getDependenciesFromFacade(DataProvider dataProvider) {
        super.getDependenciesFromFacade(dataProvider);
        EstablishConnectionDataProvider partsProvider = (EstablishConnectionDataProvider) dataProvider;
        scanPort = partsProvider.provideBtScanPort();
        connPort = partsProvider.provideBtConnPort();
        partsProvider.provideDiComponent().injectSelectAnotherDeviceStrategy(this);
    }

    /**
     * Callback, called whenever app is connected to another Bluetooth device. Connection
     * to device has to be initiated by createPendingConnectTask(BtDevice device) method in
     * base class (EstablishConnectionStrategy)
     * @param device
     */
    @Override
    protected void doOnConnectionSuccessful(BtDevice device) {
        Log.i(LOG_TAG, "Device successfully connected: " + device.getDeviceName());
    }

    @Override
    protected void doOnConnectionAttemptFailed() {
        Log.w(LOG_TAG, "Connection attempt failed");

    }

    /**
     * User tell this strategy to start working on establishing connection by this method.
     *
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
     * Inherited from BtUiDeviceSelectionViewPartial
     */


    /**
     * User picked a device, but app isn't connected to it yet.
     * Attempt to create connection to that device by using methods from base class.
     * @param device
     */
    @Override
    public void onUserChooseDevice(BtDevice device) {
        Log.i(LOG_TAG, "onUserChooseDevice()" + ( device == null ? "" : device.getDeviceName()));
    }

    /**
     * User refused to select device. suspend all activity, close Ui and notify callback about it
     */
    @Override
    public void onCancelledByUser() {
        Log.i(LOG_TAG, "onCancelledByUser()");
        cancelOngoingConnectionAttemptsAndDiscovery();
        EstablishConnectionCallback callback = getCallback();
        if (null != callback) {
            callback.onAttemptFailed();
        }
    }

    /**
     * Ui request to stop all activities with bluetooth. This method return Observable so Ui
     * will know when it can proceed
     * @return
     */
    @Override
    public Observable<Boolean> stopBluetoothCommunication() {
        return Observable.just("")
                .subscribeOn(Schedulers.io())
                .map(t -> {
                    cancelOngoingConnectionAttemptsAndDiscovery();
                    return true;
                });
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

    private PublishSubject<List<BtDevice>> pairedDevicesResultPipe = PublishSubject.create();

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
        return pairedDevicesResultPipe.asObservable()
                .take(1);
    }


    @Override
    public Observable<BtDevice> discoverDevices() {
        BtScanAdapter scanAdapter = (BtScanAdapter) scanPort;
        scanAdapter.setPortListener(this);
        scanPort.startDiscovery();
        return null;
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
                    pairedDevicesResultPipe.onNext(listOfDevices);
                }, e -> {});
    }

    @Override
    public void onDeviceFound(BtDevice device) {
        Log.i(LOG_TAG, "Bluetooth device found: " + device.getDeviceName());
    }

    @Override
    public void onScanCompleted() {
        Log.i(LOG_TAG, "Bluetooth discovery complete");
    }

    /**
     * Private logic methods
     */


    private void cancelOngoingConnectionAttemptsAndDiscovery() {
        Log.i(LOG_TAG, "Cancelling all Bluetooth activities before attempting to select another " +
                "device");
        scanPort.stopDiscovery();
        connPort.stopConnecting();
        connPort.close();
    }




    private void triggerUi() {
        presenter.showUiForSelectingAnotherBluetoothDevice();
    }
}



