package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp;

import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.hex_general.BaseModel;
import rx.Observable;

/**
 * Created by lodoss on 01/12/16.
 */

public interface AnotherDeviceModel extends BaseModel {

    /**
     * Query all devices from database app had connection with before
     * @return
     */
    Observable<List<BtDevice>> getDevicesFromConnectionHistory();

    Observable<List<BtDevice>> getPairedSystemDevices();

    Observable<BtDevice> discoverDevices();

    void stopDiscovery();

    /**
     * Attempt connecting to given device.
     * @param device
     */
    void connectToDevice(BtDevice device);

    /**
     * This method is called by Presenter if use refused to select another device, or,
     * if user selected some devices but all connection attempts have failed and user gave
     * up eventually
     */
    void onFailedToSelectAnotherDevice();

}
