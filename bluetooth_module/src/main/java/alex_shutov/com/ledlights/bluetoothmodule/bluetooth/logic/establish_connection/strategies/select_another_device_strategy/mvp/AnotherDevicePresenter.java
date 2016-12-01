package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp;

import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.ChooseDeviceActivity;
import alex_shutov.com.ledlights.hex_general.BasePresenter;

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

    }

    @Override
    protected void onModelAttached() {

    }

    @Override
    protected void onModelDetached() {

    }

}
