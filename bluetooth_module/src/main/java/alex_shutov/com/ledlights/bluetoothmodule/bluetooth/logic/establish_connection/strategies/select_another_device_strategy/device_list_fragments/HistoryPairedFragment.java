package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.device_list_fragments;

import android.util.Log;

import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lodoss on 06/12/16.
 */

/**
 * We have two similarly looking fragments with small differences - one show
 * list of devices from application history and another - paired devices.
 * But, device info has two icons (history and pairing), so we need both paired and
 * history device lists.
 * Difference between
 */

public abstract class HistoryPairedFragment extends DevicesFragment {

    public static final String LOG_TAG = HistoryPairedFragment.class.getSimpleName();

    private PublishSubject<List<BtDevice>> historyDevicesSource = PublishSubject.create();
    private PublishSubject<List<BtDevice>> pairedDevicesSource = PublishSubject.create();

    /**
     * Implement it for 'history' and 'pairing' fragments accordingly
     * @param history
     * @param paired
     * @return
     */
    protected abstract List<DeviceInfoViewModel> mapHistoryAndPairedLists(List<BtDevice> history,
                                                                          List<BtDevice> paired);


    Observable<List<DeviceInfoViewModel>> processAlg =
            Observable.zip(historyDevicesSource.asObservable(),
                    pairedDevicesSource.asObservable(),
                    (history, paired) -> {
                        List<DeviceInfoViewModel> viewModels =
                                mapHistoryAndPairedLists(history, paired);
                        addUserActionListeners(viewModels);
                        return viewModels;
                    });

    /**
     * Resembles connection to algorithm for processing resulting lists
     */
    private CompositeSubscription listProcessingSubscription;

    /**
     * Inherited from DevicesFragment
     */

    /**
     * This method is triggered when user swipes screen for update.
     * History devices don't need discovery, so we just need to update cached values
     */
    @Override
    protected void updateDeviceList() {
        Log.i(LOG_TAG, "Updating history and paired device lists");
        onUpdateComplete();
    }

    @Override
    protected void suspend() {
        Log.i(LOG_TAG, "stopping updating device list");
        suspendReceivingAlgorithm();
    }

    @Override
    protected void init() {
        // check if previous algorithm is running and suspend it if it is.
        suspendReceivingAlgorithm();
        listProcessingSubscription = new CompositeSubscription();
        Subscription task =
                Observable.defer(() -> processAlg)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(deviceList -> {
                            Log.i(LOG_TAG, deviceList.size() + " devices in history");
                            // device list is updated
                            onUpdateComplete();
                            // show ot hide empty text and change list visibility
                            getViewModel().setEmpty(deviceList.isEmpty());
                            showDeviceList(deviceList);
                        });
        listProcessingSubscription.add(task);
        // query device list
        Subscription s = getPresenter().queryDevicesFromAppHistory()
                .subscribe(historyDevicesSource);
        listProcessingSubscription.add(s);
        s = getPresenter().queryListOfPairedDevices()
                .subscribe(pairedDevicesSource);
        listProcessingSubscription.add(s);
    }

    /**
     * Inherited from AnotherDeviceView
     */


    public void displayDevicesFromAppHistory(List<BtDevice> devices) {
        Log.i(LOG_TAG, "There is " + devices.size() + " devices in app history");
        historyDevicesSource.onNext(devices);
    }


    public void displayPairedSystemDevices(List<BtDevice> devices) {
        Log.i(LOG_TAG, "There is " + devices.size() + " paired devices");
        pairedDevicesSource.onNext(devices);
    }


    private void suspendReceivingAlgorithm() {
        if (null != listProcessingSubscription && !listProcessingSubscription.isUnsubscribed()) {
            listProcessingSubscription.unsubscribe();
            listProcessingSubscription = null;
        }
    }
}
