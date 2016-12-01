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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    private Subscription linkQueryPairedDevices;

    /**
     * List of cached paired devices
     */
    private List<BtDevice> cachePairedDevices = new ArrayList<>();
    /**
     * Cached values of discovered devices. We need to know ids to check if this device is
     * discovered already - the same device can be returned immediately if we have a bond with it
     * and the next time - after discovery.
     */
    private Set<String> cacheIdsOfDiscoveredDevices = new TreeSet<>();
    private List<BtDevice> cachedDiscoveredDevices = new ArrayList<>();


    public AnotherDevicePresenter(EventBus eventBus, Context context) {
        super(eventBus);
        this.context = context;
    }

    /**
     * Start Activity, which will show user all available devices so he or she can pick one.
     */
    public void showUiForSelectingAnotherBluetoothDevice(){
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
        wipeOutCachedDevices();
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

    /**
     * Here it is assumed that this method is called from View so Model and View is attached -
     * we don't have to null check it
     */
    public void queryDevicesFromAppHistory(){
        linkQueryForAppHistoryDevices =
                getModel().getDevicesFromConnectionHistory()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(devices -> {
                    AnotherDeviceView v = getView();
                    v.displayDevicesFromAppHistory(devices);
                });
    }

    /**
     * Presenter caches paired and discovered devices, so we can return it to View right away.
     * Notice, Presenter will attempt to get paired devices from system if device list is
     * empty
     */
    public void queryListOfPairedDevices(){
        if (!cachePairedDevices.isEmpty()) {
            getView().displayPairedSystemDevices(cachePairedDevices);
        } else {
            refreshPairedDevices();
        }
    }


    /**
     * This link is active when discovery is in progress.
     * We can know if there is cached device by checkin cached discovered devices and checking if
     * this link is active;
     */
    private Subscription linkDeviceDiscovery;

    /**
     * Return all cached devices and query devices it wasn't done before
     */
    public void queryAllBluetoothDevicesWithDiscovery() {
        getModel().discoverDevices();
    }


    /**
     * We can figure out if discovery process is finished by checking list of discovered devices
     * and activity of discovery link
     * @return
     */
    private boolean isDiscoveryFinished(){
        return cacheIdsOfDiscoveredDevices.isEmpty() && null != linkDeviceDiscovery &&
                !linkDeviceDiscovery.isUnsubscribed();
    }



    /**
     * Model only know how to start discovery of all Bluetooth devices and how to get history of
     * all paired devices. But, unfortunately, it is too expansive to do it every time after
     * device screen rotation.
     * To solve that we need a method for updating all cached device history, which will be
     * triggered when user swipe screen for update or when there is no cached data at all.
     * We don't need to keep cached values all time, so Presenter is gonna wipe it out at a moment
     * when model is detached.
     */
    public void refreshDevicesFromSystem(){
        wipeOutCachedDevices();
        refreshPairedDevices();
        discoverDevices();
    }


    private void refreshPairedDevices(){
        // check if this operation requested already
        if (null != linkQueryPairedDevices && !linkQueryPairedDevices.isUnsubscribed()) {
            return;
        }
        linkQueryPairedDevices = getModel()
                .getPairedSystemDevices()
                .subscribeOn(Schedulers.io())
                .map(devices -> {
                    cachePairedDevices.clear();
                    cachePairedDevices.addAll(devices);
                    return cachePairedDevices;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(devices ->  {
                    getView().displayPairedSystemDevices(devices);
                });
    }

    private void discoverDevices(){

    }


    /**
     * Remove all cached device history.
     */
    private void wipeOutCachedDevices() {
        cachePairedDevices.clear();
        cacheIdsOfDiscoveredDevices.clear();
        cachedDiscoveredDevices.clear();
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
