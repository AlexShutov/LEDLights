package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection;

import org.greenrobot.eventbus.EventBus;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.BtAlgorithm;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;

/**
 * Created by Alex on 10/26/2016.
 */

/**
 *  Algorithm, responsible for establishing connection with Bluetooth device when
 *  external port demands it, or when connection was lost by some reason (perhaps, lost of power
 *  on the other end).
 */
public class EstablishConnectionAlgorithm extends BtAlgorithm {
    EstablishConnectionDataProvider dataProvider;
    /**
     * Access object for database, storing device connection history.
     */
    BtDeviceDao deviceDatabase;
    EventBus eventBus;

    @Override
    public void suspend() {

    }

    @Override
    protected void start() {

    }

    @Override
    protected void getDependenciesFromFacade(DataProvider dp) {
        dataProvider = (EstablishConnectionDataProvider) dp;
        // get connection history database
        deviceDatabase = dataProvider.provideHistoryDatabase();
        eventBus = dataProvider.provideEventBus();

    }
}
