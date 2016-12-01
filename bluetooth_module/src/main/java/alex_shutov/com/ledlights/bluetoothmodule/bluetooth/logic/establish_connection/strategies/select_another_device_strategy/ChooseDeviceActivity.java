package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.events.PresenterInstanceEvent;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp.AnotherDevicePresenter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp.AnotherDeviceView;

/**
 * Created by lodoss on 09/11/16.
 */
public class ChooseDeviceActivity extends AppCompatActivity implements AnotherDeviceView {
    private static final String LOG_TAG = ChooseDeviceActivity.class.getSimpleName();

    private EventBus eventBus;
    private AnotherDevicePresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_device);
        eventBus = EventBus.getDefault();
    }

    @Override
    protected void onPause() {
        eventBus.unregister(this);
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        eventBus.register(this);
    }

    /**
     * Activity receive presenter instance by sticky event. It MUST be already posted before
     * this activity is being started, because once it is, .onResume() method will register
     * Activity as a view in that presenter
     * @param instanceEvent
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(PresenterInstanceEvent instanceEvent) {
        presenter = instanceEvent.getPresenter();
        presenter.attachView(this);

        presenter.queryDevicesFromAppHistory();
        presenter.queryListOfPairedDevices();
    }

    /**
     * Inherited from AnothedDeviceView
     */

    @Override
    public void displayDevicesFromAppHistory(List<BtDevice> devices) {
        Log.i(LOG_TAG, "App remember " + devices.size() + " devices");
    }

    @Override
    public void displayPairedSystemDevices(List<BtDevice> devices) {
        Log.i(LOG_TAG, "Phone is paired to " + devices.size() + " devices");
    }
}
