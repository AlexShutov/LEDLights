package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic;

import org.greenrobot.eventbus.EventBus;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;

/**
 * Created by Alex on 10/27/2016.
 */

/**
 * There is two way for passing dependencies into atomic algorithms -
 * either use DI or custom interface.
 * Second way is more appropriate, because if we do so, it will be much simpler to pass
 * mocked objects during unit testing.
 */
public interface DataProvider {
    /**
     * Almost all atomic algorithms use ESB in some way, so they need EventBus.
     * @return
     */
    EventBus provideEventBus();

    /**
     * Connection algorithm need database, storing history connection for attempting to
     * connect to the last connected device.
     * Every strategy use history database in some way, so this method is in base interface.
     * @return
     */
    BtDeviceDao provideHistoryDatabase();
}
