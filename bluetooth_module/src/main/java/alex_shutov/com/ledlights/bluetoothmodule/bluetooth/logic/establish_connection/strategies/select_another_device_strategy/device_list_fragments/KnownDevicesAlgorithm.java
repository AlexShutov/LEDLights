package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.device_list_fragments;

import java.util.ArrayList;
import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;
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

    private PublishSubject<List<BtDevice>> historyDevicesSource;
    private PublishSubject<List<BtDevice>> pairedDevicesSource;
    private PublishSubject<List<DeviceInfoViewModel>> resultSource;

    private Subscription historyLink;
    private Subscription pairedLink;

    /**
     *
     * @return
     */
    public Observable<List<DeviceInfoViewModel>> start(Observable<List<BtDevice>> historySource,
                                                       Observable<List<BtDevice>> pairedSource) {

        historyDevicesSource = PublishSubject.create();
        pairedDevicesSource = PublishSubject.create();
        resultSource = PublishSubject.create();



        historyLink = historySource.observeOn(Schedulers.computation())
                .subscribe(historyDevicesSource);
        pairedLink = pairedSource.observeOn(Schedulers.computation())
                .subscribe(pairedDevicesSource);



        return resultSource.asObservable();
    }

    public void suspend() {
        if (null != historyLink && !historyLink.isUnsubscribed()) {
            historyLink.unsubscribe();
            historyLink = null;
        }
        if (null != pairedLink && !pairedLink.isUnsubscribed()) {
            pairedLink.unsubscribe();
            pairedLink = null;
        }
    }

    private void configurePipeline() {

    }

}
