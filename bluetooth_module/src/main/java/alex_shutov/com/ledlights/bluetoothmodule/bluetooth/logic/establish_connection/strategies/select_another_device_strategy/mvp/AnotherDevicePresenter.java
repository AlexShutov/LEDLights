package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.ChooseDeviceActivity;
import alex_shutov.com.ledlights.hex_general.BasePresenter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

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
     * Model only know how to start discovery of all Bluetooth devices and how to get history of
     * all paired devices. But, unfortunately, it is too expansive to do it every time after
     * device screen rotation.
     * To solve that we need a method for updating all cached device history, which will be
     * triggered when user swipe screen for update or when there is no cached data at all.
     * We don't need to keep cached values all time, so Presenter is gonne wipe it out at a moment
     * when model is detached.
     */
    public void refreshDevicesFromSystem(){

    }




    private void severAllLinks(){
        if (null != linkQueryForAppHistoryDevices && !linkQueryForAppHistoryDevices.isUnsubscribed()) {
            linkQueryForAppHistoryDevices.unsubscribe();
            linkQueryForAppHistoryDevices = null;
        }
    }
}
