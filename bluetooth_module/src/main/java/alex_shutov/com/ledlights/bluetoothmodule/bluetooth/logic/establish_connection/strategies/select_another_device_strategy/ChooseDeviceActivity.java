package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.database.DatabaseUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.events.PresenterInstanceEvent;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp.AnotherDevicePresenter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp.AnotherDeviceView;
import alex_shutov.com.ledlights.bluetoothmodule.databinding.ActivityPickDeviceBinding;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by lodoss on 09/11/16.
 */
public class ChooseDeviceActivity extends AppCompatActivity implements AnotherDeviceView {
    private static final String LOG_TAG = ChooseDeviceActivity.class.getSimpleName();

    private EventBus eventBus;
    private AnotherDevicePresenter presenter;

    ActivityPickDeviceBinding activityBinding;
    private DeviceInfoViewModel viewModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inflate layout, create databinding and set model
        View root = DataBindingUtil.setContentView(this, R.layout.activity_pick_device).getRoot();
        activityBinding = DataBindingUtil.bind(root);
        viewModel = new DeviceInfoViewModel();
        activityBinding.apdDeviceInfo.setModel(viewModel);

        viewModel.setDeviceName("Alpha 50cc");
        viewModel.setDeviceFromHistory(false);
        viewModel.setPairedDevice(true);
        viewModel.setShowDeviceDetailsListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChooseDeviceActivity.this, "details button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        eventBus = EventBus.getDefault();
        AppCompatButton button = (AppCompatButton) findViewById(R.id.apd_refresh);
        button.setOnClickListener(v -> {

            presenter.refreshDevicesFromSystem();
        });
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
        for (BtDevice device : devices){
            String msg = "Paired device: " + device.getDeviceName() + " " +
                    device.getDeviceAddress();
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
        BtDevice device;
    }


    @Override
    public void onNewDeviceDiscovered(BtDevice device) {
        String msg = "Device found: " + device.getDeviceName() + " : " +
                device.getDeviceAddress();
        Toast.makeText(this, msg , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDiscoveryComplete() {
        Toast.makeText(this, "Bluetooth discovery complete" , Toast.LENGTH_SHORT).show();
    }
}
