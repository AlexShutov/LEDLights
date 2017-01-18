package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.device_list_fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import alex_shutov.com.ledlights.bluetoothmodule.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.ChooseDeviceActivity;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.mvp.AnotherDevicePresenter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.databinding.ViewModelConverter.convertToViewModel;
import static alex_shutov.com.ledlights.hex_general.common.utils.impl.LogUtils.LOGI;
import static alex_shutov.com.ledlights.hex_general.common.utils.impl.LogUtils.LOGW;

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

    /**
     * Subscription, containing all supplimentary subscriptions, used by scanning algorithm
     */
    private CompositeSubscription scanLink;

    public ScanFragment() {
        super();
        // new instance of algorithm, retriving all known devices
        knownDevicesFetcher = new KnownDevicesAlgorithm();
    }

    private AnotherDevicePresenter presenter;
    private KnownDevicesAlgorithm knownDevicesFetcher;

    @Override
    protected int getEmptyTextResource() {
        return R.string.device_discovery_not_found;
    }

    @Override
    protected void updateDeviceList() {
        // hide prompt to refresh
        hideEmptyText();
        // and begin scan process
        configureGettingKnownDevices();
        startAlgorithm();
    }

    @Override
    protected void init() {
        presenter = getPresenter();
        showEmptyText(R.string.device_list_discovery_prompt_to_refresh);
    }

    @Override
    protected void suspend() {
    }

    private PublishSubject<Map<String, DeviceInfoViewModel>> knownDevicesDrain;
    private Map<String, DeviceInfoViewModel> knownDevices;

    /**
     * Before we start discovering new devices we need to have information about all devices,
     * known to this app and phone (history and paired devices).  It is necessary, because it is
     * convenient, showing icons if that device not completely new.
     */
    private void configureGettingKnownDevices() {
        // stop previous scanning if it active (normally it should not be)
        stopScanAlgorithm();
        scanLink = new CompositeSubscription();
        Subscription s;
        // allocate new instance of drain, receiving known devices
        knownDevicesDrain = PublishSubject.create();

        // create pipeline by using algorithm and pass known devices to
        // result drain
        s = knownDevicesFetcher.createPipeline()
                .subscribe(knownDevicesDrain);
        scanLink.add(s);
        s = knownDevicesDrain.asObservable()
                //.observeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .map(devices -> {
                    // save list of devices
                    knownDevices = devices;
                    startDiscovery();
                    return devices;
                })
                .subscribe(devices -> {
                    Toast.makeText(getActivity(), "starting discovery", Toast.LENGTH_SHORT).show();
                });
        scanLink.add(s);
    }

    private void startAlgorithm() {
        // query device list from history
        Observable<List<BtDevice>> history =
                getPresenter().queryDevicesFromAppHistory()
                        .take(1);
        // query devices paired to this phone
        Observable<List<BtDevice>> pairedSource =
                presenter.getSourcePairedDevices()
                        .take(1)
                        .subscribeOn(Schedulers.computation());
        // start algorithm by subscribing to device sources. By doing so, app will start fetching
        // known devices
        knownDevicesFetcher.start(Observable.defer(() -> history), pairedSource);
        presenter.queryListOfPairedDevices();
    }

    private void stopScanAlgorithm() {
        discoveredAddresses.clear();
        if (null != scanLink && !scanLink.isUnsubscribed()) {
            scanLink.unsubscribe();
            scanLink = null;
        }
    }

    private void startDiscovery() {
        LOGI(LOG_TAG, "We have all known devices (" + knownDevices.size() + " psc., starting " +
                "discovery");

        // hide text, prompting to start discovery
        hideEmptyText();
        // clear device list
        showDeviceList(new ArrayList<>());

        Observable<BtDevice> discoveryTask = presenter.createDiscoveryTask();
        discoveryTask.subscribeOn(Schedulers.io())
                .doOnNext(device -> processDiscoveredDevice(device))
        //   receive discovered devices on a main threadp to update UI
        .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        device -> {
                            // we already processed that device in .onNext() on background
                            // thread ( see few lines above)
                        }, error -> {
                            showPopup("Error occurred during discovery: " + error.getMessage());
                        }, () -> {
                            onDiscoveryFinished();
                        });
        presenter.startDiscovery();
    }

    /**
     * We have an issue here - on my HTC Desire C each device is gets discovered few times,
     * perhaps, because it is already paired, but, can be connected to unsecurely.
     * But, that device has the same address. We can ignore multiple occurrences of
     * that device by having set, containing addresses of all located devices
     */
    private Set<String> discoveredAddresses = new TreeSet<>();

    /**
     *  This method is called when app finishes scan for Bluetooth devices.
     */
    private void onDiscoveryFinished() {
        onUpdateComplete();
        showPopup("Discovery complete");
        // check if at least one device is discovered. If not, show message, that no
        // devices were found.
        if (discoveredAddresses.isEmpty()) {
            LOGI(LOG_TAG, "No devices found");
            showEmptyText(R.string.device_discovery_not_found);
        }
    }

    /**
     * Transform device info to view model and show that view model in the list
     * @param justDiscovered
     */
    private void processDiscoveredDevice(BtDevice justDiscovered) {
        LOGI(LOG_TAG, "Processing discovered device: " + justDiscovered.getDeviceName() +
            " " + justDiscovered.getDeviceAddress());
        DeviceInfoViewModel vm = convertToViewModel(justDiscovered);
        String deviceAddress = vm.getDeviceAddress();
        if (discoveredAddresses.contains(deviceAddress)) {
            // we already handled that device, it was discovered again (not sure why,
            // maybe because it can be connected secure and unsecure)
            return;
        }
        discoveredAddresses.add(deviceAddress);
        // we have to form final view model user will see
        DeviceInfoViewModel modelToShow = null;
        // check if that device is from known devices
        if (knownDevices.containsKey(deviceAddress)) {
            modelToShow = knownDevices.get(deviceAddress);
            // check device name, app cache might have older one
            // name of newly discovered device
            String deviceName = vm.getDeviceName() == null ? "" : vm.getDeviceName();
            if (modelToShow.getDeviceName() != null &&
                    !deviceName.equals(modelToShow.getDeviceName())) {
                // use newer name from discovery result
                // notice, it will now override database of history device nor paired device
                modelToShow.setDeviceName(deviceName);
            }
        } else {
            // or, if not, use model we just converted
            modelToShow = vm;
        }
        //create final reference to model
        DeviceInfoViewModel t = modelToShow;
        // add listener for selection tap and details button+
        addUserActionListeners(t);
        // Some devices might have no device name (some Apple keyboards, etc.).
        // In that case show name for 'unknown device'
        if (t.getDeviceName() == null) {
            LOGW(LOG_TAG, "Device name not specified for device with address: " +
                    t.getDeviceAddress());
            String nameUnknown = getString(R.string.device_info_unnamed);
            t.setDeviceName(nameUnknown);
        }
        // and add it to the list on main thread
        Observable.defer(() -> Observable.just(t))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> addDeviceToTheList(model));
    }

    private void showPopup(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
