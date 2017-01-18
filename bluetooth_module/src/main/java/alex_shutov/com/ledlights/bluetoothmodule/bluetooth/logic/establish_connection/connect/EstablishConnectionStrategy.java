package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.BtAlgorithm;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManager;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManagerCallback;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnEsbStore.*;
import static alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnEsbStore.PortState.*;
import static alex_shutov.com.ledlights.hex_general.common.utils.impl.LogUtils.LOGE;
import static alex_shutov.com.ledlights.hex_general.common.utils.impl.LogUtils.LOGI;
import static alex_shutov.com.ledlights.hex_general.common.utils.impl.LogUtils.LOGW;

/**
 * Created by Alex on 11/5/2016.
 */
public abstract class EstablishConnectionStrategy extends BtAlgorithm
        implements ConnectionManager {
    private static final String LOG_TAG = EstablishConnectionStrategy.class.getSimpleName();
    private static ConnectionManagerCallback stubCallback = new ConnectionManagerCallback() {
        @Override
        public void onConnectionEstablished(BtDevice conenctedDevice) {}
        @Override
        public void onAttemptFailed() {}
        @Override
        public void onUnsupportedOperation() {}
    };
    private ConnectionManagerCallback callback;
    /**
     * We need to listen for ESB's events for successful connection or connection failure
     */
    protected EventBus eventBus;
    protected BtDeviceDao historyDb;

    protected Observable<Boolean> sendAttemptFailedEventTask =
            Observable.just("")
                    .subscribeOn(Schedulers.computation())
                    .map(t -> {
                        if (null == callback) {
                            return false;
                        }
                        callback.onAttemptFailed();
                        return true;
                    })
                    .take(1);
    protected Subscription notifyAboutFailure;
    public EstablishConnectionStrategy(){
        setCallback(stubCallback);
        pendingConnectTask = null;
    }

    /**
     *  The next section is responsible for getting instance of EventBus and registering this
     *  object in it so we can track completion and failure events emitted into ESB.
     *  This logic has to be here in base class, because all strategies handle results in the
     *  same way.
     */

    /**
     * We meed a FRP- way to be notified whenever connection attempt is successful or if it
     * have failed
     */
    private PublishSubject<Boolean> connResultPipe = PublishSubject.create();

    private Subscription pendingConnectTask;
    /**
     * Concrete strategies need convenient FRP way to be notified about completion of
     * connection request- whether it was successful and device is connected or if it failed
     * and strategy has to report of it (if it is active).
     * Notice, it is assumed that only one strategy is active at a time, so we need only one
     * result value for every connection attempt (so .take(1) operator come in play).
     * @return Source, emitting true in case of success and false in case of a failure
     */
    protected Observable<Boolean> getConnResultPipe(){
        Observable<Boolean> pipe = connResultPipe.asObservable()
                .subscribeOn(Schedulers.computation());
        return pipe;
    }

    /**
     * At some point strategy knows what device it want to connect to. But, ESb provide only
     * event, indicating, if connection attempt was successful. It won't give you actual
     * device infos. So, we have to combine result pipe with device data in order to in the
     * end know what device we connected to.
     * Notice, I use Observable.defer(), because device will differ every time.
     * @param device Device we want to connect to
     * @return Source, returning device info in case of success or error when connection attempt
     *          fails.
     */
    protected Observable<BtDevice> formConnDevicePipe(BtDevice device){
        Observable<BtDevice> deviceSource = Observable.defer(() -> Observable.just(device));
        Observable<Boolean> resultSource = getConnResultPipe();
        Observable<BtDevice> pipe =
        Observable.zip(deviceSource, resultSource, (btDevice, result) -> {
            if (result){
                return device;
            } else {
                throw new IllegalStateException("conection attempt failed");
            }
        })
        .take(1);
        return pipe;
    }
    /**
     * Inherited from ConnectionManager interface - actual interface implementation
     */

    /**
     * Get instance of EventBus
     * @param dataProvider
     */
     protected void getDependenciesFromFacade(DataProvider dataProvider){
         eventBus = dataProvider.provideEventBus();
         historyDb = dataProvider.provideHistoryDatabase();
     }

    /**
     * When Bluetooth device is connected, we should update record in database for last
     * connected device as well as createPipeline time of connection
     * @param device
     */
    protected void updateLastConnectedDeviceRecord(BtDevice device){
        Observable.defer(() -> Observable.just(device))
                .subscribeOn(Schedulers.io())
                .subscribe(connectedDevice -> {
                    if (null == device){
                        LOGE(LOG_TAG, "can't save last device history for null object");
                        return;
                    }
                    historyDb.setLastConnectedMotorcycleInfo(connectedDevice);
                    Long nowTime = System.currentTimeMillis();
                    LOGI(LOG_TAG, "Saving device: " + connectedDevice.getDeviceName() +
                        " as last device, connected at: " + nowTime);
                    historyDb.setLastConnectionStartTime(nowTime);
                    // connection is just started, clear connection end time
                    historyDb.setLastConnectionEndTime(0);
                });
    }

    @Override
    protected void start() {
        eventBus.register(this);
    }

    @Override
    public void suspend() {
        eventBus.unregister(this);
    }

    @Subscribe
    public void onEvent(ArgumentConnectionFailedEvent failedEvent){
        LOGI(LOG_TAG, "Connection port told that connection attempt have failed");
        connResultPipe.onNext(false);
    }

    /**
     * Bluetooth adapter might reject connection by some reason. In this case it will
     * go to 'idle' state
     * @param event
     */
    @Subscribe
    public void onEvent(ArgumentStateChangedEvent event){
        if (!event.isGeneralCallbackFired && event.portState == CONNECTED){
            LOGI(LOG_TAG, "Device connected ");
            connResultPipe.onNext(true);
        }
    }

    /**
     * Register for event, indicating that connection attempt failed
     * @param failedEvent
     */
    @Subscribe
    public void onConnectionFailedEvent(ArgumentConnectionFailedEvent failedEvent) {
        connResultPipe.onNext(false);
    }


    /**
     * Here is the tricky part - all three strategy need to connect to Bluetooth device at
     * some point and wait until this operation is complete. After that every strategy
     * has to process result in different way. All strategy has to notify external listener
     * of successful connection and update history of last connection. But, additional
     * behaviour vary, as well as behaviour in case of connection error. In case of 'reconnet'
     * strategy, when connection is established, we just have to close algorithm and do noting.
     * But, for 'lookup from history' behavior, when there is a lot of pending device candidates,
     * we have to tell concrete strategy, that job is done and we don't have to try connecting
     * to another devices from that list. Consider 'prompt selection' strategy - UI show list of
     * scanned devices and we have to select one to connect. If successful, show popup message and
     * close UI, but, in case of failure, we have to stay on the same ui, notify of error and
     * prompt to select another device.
     * At this point we updated info of last connected device and notified callback already,
     * just do some extra work.
     */
    protected abstract void doOnConnectionSuccessful(BtDevice device);

    /**
     * Connection attempt failed. We don't have to change records in device history database.
     * Base implementation won't notify callback of error - this is up to concrete strategy
     * to decide when it want to do so (maybe try again)
     */
    protected abstract void doOnConnectionAttemptFailed();

    /**
     * This method is called by strategy when it know that database has info about
     * last connected device and at point in time when strategy read that info in background
     * @param device
     */
    protected void createPendingConnectTask(BtDevice device){
        Observable<BtDevice> trigger = formConnDevicePipe(device);
        // release previous request is there is any
        cancelConnectionPendingRequest();
        pendingConnectTask = trigger
                .observeOn(Schedulers.computation())
                .subscribe(connectedDevice -> {
                    LOGI(LOG_TAG, "Connected to: " + connectedDevice.getDeviceName());
                    // save device we just connected to as last connected device into history db.
                    updateLastConnectedDeviceRecord(connectedDevice);
                    // tell callback that connection is established
                    ConnectionManagerCallback callback = getCallback();
                    if (null != callback){
                        callback.onConnectionEstablished(connectedDevice);
                    }
                    // call final action (abstract) - from concrete strategy
                    doOnConnectionSuccessful(connectedDevice);
                }, error -> {
                    LOGW(LOG_TAG, "Can't connect to device");
                    // attempt failed, don't touch history database.
                    doOnConnectionAttemptFailed();
                });
    }

    /**
     * Strategy is attempting to connect only when it has active subscription
     * to 'connect' task
     * @return
     */
    @Override
    public boolean isAttemptingToConnect() {
        return pendingConnectTask != null && !pendingConnectTask.isUnsubscribed();
    }

    /**
     * Unsubscribe from pending 'connect' task result and stop any
     * ongoing connection.
     */
    protected void cancelConnectionPendingRequest() {
        if (null != pendingConnectTask && !pendingConnectTask.isUnsubscribed()){
            pendingConnectTask.unsubscribe();
            pendingConnectTask = null;
        }
    }

    /**
     * Use callback to notify it that concrete connection strategy didn't work.
     */
    protected void notifyAboutFailure(){
        Observable.defer(() -> sendAttemptFailedEventTask)
                .subscribe(t -> {
                    if (t) {
                        LOGI(LOG_TAG, "callback notified of failure");
                        if (null != callback){
                            callback.onUnsupportedOperation();
                        }
                    } else {
                        LOGW(LOG_TAG, "callback is null, can't tell it of failure");
                    }
                });
    }

    /**
     * I assume here that failure notification process may take a while
     */
    protected void cancellFailureNotification(){
        if (null != notifyAboutFailure && !notifyAboutFailure.isUnsubscribed()){
            notifyAboutFailure.unsubscribe();
            notifyAboutFailure = null;
        }
    }

    public void setCallback(ConnectionManagerCallback reconnectCallback) {
        this.callback = reconnectCallback;
    }

    public ConnectionManagerCallback getCallback() {
        return callback;
    }

}
