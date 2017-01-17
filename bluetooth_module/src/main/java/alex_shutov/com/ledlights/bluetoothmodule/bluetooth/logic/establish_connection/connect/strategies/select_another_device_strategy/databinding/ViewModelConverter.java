package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.databinding;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;

/**
 * Created by lodoss on 09/12/16.
 */

public class ViewModelConverter {

    /**
     * Convert application model into view model (is used in different fragments. All additional
     * flags is set to false
     * @param d
     * @return
     */
    public static DeviceInfoViewModel convertToViewModel(BtDevice d) {
        DeviceInfoViewModel vm = new DeviceInfoViewModel();
        vm.setDeviceName(d.getDeviceName());
        vm.setDeviceAddress(d.getDeviceAddress());
        vm.setDeviceDescription(d.getDeviceDescription());
        // additional fields set tu false by default
        vm.setPairedDevice(false);
        vm.setDeviceFromHistory(false);
        vm.setShowDescription(false);
        return vm;
    }

    /**
     * Convert back from ViewModel (Ui use view model, presenter app model)
     * @param viewModel
     * @return
     */
    public static BtDevice fromViewModel(DeviceInfoViewModel viewModel) {
        BtDevice device = new BtDevice();
        device.setDeviceName(viewModel.getDeviceName());
        device.setDeviceAddress(viewModel.getDeviceAddress());
        device.setPaired(viewModel.isPairedDevice());
        return device;
    }

}
