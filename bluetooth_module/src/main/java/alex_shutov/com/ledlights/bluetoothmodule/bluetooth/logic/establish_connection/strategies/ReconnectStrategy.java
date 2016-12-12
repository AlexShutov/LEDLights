package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies;

import android.util.Log;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
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

    private Observable<LastDeviceData> getLastDeviceFromDbTask =
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
        pendingLastDeviceTask = Observable.defer(() -> getLastDeviceFromDbTask)
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
     * Methods, defining actual work need to be done when base after functionality from
     * base class established connection or failed either.
     */

    /**
     * Do nothing, we already informed callback.
     * @param device
     */
    @Override
    protected void doOnConnectionSuccessful(BtDevice device) {
        Log.i(LOG_TAG, "Connection established, performing final action in 'Reconnect' strategy");
    }

    /**
     * This is 'single shot' strategy - report of error if attempt failed. Another strategies may have
     * different behaviour
     */
    @Override
    protected void doOnConnectionAttemptFailed() {
        Log.i(LOG_TAG, "Connection attempt failed, perfforming action from 'reconnect' strategy");
        EstablishConnectionCallback callback = getCallback();
        if (null != callback) {
            callback.onAttemptFailed();
        }
    }


    @Override
    public void stopConnecting() {
        stopTask();
    }

    @Override
    public void selectDeviceByUi() {}

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

        if (null != pendingLastDeviceTask && !pendingLastDeviceTask.isUnsubscribed()){
            pendingLastDeviceTask.unsubscribe();
            pendingLastDeviceTask = null;
        }
        cancelConnectionPendingRequest();
        connPort.stopConnecting();
        cancellFailureNotification();
    }
}
