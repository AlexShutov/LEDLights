package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.device_list_fragments;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import alex_shutov.com.ledlights.bluetoothmodule.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.ChooseDeviceActivity;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.databinding.ViewModelConverter;

/**
 * Created by lodoss on 06/12/16.
 */

/**
 * Get list of paired devices and turn on 'history' icon if that device is form application history.
 */
public class PairedDevicesFragment extends HistoryPairedFragment {
    private static final String LOG_TAG = PairedDevicesFragment.class.getSimpleName();
    
    public static DevicesFragment newInstance() {
        Bundle args = new Bundle();
        args.putInt(ARG_FRAGMENT_TYPE, ChooseDeviceActivity.FRAGMENT_PAIRED);
        PairedDevicesFragment instance = new PairedDevicesFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    protected int getEmptyTextResource() {
        return R.string.device_list_paired_empty;
    }

    @Override
    protected List<DeviceInfoViewModel> mapHistoryAndPairedLists(List<BtDevice> history,
                                                                 List<BtDevice> paired) {
        List<DeviceInfoViewModel> pairedViewModel = new ArrayList<>();
        Map<String, DeviceInfoViewModel> pairedMapping = new TreeMap<>();
        for (BtDevice device : paired) {
            DeviceInfoViewModel vm = ViewModelConverter.convertToViewModel(device);
            // This is a device from history
            vm.setPairedDevice(true);
            pairedViewModel.add(vm);
            pairedMapping.put(vm.getDeviceAddress(), vm);
        }
        // now check which paired devices is in device history and mark those devices as paired
        for (BtDevice device : history) {
            String address = device.getDeviceAddress();
            if (!pairedMapping.containsKey(address)) continue;
            // We're sure this device is paired to this phone
            pairedMapping.get(address).setDeviceFromHistory(true);
        }
        return pairedViewModel;
    }
}
