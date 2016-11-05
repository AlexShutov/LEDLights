package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection;

import org.greenrobot.eventbus.EventBus;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
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
    private EstablishConnectionDataProvider dataProvider;
    /**
     * Used to notify caller about results of algorithms (success or failure)
     */
    private EstablishConnectionCallback callback;

    /**
     * Currently connected device - result of this algorithm
     */
    private BtDevice connectedDevice;
    /**
     * Access object for database, storing device connection history.
     */
    private BtDeviceDao deviceDatabase;
    private BtConnPort connPort;
    private BtScanPort scanPort;

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
        connPort = dataProvider.provideBtConnPort();
        scanPort = dataProvider.provideBtScanPort();
    }

    public void attemptToEstablishConnection() {

    }



    public void setCallback(EstablishConnectionCallback callback) {
        this.callback = callback;
    }

    public BtDevice getConnectedDevice() {
        return connectedDevice;
    }
}
