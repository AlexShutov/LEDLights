package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;
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
    private BtDeviceDao historyDb;
    private BtConnPort connPort;
    private EventBus eventBus;
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
            });
    private Subscription lastDeviceTaskState;

    public ReconnectStrategy(){
        // set empty callback by default from base class
        super();
        lastDeviceTaskState = null;
    }

    @Override
    public void init(DataProvider dataProvider) {
        EstablishConnectionDataProvider provider = (EstablishConnectionDataProvider) dataProvider;
        historyDb = provider.provideHistoryDatabase();
        connPort = provider.provideBtConnPort();
        eventBus = provider.provideEventBus();
    }

    @Override
    public void attemptToEstablishConnection() {
        Log.i(LOG_TAG, "attemptToEstablishConnection()");
        stopLastDeviceTask();
        lastDeviceTaskState = Observable.defer(() -> getLastDeviceFromDbtask)
                .observeOn(Schedulers.computation())
                .subscribe(lastDeviceInfo -> {
                    Log.i(LOG_TAG, "Last connected device: " + lastDeviceInfo.deviceInfo.getDeviceName());
                }, error -> {
                    Log.w(LOG_TAG, "There is no info about last connected device");
                });
    }

    @Override
    public boolean isAttemptingToConnect() {
        return false;
    }

    @Override
    public void stopConnecting() {
        stopLastDeviceTask();
    }

    /**
     * Unsubscribe from that task if it is active
     */
    private void stopLastDeviceTask(){
        if (null != lastDeviceTaskState && !lastDeviceTaskState.isUnsubscribed()){
            lastDeviceTaskState.unsubscribe();
            lastDeviceTaskState = null;
        }
    }
}
