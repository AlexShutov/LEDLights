package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.device_list_fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import alex_shutov.com.ledlights.bluetoothmodule.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.ChooseDeviceActivity;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp.AnotherDevicePresenter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
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
//        s =
//        knownDevicesDrain.asObservable()
//                .map(vmm -> new ArrayList(vmm.values()))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(devices -> {
//                    Toast.makeText(getActivity(), "known devices received: " + devices.size(), Toast.LENGTH_SHORT).show();
//                    showDeviceList(devices);
//                });
//        scanLink.add(s);
        s =
        knownDevicesDrain.asObservable()
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
        if (null != scanLink && !scanLink.isUnsubscribed()) {
            scanLink.unsubscribe();
            scanLink = null;
        }
    }

    private void startDiscovery() {
        Log.i(LOG_TAG, "We have all known devices (" + knownDevices.size() + " psc., starting " +
                "discovery");
        Toast.makeText(getActivity(), "source emitted " + knownDevices.size() + " items",
                Toast.LENGTH_SHORT).show();

        hideEmptyText();
        onDiscoveryFinished();

    }


    /**
     *  This method is called when app finishes scan for Bluetooth devices.
     */
    private void onDiscoveryFinished() {
        onUpdateComplete();
    }

}
