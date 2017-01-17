package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.device_list_fragments;

import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.databinding.ViewModelConverter;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by lodoss on 08/12/16.
 */

/**
 * Gets all known devices (history and paired) and forms list of view model from them
 */
public class KnownDevicesAlgorithm {
    public static final String LOG_TAG = KnownDevicesAlgorithm.class.getSimpleName();

    private PublishSubject<List<BtDevice>> historyDevicesSource;
    private PublishSubject<List<BtDevice>> pairedDevicesSource;
    private PublishSubject<Map<String, DeviceInfoViewModel>> resultPipe;

    private Subscription historySourceLink;
    private Subscription historyPipeLink;
    private Subscription pairedSourceLink;
    private Subscription pairedPipeLink;

    private Map<String, DeviceInfoViewModel> knownDevices = new TreeMap<>();

    // get list of devices from history and convert into View Model
    Observable<DeviceInfoViewModel> historyDevices =
                    Observable.defer(() -> historyDevicesSource)
                            .take(1)
                            .flatMap(devices -> Observable.from(devices))
                            .map(device -> ViewModelConverter.convertToViewModel(device))
                            .map(device -> {
                                device.setDeviceFromHistory(true);
                                return device;
                            });
    // get list of paired devices and convert those into ViewModel
    Observable<DeviceInfoViewModel> pairedDevices =
                    Observable.defer(() -> pairedDevicesSource)
                            .take(1)
                            .flatMap(devices -> Observable.from(devices))
                            .map(device -> ViewModelConverter.convertToViewModel(device))
                            .map(device -> {
                                device.setPairedDevice(true);
                                return device;
                            });

    /**
     * Consider algorithm finished if at least one of lookups is still running
     * @return
     */
    public boolean isRunning() {
        boolean historyLookupRunning = historySourceLink != null && !historySourceLink.isUnsubscribed();
        boolean pairedLookupRunning = pairedSourceLink != null && !pairedSourceLink.isUnsubscribed();
        boolean historyPipeLinkActive = historyPipeLink != null && !historyPipeLink.isUnsubscribed();
        boolean pairedPipeLinkActive = pairedPipeLink != null && !pairedPipeLink.isUnsubscribed();
        return historyLookupRunning || pairedLookupRunning ||
                historyPipeLinkActive || pairedPipeLinkActive;
    }

    /**
     * stop algorithm if it is active
     * @return true if algorithm was active, false - otherwise
     */
    public boolean stop() {
        if (!isRunning()) return false;
        if (historySourceLink != null && !historySourceLink.isUnsubscribed()) {
            historySourceLink.unsubscribe();
            historySourceLink = null;
        }
        if (pairedSourceLink != null && !pairedSourceLink.isUnsubscribed()) {
            pairedSourceLink.unsubscribe();
            pairedSourceLink = null;
        }
        if (historyPipeLink != null && !historyPipeLink.isUnsubscribed()) {
            historyPipeLink.unsubscribe();
            historyPipeLink = null;
        }
        if (pairedPipeLink != null && !pairedPipeLink.isUnsubscribed()) {
            pairedPipeLink.unsubscribe();
            pairedPipeLink = null;
        }
        knownDevices.clear();
        knownDevices = null;
        return true;
    }


    public Observable<Map<String, DeviceInfoViewModel>> createPipeline() {
        // stop algorithm if it active
        if (isRunning()) {
            Log.i(LOG_TAG, "algorithm for known devices is active, stopping it.");
            stop();
        }
        historyDevicesSource = PublishSubject.create();
        pairedDevicesSource = PublishSubject.create();
        // we now have all data emitter and receivers, next step -configure pipeline
        return configurePipeline();
    }

    public void start(Observable<List<BtDevice>> historySource,
                      Observable<List<BtDevice>> pairedSource) {
        // pipeline is configure, connect data emitters to external sources
        historySourceLink = historySource.observeOn(Schedulers.computation())
                .subscribe(historyDevicesSource);
        pairedSourceLink = pairedSource.observeOn(Schedulers.computation())
                .subscribe(pairedDevicesSource);
    }

    /**
     *
     */
    private Observable<Map<String, DeviceInfoViewModel>> configurePipeline() {
        Log.i(LOG_TAG, "Configuring pipeline");
        knownDevices = new TreeMap<>();
//        historyPipeLink =
//             historyDevices
//                    .doOnCompleted(() -> {
//                        completionSource.onNext(true);
//                        completionSource.onCompleted();
//                    })
//                    .subscribe(vm -> {
//                        Log.i(LOG_TAG, "View model received for: " + vm.getDeviceName());
//                 });
//        pairedPipeLink =
//                pairedDevices
//                        .doOnCompleted(() -> {
//                        completionSource.onNext(true);
//                        completionSource.onCompleted();
//                    })
//                    .subscribe(vm -> {
//                        Log.i(LOG_TAG, "View model received for: " + vm.getDeviceName());
//                 });


        Observable<Map<String, DeviceInfoViewModel>> pipe =
                Observable.merge(Observable.defer(() -> historyDevices),
                        Observable.defer(() -> pairedDevices))
                        .subscribeOn(Schedulers.computation())
                        .doOnNext(viewModel -> processKnownDevice(viewModel))
                        .last()
                        .map(t -> knownDevices);
        return Observable.defer(() -> pipe);
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

}
