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

}
