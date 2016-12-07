package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.device_list_fragments.DevicesFragment;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.device_list_fragments.HistoryDevicesFragment;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.device_list_fragments.PairedDevicesFragment;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.events.PresenterInstanceEvent;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp.AnotherDevicePresenter;
import alex_shutov.com.ledlights.bluetoothmodule.databinding.ActivityPickDeviceBinding;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by lodoss on 09/11/16.
 */
public class ChooseDeviceActivity extends AppCompatActivity implements DevicesFragment.UserActionListener {

    private static final String LOG_TAG = ChooseDeviceActivity.class.getSimpleName();

    private EventBus eventBus;
    private AnotherDevicePresenter presenter;

    public static final int FRAGMENT_HISTORY = 0;
    public static final int FRAGMENT_PAIRED = 1;

    /** Binding for this Activity. It is used for setting up TabLayout and ViewPager */
    private ActivityPickDeviceBinding activityBinding;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    /**
     * Activity receive presenter instance by sticky event. It MUST be already posted before
     * this activity is being started, because once it is, .onResume() method will register
     * Activity as a view in that presenter. Here we don't attach this Activity to Presenter,
     * because all work is done in Fragments, implementing AnotherDeviceView
     * @param instanceEvent
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(PresenterInstanceEvent instanceEvent) {
        presenter = instanceEvent.getPresenter();
        init();
    }

    /**
     * Init all fragments once we have presenter reference
     */
    private void init(){
        if (activityBinding.apdVpDevices != null) {
            setupViewPager();
            activityBinding.apdVpDevices.setCurrentItem(FRAGMENT_HISTORY);
        }
        activityBinding.apdTlViewModeSelector.setupWithViewPager(activityBinding.apdVpDevices);
    }

    /**
     * Inherited from AnothedDeviceView
     */

//    @Override
//    public void displayDevicesFromAppHistory(List<BtDevice> devices) {
//        Log.i(LOG_TAG, "App remember " + devices.size() + " devices");
//    }
//
//    @Override
//    public void displayPairedSystemDevices(List<BtDevice> devices) {
//        Log.i(LOG_TAG, "Phone is paired to " + devices.size() + " devices");
//        for (BtDevice device : devices){
//            String msg = "Paired device: " + device.getDeviceName() + " " +
//                    device.getDeviceAddress();
//            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//        }
//        BtDevice device;
//    }
//
//
//    @Override
//    public void onNewDeviceDiscovered(BtDevice device) {
//        String msg = "Device found: " + device.getDeviceName() + " : " +
//                device.getDeviceAddress();
//        Toast.makeText(this, msg , Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onDiscoveryComplete() {
//        Toast.makeText(this, "Bluetooth discovery complete" , Toast.LENGTH_SHORT).show();
//    }


    private void setupViewPager(){
        Adapter adapter = new Adapter(getSupportFragmentManager());
        // init all fragments here
        // fragment for paired devices
        DevicesFragment pairedDevicesFragment = PairedDevicesFragment.newInstance();
        adapter.addFragment(pairedDevicesFragment, getString(R.string.device_list_paired));
        // fragment for device history
        DevicesFragment historyDevicesFragment = HistoryDevicesFragment.newInstance();
        adapter.addFragment(historyDevicesFragment, getString(R.string.device_list_history));

        activityBinding.apdVpDevices.setAdapter(adapter);
    }

    /**
     * Inherited from DevicesFragment.UserActionListener -
     */

    @Override
    public void onDevicePicked(int fragmentType, DeviceInfoViewModel device) {
        Toast.makeText(this, "Device picked: " + device.getDeviceName(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdditionalInfoClicked(int fragmentType, DeviceInfoViewModel device) {
        Toast.makeText(this, "Show additional info for device: " + device.getDeviceName(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Inherited from DevicesFragment.UserActionListener
     */



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
