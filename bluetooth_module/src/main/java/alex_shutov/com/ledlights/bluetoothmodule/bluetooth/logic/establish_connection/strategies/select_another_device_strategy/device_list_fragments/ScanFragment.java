package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.device_list_fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import alex_shutov.com.ledlights.bluetoothmodule.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.ChooseDeviceActivity;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp.AnotherDevicePresenter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lodoss on 08/12/16.
 */

/**
 * Ui for scanning for Bluetooth devices.
 * This fragment is a Ui, it contain some viewing logic. For example, it queries
 * all known devices and then show icons for paiting an history for each discovered device if that
 * device was known to application before.
 */
public class ScanFragment extends DevicesFragment {
    private static final String LOG_TAG = ScanFragment.class.getSimpleName();

    public static ScanFragment newInstance() {
        Bundle args = new Bundle();
        args.putInt(ARG_FRAGMENT_TYPE, ChooseDeviceActivity.FRAGMENT_SCAN);
        ScanFragment instance = new ScanFragment();
        instance.setArguments(args);
        return instance;
    }

    private AnotherDevicePresenter presenter;
    // connection of task, quering all history and paired devices.
    private Subscription knownDevicesLink;
    private Map<String, DeviceInfoViewModel> knownDevices = new TreeMap<>();

    @Override
    protected int getEmptyTextResource() {
        return R.string.device_discovery_not_found;
    }

    @Override
    protected void updateDeviceList() {
        startScanAlgorithm();
    }

    @Override
    protected void init() {
        if (null == presenter) {
            presenter = getPresenter();
            startScanAlgorithm();
        }
    }

    @Override
    protected void suspend() {
        stopScanAlgorithm();
    }

    private void startScanAlgorithm() {
        // check if scanning is in progress already
        if (isLoadingKnownDevices()) {
            Log.w(LOG_TAG, "scanning for devices is already in progress");
            return;
        }
        Observable<Boolean> knownDevicesTask =  Observable.defer(() -> getKnownDevices());
        knownDevicesLink =
                knownDevicesTask
                .observeOn(AndroidSchedulers.mainThread())
                        .delay(100, TimeUnit.MILLISECONDS)
                .subscribe(t -> {}, e -> {
                    onUpdateComplete();
                    }, () -> {
                            Toast.makeText(getActivity(), "All known devices received: " +
                                    knownDevices.size() , Toast.LENGTH_SHORT)
                                    .show();
                            onUpdateComplete();
                            showKnownDevices();
                        });
    }

    // get list of devices from history and convert into View Model
    Observable<DeviceInfoViewModel> historyDevices =
            Observable.defer(() ->
            presenter.queryDevicesFromAppHistory()
                    .take(1)
                    .flatMap(devices -> Observable.from(devices))
                    .map(device -> convertToViewModel(device))
                    .map(device -> {
                        device.setDeviceFromHistory(true);
                        return device;
                    }));
    // get list of paired devices and convert those into ViewModel
    Observable<DeviceInfoViewModel> pairedDevices =
            Observable.defer(() ->
            presenter.queryListOfPairedDevices()
                    .take(1)
                    .flatMap(devices -> Observable.from(devices))
                    .map(device -> convertToViewModel(device))
                    .map(device -> {
                        device.setPairedDevice(true);
                        return device;
                    }));

    /**
     * We need to display icons if newly discovered device is from history or
     * if phone is paired with it.
     * We query all devices from history and paired devices and then combine those to
     * single array.
     * Here I use old rxJava, in version 2 we could use Completable instead of Observable,
     * returning Boolean
     * @return
     */
    private Observable<Boolean> getKnownDevices() {
        AnotherDevicePresenter presenter = getPresenter();
        // mix all device into the same pipe
        Observable<Boolean> mapTask =
                Observable.merge(historyDevices, pairedDevices)
                .observeOn(Schedulers.computation())
                .doOnSubscribe(() -> knownDevices.clear())
                .doOnNext(device -> processKnownDevice(device))
                .map(t -> true);
        return mapTask;
    }

    private void processKnownDevice(DeviceInfoViewModel device) {
        Log.i(LOG_TAG, "Received device: " + device.getDeviceName());
        String deviceAddress = device.getDeviceAddress();
        // add new device to the list
        if (!knownDevices.containsKey(deviceAddress)) {
            knownDevices.put(deviceAddress, device);
        } else {
            // update saved view model
            // get saved device with the same address
            DeviceInfoViewModel savedDevice = knownDevices.get(deviceAddress);
            boolean fromHistory = savedDevice.isDeviceFromHistory();
            fromHistory |= device.isDeviceFromHistory();
            savedDevice.setDeviceFromHistory(fromHistory);
            boolean paired = savedDevice.isPairedDevice();
            paired |= device.isPairedDevice();
            savedDevice.setPairedDevice(paired);
            // we can't edit device description here, only device from history can have description
            String savedDescription = savedDevice.getDeviceDescription();
            if (savedDescription == null || savedDescription.equals("")) {
                String description = device.getDeviceDescription();
                if (description != null && !description.isEmpty()) {
                    savedDevice.setDeviceDescription(description);
                }
            }
        }
    }

    private void showKnownDevices() {
        Observable.defer(() -> Observable.just(""))
                .subscribeOn(Schedulers.computation())
                .map(t -> {
                    List<DeviceInfoViewModel> devices = new ArrayList<>();
                    devices.addAll(knownDevices.values());
                    return devices;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(models -> showDeviceList(models));
    }

    private boolean isLoadingKnownDevices() {
        return null != knownDevicesLink && !knownDevicesLink.isUnsubscribed();
    }

    private void stopScanAlgorithm() {
        if (null !=  knownDevicesLink && !knownDevicesLink.isUnsubscribed()) {
            knownDevicesLink.unsubscribe();
            knownDevicesLink = null;
        }
    }

}
