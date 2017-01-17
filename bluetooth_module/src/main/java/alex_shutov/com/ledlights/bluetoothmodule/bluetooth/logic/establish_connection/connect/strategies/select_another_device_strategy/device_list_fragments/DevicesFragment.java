package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.device_list_fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.ListOfDevicesAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.databinding.DevicePickerViewModel;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.events.PresenterInstanceEvent;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.mvp.AnotherDevicePresenter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.mvp.AnotherDeviceView;
import alex_shutov.com.ledlights.bluetoothmodule.databinding.DeviceListBinding;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by lodoss on 02/12/16.
 */

public abstract class DevicesFragment extends Fragment implements AnotherDeviceView {
    /**
     * Containing Activity implement this interface so Fragment can tell it if user did
     * something: selected a device or want to see additional info
     */
    public interface UserActionListener {
        void onDevicePicked(int fragmentType,  DeviceInfoViewModel device);
        void onAdditionalInfoClicked(int fragmentType, DeviceInfoViewModel device);
    }

    private static final String LOG_TAG = DevicesFragment.class.getSimpleName();
    protected static final String ARG_FRAGMENT_TYPE = "FRAGMENT_TYPE";

    private int fragmentType;
    private EventBus eventBus;

    private AnotherDevicePresenter presenter;
    // Adapter for RecyclerView with list of devices
    private ListOfDevicesAdapter listAdapter;
    // ViewModel for showing hiding 'empty' text
    DevicePickerViewModel viewModel;
    /**
     * All device lists has the same layout, only logic differ
     */
    private DeviceListBinding viewBinding;

    /**
     * Those two method initiate loading list of devices and stop it too.
     */
    protected abstract void updateDeviceList();
    protected abstract void init();
    protected abstract void suspend();

    /**
     * String id for 'empty' message
     * @return
     */
    protected abstract int getEmptyTextResource();

    // use static fabric method only
    protected DevicesFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        fragmentType = args.getInt(ARG_FRAGMENT_TYPE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.device_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.bind(view);
        viewModel = new DevicePickerViewModel();
        viewBinding.setModel(viewModel);
        viewModel.setEmptyText(getEmptyTextResource());
        // hide 'empty' message by default
        viewModel.setEmpty(false);
    }

    /**
     * Show text, which is displayed when list is empty. It is used by 'scan' fragment for
     * prompting user to swipe to refresh
     * @param textResourceId
     */
    public void showEmptyText(int textResourceId) {
        viewModel.setEmpty(true);
        viewModel.setEmptyText(textResourceId);
    }

    /**
     * Hide text for empty state
     */
    public void hideEmptyText(){
        viewModel.setEmpty(false);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(PresenterInstanceEvent instanceEvent) {
        presenter = instanceEvent.getPresenter();
        presenter.attachView(this);
        setupSwipeRefresh();
        setupListOfDevices();
        Observable.just("")
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> init());
    }

    /**
     * Disconnect from Presenter
     */
    @Override
    public void onPause() {
        eventBus.unregister(this);
        suspend();
        presenter.detachView();
        // cancel refresh animation
        if (viewBinding.dlRefreshLayout.isRefreshing()) {
            Log.i(LOG_TAG, "content is still refreshing, cancelling");
            viewBinding.dlRefreshLayout.setRefreshing(false);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        onUpdateComplete();
    }

    /**
     * New values is loaded, cancel refresh animation
     */
    protected void onUpdateComplete() {
        viewBinding.dlRefreshLayout.setRefreshing(false);
    }

    /**
     * Display final list of devices
     * @param devices
     */
    protected void showDeviceList(List<DeviceInfoViewModel> devices) {
        getListAdapter().setDevices(devices);
    }

    /**
     * This method is used during discovery of Bluetooth devices
     * @param device
     */
    protected void addDeviceToTheList(DeviceInfoViewModel device) {
        getListAdapter().addDevice(device);
    }

    /**
     * Each device list representation need listener for user actions - 'show more info' and
     * selection action. Click listeners is stored in view model
     * @param vms
     */
    protected void addUserActionListenersToList(List<DeviceInfoViewModel> vms) {
        for (DeviceInfoViewModel vm : vms) {
            addUserActionListeners(vm);
        }
    }

    protected void addUserActionListeners(DeviceInfoViewModel viewModel) {
        viewModel.setShowDeviceDetailsListener(v -> {
            UserActionListener l = (UserActionListener) getActivity();
            l.onAdditionalInfoClicked(getFragmentType(), viewModel);
        });
        viewModel.setDevicePickedListener((v) -> {
            UserActionListener l = (UserActionListener) getActivity();
            l.onDevicePicked(getFragmentType(), viewModel);
        });
    }


    /**
     * When user swipe view down, show refresh animation and ask derived class to update data.
     */
    private void setupSwipeRefresh() {
        viewBinding.dlRefreshLayout.setOnRefreshListener(() -> {
                viewBinding.dlRefreshLayout.setRefreshing(true);
                updateDeviceList();
        });
    }

    /**
     * Setup RecyclerView, containing list of Bluetooth devices, specific to concrete Fragment.
     */
    private void setupListOfDevices() {
        RecyclerView list = viewBinding.dlList;
        Context context = getActivity();
        list.setLayoutManager(new LinearLayoutManager(context));
        listAdapter = new ListOfDevicesAdapter(context);
        list.setAdapter(listAdapter);
    }

    /**
     * Accessors to be used in derived class
     */

    protected AnotherDevicePresenter getPresenter() {
        return presenter;
    }

    protected ListOfDevicesAdapter getListAdapter() {
        return listAdapter;
    }

    public int getFragmentType() {
        return fragmentType;
    }

    public DevicePickerViewModel getViewModel() {
        return viewModel;
    }
}
