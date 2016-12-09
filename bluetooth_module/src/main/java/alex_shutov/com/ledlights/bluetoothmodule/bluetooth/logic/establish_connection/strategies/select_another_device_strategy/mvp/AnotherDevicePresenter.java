package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.ChooseDeviceActivity;
import alex_shutov.com.ledlights.hex_general.BasePresenter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by lodoss on 01/12/16.
 */

/**
 * This presenter mediates interaction between strategy for selecting another device (Model)
 * and actual UI activity
 */

public class AnotherDevicePresenter extends BasePresenter<AnotherDeviceModel, AnotherDeviceView> {
    private static final String LOG_TAG = AnotherDevicePresenter.class.getSimpleName();

    private Context context;

    private Subscription linkQueryForAppHistoryDevices;
    /**
     * Presenter use model to query paired devices. This is done in background and presenter might
     * be detached from model during process. If that happens, it need a way to cancel operation.
     */
    private Subscription linkQueryPairedDevices;
    /**
     * This link is active when discovery is in progress.
     * We can know if there is cached device by checkin cached discovered devices and checking if
     * this link is active;
     */
    private Subscription linkDeviceDiscovery;


    /**
     * List of cached paired devices
     */
    private List<BtDevice> cachePairedDevices = new ArrayList<>();
    /**
     * Cached values of discovered devices. We need to know ids to check if this device is
     * discovered already - the same device can be returned immediately if we have a bond with it
     * and the next time - after discovery.
     */
    private Set<String> cacheDiscoveredAddresses = new TreeSet<>();
    private List<BtDevice> cachedDiscoveredDevices = new ArrayList<>();

    public AnotherDevicePresenter(EventBus eventBus, Context context) {
        super(eventBus);
        this.context = context;
    }

