package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies;

import android.util.Log;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionCallback;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionDataProvider;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by Alex on 11/5/2016.
 */
public class ReconnectStrategy extends EstablishConnectionStrategy {
    private static final String LOG_TAG = ReconnectStrategy.class.getSimpleName();

    /**
     * This is info about last connected device
     */
    private class LastDeviceData {
        BtDevice deviceInfo;
        Long lastConnectionStartTime;
        Long lastConnectionEndTime;
    }
    // entities
    private BtConnPort connPort;
    // FRP - logic

    private Observable<LastDeviceData> getLastDeviceFromDbtask =
            Observable.just("")
            .subscribeOn(Schedulers.io())
            .map(t -> {
                BtDevice lastDevice = historyDb.getLastConnectedMotorcycleInfo();
                if (null == lastDevice){
                    // will redirect to 'onError()' method, we're done here
                    throw new IllegalStateException("There is no info of last connected device");
                }
                Long lastConnStartTime = historyDb.getLastConnectionStartTime();
                lastConnStartTime = (null == lastConnStartTime) ? 0 : lastConnStartTime;
                Long lastConnEndTime = historyDb.getLastConnectionEndTime();
                lastConnEndTime = (null == lastConnEndTime) ? 0 : lastConnEndTime;
                LastDeviceData info = new LastDeviceData();
                info.deviceInfo = lastDevice;
                info.lastConnectionStartTime = lastConnStartTime;
                info.lastConnectionEndTime = lastConnEndTime;
                return info;
            })
            .take(1);

    private Subscription pendingLastDeviceTask;
    private Subscription pendingConectTask;


    public ReconnectStrategy(){
        // set empty callback by default from base class
        super();
        pendingLastDeviceTask = null;
    }

    /**
     * Get everything this algorithm need from DataProvider.
     * Here I suppose that all entities are the same during all lifetime of that logic cell -
     * those are singletons and we don't need to provide data every time strategy is triggered
     */
    @Override
    protected void getDependenciesFromFacade(DataProvider dataProvider) {
        // it is a MUST
        super.getDependenciesFromFacade(dataProvider);
        EstablishConnectionDataProvider provider = (EstablishConnectionDataProvider) dataProvider;
        connPort = provider.provideBtConnPort();
    }

    /**
     * Manage EventBus subscriptions here so this strategy can receive needed events (via ESB)
     */

    /**
     * Unsubscribe this strategy from EventBus' events
     */
    @Override
    public void suspend() {
        super.suspend();
    }

    /**
     * Register this strategy with EventBus
     */
    @Override
    protected void start() {
        super.start();
    }


    @Override
    public void attemptToEstablishConnection() {
        Log.i(LOG_TAG, "attemptToEstablishConnection()");
        stopTask();
        pendingLastDeviceTask = Observable.defer(() -> getLastDeviceFromDbtask)
                .observeOn(Schedulers.computation())

                .subscribe(lastDeviceInfo -> {
                    Log.i(LOG_TAG, "Last connected device: " +
                            lastDeviceInfo.deviceInfo.getDeviceName());
                    BtDevice device = lastDeviceInfo.deviceInfo;
                    createPendingConnectTask(device);
                    connectToDevice(device);
                }, error -> {
                    Log.w(LOG_TAG, "There is no info about last connected device");
                    notifyAboutFailure();
                    // bring down all ungoing tasks
                    stopTask();
                });
    }

    /**
     * Strategy is attempting to connect only when it has active subscriptioin
     * to 'connect' task
     * @return
     */
    @Override
    public boolean isAttemptingToConnect() {
        return pendingConectTask != null && !pendingConectTask.isUnsubscribed();
    }

    @Override
    public void stopConnecting() {
        stopTask();
    }

    /**
     * This method is called by strategy when it know that database has info about
     * last connected device and at point in time when strategy read that info in background
     * @param device
     */
    private void createPendingConnectTask(BtDevice device){
        Observable<BtDevice> trigger = formConnDevicePipe(device);
        // release previous request is there is any
        cancelConnectionPendingRequest();
        pendingConectTask = trigger
                .observeOn(Schedulers.computation())
                .subscribe(connectedDevice -> {
                    Log.i(LOG_TAG, "Connected to: " + connectedDevice.getDeviceName());
                    // save device we just connected to as last connected device into history db.
                    updateLastConnectedDeviceRecord(connectedDevice);
                    // tell callback that connection is established
                    EstablishConnectionCallback callback = getCallback();
                    if (null != callback){
                        callback.onConnectionEstablished(connectedDevice);
                    }
                }, error -> {
                    Log.w(LOG_TAG, "Can't connect to device");
                    // attempt failed, don't touch history database.
                    EstablishConnectionCallback callback = getCallback();
                    if (null != callback){
                        callback.onAttemptFailed();
                    }
                });
    }

    private void connectToDevice(BtDevice device){
        connPort.connect(device);
    }

    /**
     * Unsubscribe from that task if it is active
     */
    private void stopTask(){
        /**
         * Stop receiving info about last connected device
         */
        if (null != pendingConectTask && !pendingConectTask.isUnsubscribed()){
            pendingConectTask.unsubscribe();
            pendingConectTask = null;
        }
        if (null != pendingLastDeviceTask && !pendingLastDeviceTask.isUnsubscribed()){
            pendingLastDeviceTask.unsubscribe();
            pendingLastDeviceTask = null;
        }
        cancelConnectionPendingRequest();
        connPort.stopConnecting();
        cancellFailureNotification();
    }

    /**
     * Ubsubscribe from pending 'connect' task result and stop any
     * ongoing connection.
     */
    private void cancelConnectionPendingRequest(){
        if (null != pendingConectTask && !pendingConectTask.isUnsubscribed()){
            pendingConectTask.unsubscribe();
            pendingConectTask = null;
        }
    }
}
