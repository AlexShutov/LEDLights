package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnEsbStore;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.BtAlgorithm;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnection;import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionCallback;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Alex on 11/5/2016.
 */
public abstract class EstablishConnectionStrategy extends BtAlgorithm
        implements EstablishConnection {
    private static final String LOG_TAG = EstablishConnectionStrategy.class.getSimpleName();
    private static EstablishConnectionCallback stubCallback = new EstablishConnectionCallback() {
        @Override
        public void onConnectionEstablished(BtDevice conenctedDevice) {        }

        @Override
        public void onAttemptFailed() {        }
    };
    private EstablishConnectionCallback callback;
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
    }

    /**
     *  The mext section is responsible for getting instance of EventBus and registering this
     *  object in it so we can track completion and failure events emitted into ESB.
     *  This logic has to be here in base class, because all strategies handle results in the
     *  same way.
     */

    /**
     * We meed a FRP- way to be notified whenever connection attempt is successful or if it
     * have failed
     */
    private PublishSubject<Boolean> connResultPipe = PublishSubject.create();

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
     * end what device we connected to.
     * Notice, i use Observable.defer(), because device will differ every time.
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
     * Inherited from EstablishConnection interface - actual interface implementation
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
     * connected device as well as start time of connection
     * @param device
     */
    protected void updateLastConnectedDeviceRecord(BtDevice device){
        Observable.defer(() -> Observable.just(device))
                .subscribeOn(Schedulers.io())
                .subscribe(connectedDevice -> {
                    if (null == device){
                        Log.e(LOG_TAG, "can't save last device history for null object");
                        return;
                    }
                    historyDb.setLastConnectedMotorcycleInfo(connectedDevice);
                    Long nowTime = System.currentTimeMillis();
                    Log.i(LOG_TAG, "Saving device: " + connectedDevice.getDeviceName() +
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
    public void onEvent(BtConnEsbStore.ArgumentConnectionFailedEvent failedEvent){
        Log.i(LOG_TAG, "Connection port told that connectionattempt have failed");
        connResultPipe.onNext(false);
    }

    @Subscribe
    public void onEvent(BtConnEsbStore.ArgumentStateChangedEvent event){
        if (!event.isGeneralCallbackFired && event.portState == BtConnEsbStore.PortState.CONNECTED){
            Log.i(LOG_TAG, "Device connected ");
            connResultPipe.onNext(true);
        }
    }




    /**
     * Use callback to notify it that concrete connection strategy didn't work.
     */
    protected void notifyAboutFailure(){
        Observable.defer(() -> sendAttemptFailedEventTask)
                .subscribe(t -> {
                    if (t) {
                        Log.i(LOG_TAG, "callback notified of failure");
                    } else {
                        Log.w(LOG_TAG, "callback is null, can't tell it of faulure");
                    }
                });
    }



    /**
     * I assume here that process of notifying about failure may take a while
     */
    protected void cancellFailureNotification(){
        if (null != notifyAboutFailure && !notifyAboutFailure.isUnsubscribed()){
            notifyAboutFailure.unsubscribe();
            notifyAboutFailure = null;
        }
    }

    public void setCallback(EstablishConnectionCallback callback) {
        this.callback = callback;
    }

    public EstablishConnectionCallback getCallback() {
        return callback;
    }
}