    /**
     * Start Activity, which will show user all available devices so he or she can pick one.
     */
    public void showUiForSelectingAnotherBluetoothDevice() {
        Intent startIntent = new Intent(context, ChooseDeviceActivity.class);
        startIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startIntent);
    }

    /**
     * Inherited from BasePresenter
     */

    @Override
    protected void onViewAttached() {

    }

    @Override
    protected void onViewDetached() {
        Log.i(LOG_TAG, "View detached");
        severAllLinks();
        cachedDiscoveredDevices.clear();
        cacheDiscoveredAddresses.clear();
        cachePairedDevices.clear();
    }

    @Override
    protected void onModelAttached() {

    }

    @Override
    protected void onModelDetached() {
        Log.i(LOG_TAG, "Model detached");
        severAllLinks();
    }

    /**
     * Its own methods
     */

    private PublishSubject<List<BtDevice>> sourceHistory = PublishSubject.create();
    private PublishSubject<List<BtDevice>> sourcePairedDevices = PublishSubject.create();

    /**
     * Get link to a source, emitting paired devices
     * @return
     */
    public Observable<List<BtDevice>> getSourcePairedDevices() {
        return sourcePairedDevices.asObservable();
    }

    /**
     * Here it is assumed that this method is called from View so Model and View is attached -
     * we don't have to null check it
     */
    public Observable<List<BtDevice>> queryDevicesFromAppHistory() {
        linkQueryForAppHistoryDevices =
                getModel().getDevicesFromConnectionHistory()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(devices -> {
                    Log.i(LOG_TAG, devices.size() + " devices in history");
                    sourceHistory.onNext(devices);
//                    AnotherDeviceView v = getView();
//                    v.displayDevicesFromAppHistory(devices);
                });
        return sourceHistory.asObservable().take(1);
    }

    /**
     * Presenter caches paired and discovered devices, so we can return it to View right away.
     * Notice, Presenter will attempt to get paired devices from system if device list is
     * empty
     */
    public Observable<List<BtDevice>> queryListOfPairedDevices() {
        if (!cachePairedDevices.isEmpty()) {
            sourcePairedDevices.onNext(cachePairedDevices);
            Log.i(LOG_TAG, cachePairedDevices.size() + " cached paired devices");
//            getView().displayPairedSystemDevices(cachePairedDevices);
        } else {
            refreshPairedDevices();
        }
        return sourcePairedDevices.asObservable()
                .take(1);
    }

    /**
     * Return all cached devices and query devices it wasn't done before.
     * All discovered devices is added to cache. Consider the following case - view request
     * discovery and then request it again while previous request is in progress.
     * Newly discovered devices will be saved to cache and handed off to View anyways
     */
    public void queryAllBluetoothDevicesWithDiscovery() {
        if (isDiscoveryFinished()) {
            for (BtDevice d : cachedDiscoveredDevices) {
//                getView().onNewDeviceDiscovered(d);
            }
        } else {
            discoverDevices();
        }
    }

    /**
     * Check link to discovery task. If discovery is active, this link is active too.
     * In presenter request discovery and then gets detached from model, discovery will be
     * re-activated (scanner stops discovery before starting it, making sure there is always
     * one active discovery)
     * @return
     */
    private boolean isDiscoveryInProgress(){
        return linkDeviceDiscovery != null && !linkDeviceDiscovery.isUnsubscribed();
    }

    /**
     * We can figure out if discovery process is finished by checking list of discovered devices
     * and activity of discovery link
     * @return
     */
    private boolean isDiscoveryFinished(){
        return !cacheDiscoveredAddresses.isEmpty() || isDiscoveryInProgress();
    }

    /**
     * Model only know how to createPipeline discovery of all Bluetooth devices and how to get history of
     * all paired devices. But, unfortunately, it is too expansive to do it every time after
     * device screen rotation.
     * To solve that we need a method for updating all cached device history, which will be
     * triggered when user swipe screen for update or when there is no cached data at all.
     * We don't need to keep cached values all time, so Presenter is gonna wipe it out at a moment
     * when model is detached.
     * This method is supposed to be used when user want update all data, so here app history
     * is queried too.
     */
    public void refreshDevicesFromSystem(boolean startDiscovery) {
        refreshPairedDevices();
        queryDevicesFromAppHistory();
        if (startDiscovery) {
            discoverDevices();
        }
    }

    private void refreshPairedDevices() {
        Log.i(LOG_TAG, "refreshPairedDevices()");
        // check if this operation requested already
        if (null != linkQueryPairedDevices && !linkQueryPairedDevices.isUnsubscribed()) {
            return;
        }
        // remove all cached paired devices
        cachePairedDevices.clear();
        linkQueryPairedDevices = getModel()
                .getPairedSystemDevices()
                .subscribeOn(Schedulers.io())
                .map(devices -> {
                    Log.i(LOG_TAG, "Refreshed: " + devices.size() + " paired devices");
                    cachePairedDevices.clear();
                    cachePairedDevices.addAll(devices);
                    return cachePairedDevices;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(devices ->  {
                    sourcePairedDevices.onNext(devices);
//                    getView().displayPairedSystemDevices(devices);
                });
    }

    private void discoverDevices(){
        // check is discovery already in progress and do nothing if it is
        if (isDiscoveryInProgress()) {
            Log.w(LOG_TAG, "Trying to createPipeline discovery while another discovery is still in progress");
            return;
        }
        Observable<BtDevice> discoveryTask =
                Observable.just("")
                .subscribeOn(Schedulers.computation())
                .map(t -> {
                    // clear cached discovery data
                    cacheDiscoveredAddresses.clear();
                    cachedDiscoveredDevices.clear();
                    return t;
                })
                .observeOn(Schedulers.io())
                .flatMap(t -> getModel().discoverDevices())
                        .subscribeOn(Schedulers.computation())
                        // process only new devices (safeguard)
                .filter(foundDevice -> {
                    String address = foundDevice.getDeviceAddress();
                    boolean alreadyThere = cacheDiscoveredAddresses.contains(address);
                    return !alreadyThere;
                })
                // save device info into cache
                .map(device -> {
                    cachedDiscoveredDevices.add(device);
                    cacheDiscoveredAddresses.add(device.getDeviceAddress());
                    return device;
                });
        linkDeviceDiscovery = Observable.defer(() -> discoveryTask)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(device -> {
                    AnotherDeviceView view = getView();
//                    view.onNewDeviceDiscovered(device);
                }, error -> {
                    Log.e(LOG_TAG, "Error during device discovery");
//                    getView().onDiscoveryComplete();
                }, () -> {
//                    getView().onDiscoveryComplete();
                });
    }


    /**
     * Cancel all pending jobs
     */
    private void severAllLinks(){
        if (null != linkQueryForAppHistoryDevices && !linkQueryForAppHistoryDevices.isUnsubscribed()) {
            linkQueryForAppHistoryDevices.unsubscribe();
            linkQueryForAppHistoryDevices = null;
        }
        if (null != linkQueryPairedDevices && !linkQueryPairedDevices.isUnsubscribed()) {
            linkQueryPairedDevices.unsubscribe();
            linkQueryPairedDevices = null;
        }
        if (null != linkDeviceDiscovery && !linkDeviceDiscovery.isUnsubscribed()) {
            linkDeviceDiscovery.unsubscribe();
            linkDeviceDiscovery = null;
        }
    }
}
