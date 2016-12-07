package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.device_list_fragments;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import alex_shutov.com.ledlights.bluetoothmodule.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.ChooseDeviceActivity;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;

/**
 * Created by lodoss on 06/12/16.
 */

/**
 * In this fragment we'll show all devices from application history.
 * Every line from list has to have two state icons - if this device is from history and
 * if it is paired.
 * We can query list of paired devices from Presenter and do it quit fast.
 * After that, we create map of devices my their addresses and look for intersection of
 * history devices and paired devices.
 * I don't move this functionality into bas class, because paired devices will have 'base set'
 * from paired devices and this class from history devices.
 */
public class HistoryDevicesFragment extends HistoryPairedFragment {

    public static final String LOG_TAG = HistoryDevicesFragment.class.getSimpleName();

    public static DevicesFragment newInstance() {
        Bundle args = new Bundle();
        args.putInt(ARG_FRAGMENT_TYPE, ChooseDeviceActivity.FRAGMENT_HISTORY);
        HistoryDevicesFragment instance = new HistoryDevicesFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    protected int getEmptyTextResource() {
        return R.string.device_list_history_empty;
    }

    @Override
    protected List<DeviceInfoViewModel> mapHistoryAndPairedLists(List<BtDevice> history,
                                                               List<BtDevice> paired) {
        List<DeviceInfoViewModel> historyViewModel = new ArrayList<>();
        Map<String, DeviceInfoViewModel> historyMapping = new TreeMap<>();
        for (BtDevice device : history) {
            DeviceInfoViewModel vm = convertToViewModel(device);
            // This is a device from history
            vm.setDeviceFromHistory(true);
            historyViewModel.add(vm);
            historyMapping.put(vm.getDeviceAddress(), vm);
        }
        // now check which paired devices is in device history and mark those devices as paired
        for (BtDevice device : paired) {
            String address = device.getDeviceAddress();
            if (!historyMapping.containsKey(address)) continue;
            // We're sure this device is paired to this phone
            historyMapping.get(address).setPairedDevice(true);
        }
        return historyViewModel;
    }
}
