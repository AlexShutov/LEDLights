package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.device_list_fragments;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.ChooseDeviceActivity;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by lodoss on 06/12/16.
 */

/**
 * In this fragment we'll show all devices from application history.
 * Every line from list has to have two state icons - if this device is from history and
 * if it is paired.
 * We can query list of paired devices from Presenter and do it quit fast.
 * After that, we create map of devices my their addresses and look for intersection of
 * history devices and paired devices.
 * I don't move this functionality into bas class, because paired devices will have 'base set'
 * from paired devices and this class from history devices.
 */
public class HistoryDevicesFragment extends DevicesFragment {

    public static final String LOG_TAG = HistoryDevicesFragment.class.getSimpleName();

    public static DevicesFragment newInstance() {
        Bundle args = new Bundle();
        args.putInt(ARG_FRAGMENT_TYPE, ChooseDeviceActivity.FRAGMENT_HISTORY);
        HistoryDevicesFragment instance = new HistoryDevicesFragment();
        instance.setArguments(args);
        return instance;
    }

    private PublishSubject<List<BtDevice>> historyDevicesSource = PublishSubject.create();
    private PublishSubject<List<BtDevice>> pairedDevicesSource = PublishSubject.create();
    /**
     * Resembles connection to algorithm for processing resulting lists
     */
    private Subscription listProcessingSubscription;


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
        getPresenter().refreshDevicesFromSystem(false);
    }

    @Override
    protected void suspend() {
        Log.i(LOG_TAG, "stopping updating device list");
        suspendReceivingAlgorithm();
    }

    @Override
    protected void init() {
        Observable<List<DeviceInfoViewModel>> processAlg =
                Observable.zip(historyDevicesSource.asObservable(),
                        pairedDevicesSource.asObservable(),
                        (history, paired) -> {
                            List<DeviceInfoViewModel> viewModels =
                                mapHistoryAndPairedLists(history, paired);
                            addUserActionListeners(viewModels);
                            return viewModels;
                        });
        // check if previous algorithm is running and suspend it if it is.
        suspendReceivingAlgorithm();
        listProcessingSubscription =
                Observable.defer(() -> processAlg)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(deviceList -> {
                            Log.i(LOG_TAG, deviceList.size() + " devices in history");
                            // device list is updated
                            onUpdateComplete();
                            showDeviceList(deviceList);
                        });
        // query device list
        getPresenter().queryDevicesFromAppHistory();
        getPresenter().queryListOfPairedDevices();
    }

    /**
     * Inherited from AnotherDeviceView
     */

    @Override
    public void displayDevicesFromAppHistory(List<BtDevice> devices) {
        Log.i(LOG_TAG, "There is " + devices.size() + " devices in app history");
        historyDevicesSource.onNext(devices);
    }

    @Override
    public void displayPairedSystemDevices(List<BtDevice> devices) {
        Log.i(LOG_TAG, "There is " + devices.size() + " paired devices");
        pairedDevicesSource.onNext(devices);
    }

    @Override
    public void onNewDeviceDiscovered(BtDevice device) {
        Log.e(LOG_TAG, "History fragment can't trigger discovery, error");
    }

    @Override
    public void onDiscoveryComplete() {
        Log.e(LOG_TAG, "History fragment can't trigger discovery, error");
    }

    private void suspendReceivingAlgorithm() {
        if (null != listProcessingSubscription && !listProcessingSubscription.isUnsubscribed()) {
            listProcessingSubscription.unsubscribe();
            listProcessingSubscription = null;
        }
    }

    private List<DeviceInfoViewModel> mapHistoryAndPairedLists(List<BtDevice> history,
                                                               List<BtDevice> paired) {
        List<DeviceInfoViewModel> historyViewModel = new ArrayList<>();
        Map<String, DeviceInfoViewModel> historyMapping = new TreeMap<>();
        for (BtDevice device : history) {
            DeviceInfoViewModel vm = convertToViewModel(device);
            // This is a device from history
            vm.setDeviceFromHistory(true);
            historyViewModel.add(vm);
            historyMapping.put(vm.getDeviceAddress(), vm);
        }
        // now check which paired devices is in device history and mark those devices as paired
        for (BtDevice device : paired) {
            String address = device.getDeviceAddress();
            if (!historyMapping.containsKey(address)) continue;
            // We're sure this device is paired to this phone
            historyMapping.get(address).setPairedDevice(true);
        }
        return historyViewModel;
    }

    private void addUserActionListeners(List<DeviceInfoViewModel> vms) {
        for (DeviceInfoViewModel vm : vms) {
            vm.setShowDeviceDetailsListener(v -> {
                UserActionListener l = (UserActionListener) getActivity();
                l.onAdditionalInfoClicked(getFragmentType(), vm);
            });
        }

    }

}
