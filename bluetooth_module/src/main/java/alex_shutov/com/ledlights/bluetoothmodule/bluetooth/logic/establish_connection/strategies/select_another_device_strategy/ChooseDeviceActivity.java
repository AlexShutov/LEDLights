package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.databinding.ViewModelConverter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.device_list_fragments.DevicesFragment;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.device_list_fragments.HistoryDevicesFragment;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.device_list_fragments.PairedDevicesFragment;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.device_list_fragments.ScanFragment;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.dialogs.ConnectAttemptFailedDialog;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.dialogs.DeviceInfoDialog;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.events.ConnectionAttemptFailedEvent;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.events.PresenterInstanceEvent;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp.AnotherDevicePresenter;
import alex_shutov.com.ledlights.bluetoothmodule.databinding.ActivityPickDeviceBinding;
import rx.Observable;
import rx.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by lodoss on 09/11/16.
 */
public class ChooseDeviceActivity extends AppCompatActivity implements
        DevicesFragment.UserActionListener, ConnectAttemptFailedDialog.ConnectionFailedDialogCallback {
    private static final String LOG_TAG = ChooseDeviceActivity.class.getSimpleName();
    public static final int FRAGMENT_HISTORY = 0;
    public static final int FRAGMENT_PAIRED = 1;
    public static final int FRAGMENT_SCAN = 2;

    private EventBus eventBus;
    private AnotherDevicePresenter presenter;

    /** Binding for this Activity. It is used for setting up TabLayout and ViewPager */
    private ActivityPickDeviceBinding activityBinding;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate()");
        // inflate layout, create data binding and set model
        View root = DataBindingUtil.setContentView(this, R.layout.activity_pick_device).getRoot();
        activityBinding = DataBindingUtil.bind(root);
        // create view model for this Activity
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            presenter.onUserRefusedToPickDevice();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Activity receive presenter instance by sticky event. It MUST be already posted before
     * this activity is being started, because once it is, .onResume() method will register
     * Activity as a view in that presenter. Here we don't attach this Activity to Presenter,
     * because all work is done in Fragments, implementing AnotherDeviceView
     * @param instanceEvent
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(PresenterInstanceEvent instanceEvent) {
        if (null == presenter) {
            presenter = instanceEvent.getPresenter();
            Log.i(LOG_TAG, "Initializing Activity for choosing device");
            init();
        }
    }

    /**
     * Remember device we could not to connect
     */
    private BtDevice notConnectedDevice;
    /**
     * If connection attempt fails, model inform presenter of that. Presenter, in turn,
     * tell this View (Activity) show dialog, allowing user to deal with it, or try to connect again
     * @param event
     */
    @Subscribe
    public void onConnectionAttemptFailedEvent(ConnectionAttemptFailedEvent event) {
        notConnectedDevice = event.getDevice();
        DeviceInfoViewModel model =
                ViewModelConverter.convertToViewModel(notConnectedDevice);
        ConnectAttemptFailedDialog dialog = new ConnectAttemptFailedDialog();
        dialog.setModel(model);
        dialog.show(getSupportFragmentManager(), "connection_failed_dialog");
    }

    /**
     * Init all fragments once we have presenter reference
     */
    private void init() {
        if (activityBinding.apdVpDevices != null) {
            setupViewPager();
            activityBinding.apdVpDevices.setCurrentItem(FRAGMENT_HISTORY);
        }
        activityBinding.apdTlViewModeSelector.setupWithViewPager(activityBinding.apdVpDevices);
    }

    /**
     * Inherited from AnothedDeviceView
     */

    private void setupViewPager() {
        Log.i(LOG_TAG, "Setting up ViewPager");
        Adapter adapter = new Adapter(getSupportFragmentManager());
        // init all fragments here
        // fragment for device history
        DevicesFragment historyDevicesFragment = HistoryDevicesFragment.newInstance();
        adapter.addFragment(historyDevicesFragment, getString(R.string.device_list_history));
        // fragment for paired devices
        DevicesFragment pairedDevicesFragment = PairedDevicesFragment.newInstance();
        adapter.addFragment(pairedDevicesFragment, getString(R.string.device_list_paired));
        // fragment for scanning Bluetooth devices
        ScanFragment scanFragment = ScanFragment.newInstance();
        adapter.addFragment(scanFragment, getString(R.string.device_list_scan));
        activityBinding.apdVpDevices.setAdapter(adapter);
    }

    /**
     * Inherited from DevicesFragment.UserActionListener -
     */

    @Override
    public void onDevicePicked(int fragmentType, DeviceInfoViewModel device) {
        Log.i(LOG_TAG, "Device picked: " + device.getDeviceName() + " " +
                device.getDeviceAddress());
        Observable.defer(() -> Observable.just(device))
                .subscribeOn(Schedulers.computation())
                .map(vm -> ViewModelConverter.fromViewModel(vm))
                .subscribe(d -> presenter.onDeviceSelected(d));
        
    }

    @Override
    public void onAdditionalInfoClicked(int fragmentType, DeviceInfoViewModel device) {
        DeviceInfoDialog infoDialog = new DeviceInfoDialog();
        infoDialog.setViewModel(device);
        infoDialog.show(getSupportFragmentManager(), "device_info");
    }




    /**
     * Inherited from ConnectAttemptFailedDialog.ConnectionFailedDialogCallback - methods,
     * defining what to do if connection attempt failed
     */

    /**
     * User choose to get over with unsuccessful connection and decided to do something else
     * (perhaps choose another device (or remove app)).
     */
    @Override
    public void acceptFailure() {
        Toast.makeText(this, "user accepted failure", Toast.LENGTH_SHORT).show();
        presenter.onUserRefusedToPickDevice();
    }

    /**
     * user decided to try again to connect to that device.
     * Tell Presenter to connect and clear device reference.
     */
    @Override
    public void retryToConnect() {
        Toast.makeText(this, "retry to connect to device: " + notConnectedDevice.getDeviceName(),
                Toast.LENGTH_SHORT).show();
        presenter.onDeviceSelected(notConnectedDevice);
        notConnectedDevice = null;
    }

    /**
     * Adapter for tab layout
     */
    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
