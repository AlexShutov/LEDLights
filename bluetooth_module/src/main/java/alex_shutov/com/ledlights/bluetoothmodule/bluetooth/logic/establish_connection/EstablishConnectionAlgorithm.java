package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.BtAlgorithm;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.EstablishConnectionStrategy;

/**
 * Created by Alex on 10/26/2016.
 */

/**
 *  Algorithm, responsible for establishing connection with Bluetooth device when
 *  external port demands it, or when connection was lost by some reason (perhaps, lost of power
 *  on the other end).
 */
public class EstablishConnectionAlgorithm extends BtAlgorithm implements
        EstablishConnection {
    private static final String LOG_TAG = EstablishConnectionAlgorithm.class.getSimpleName();
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

    /**
     * Strategies for establishing connection
     */
    private EstablishConnectionStrategy reconnect;


    public EstablishConnectionAlgorithm(EstablishConnectionStrategy reconnectStrategy) {
        this.reconnect = reconnectStrategy;
    }

    @Override
    public void suspend() {
        reconnect.suspend();
    }

    /**
     * Use DataProvider we saved to initialize strategies and wire up
     * strategies
     */
    @Override
    protected void start() {
        reconnect.init(dataProvider);
        reconnect.setCallback(new EstablishConnectionCallback() {
            @Override
            public void onConnectionEstablished(BtDevice connectedDevice) {
                Log.i(LOG_TAG, "device reconnected(): " + connectedDevice.getDeviceName());
                EstablishConnectionAlgorithm.this.connectedDevice = connectedDevice;
                if (null != callback){
                    callback.onConnectionEstablished(connectedDevice);
                }
            }

            @Override
            public void onAttemptFailed() {
                Log.w(LOG_TAG, "onAttemptFailed()");
                connectedDevice = null;
                if (null != callback){
                    callback.onAttemptFailed();
                }
            }
        });
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

    @Override
    public boolean isAttemptingToConnect() {
        return false;
    }

    @Override
    public void stopConnecting() {

    }

    /**
     * Inherited from EstablishConnection
     */



    @Override
    public void attemptToEstablishConnection() {
        testWriteLastDevice();
        reconnect.attemptToEstablishConnection();
    }

    public void setCallback(EstablishConnectionCallback callback) {
        this.callback = callback;
    }

    public BtDevice getConnectedDevice() {
        return connectedDevice;
    }


    private void testWriteLastDevice(){
        // remove any info regarding last connected device
        deviceDatabase.clearLastConnectedDeviceInfo();
        BtDevice hc05 = new BtDevice();
        hc05.setDeviceName("My bike");
        String address = "98:D3:31:20:A0:07";
        String uuid = "00001101-0000-1000-8000-00805F9B34FB";
        hc05.setDeviceAddress(address);
        hc05.setDeviceUuIdSecure(uuid);
        hc05.setDeviceUuIdInsecure(uuid);
        hc05.setPaired(true);
        hc05.setSecureOperation(true);
        deviceDatabase.setLastConnectedMotorcycleInfo(hc05);
        // 1 hour ago
        Long startConnectionTime = System.currentTimeMillis() - 60 * 60 * 1000;
        // as though it disconnected 10 seconds ago
        Long endConnectionTime = System.currentTimeMillis() - 10 * 1000;
        deviceDatabase.setLastConnectionStartTime(startConnectionTime);
        deviceDatabase.setLastConnectionEndTime(endConnectionTime);
    }
}
