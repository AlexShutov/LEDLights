package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp;

import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.hex_general.BaseView;

/**
 * Created by lodoss on 01/12/16.
 */

public interface AnotherDeviceView extends BaseView {

    /**
     * Show list of devices from app history.
     * Those devices will be show in currently active tab. If user choose another tab, View will
     * request the same data again.
     * Presenter cna query app data every time, because db is fast, but, it should no do the same
     * for devices, scanned on system level
     * @param devices
     */
    void displayDevicesFromAppHistory(List<BtDevice> devices);

    /**
     * Show devices, this phone currently paired with.
     * @param devices
     */
    void displayPairedSystemDevices(List<BtDevice> devices);


}
