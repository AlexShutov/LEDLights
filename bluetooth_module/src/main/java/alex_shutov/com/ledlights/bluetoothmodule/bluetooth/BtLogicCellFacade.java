package alex_shutov.com.ledlights.bluetoothmodule.bluetooth;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.CommInterface;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.hex.BtStoragePort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionDataProvider;

/**
 * Created by Alex on 11/6/2016.
 */
public class BtLogicCellFacade implements CommInterface, EstablishConnectionDataProvider {
    private static final String LOG_TAG = BtLogicCellFacade.class.getSimpleName();
    /**
     * DI component, responsible for creating all objects
     */
    private BtPortAdapterCreator diComponent;

    @Inject
    public EventBus eventBus;
    @Inject
    public BtConnPort connPort;
    @Inject
    public BtScanPort scanPort;
    @Inject
    public BtStoragePort storagePort;

    private BtCommPortListener commFeedback;

    
    public BtLogicCellFacade(BtPortAdapterCreator diComponent){
        this.diComponent = diComponent;
    }

    /**
     * Inherited from EstablishConnectionDataProvider
     */

    @Override
    public BtConnPort provideBtConnPort() {
        return connPort;
    }

    @Override
    public BtScanPort provideBtScanPort() {
        return scanPort;
    }

    @Override
    public EventBus provideEventBus() {
        return eventBus;
    }

    @Override
    public BtDeviceDao provideHistoryDatabase() {
        BtDeviceDao historyDatabase = storagePort.getHistoryDatabase();
        return historyDatabase;
    }

    /**
     * Request all objects after everything is ready (BtLogicCell is initialized)
     */
    public void onInitialized(){
        Log.i(LOG_TAG, "onInitialized()");
        diComponent.injectBtLogicCellFacade(this);
    }

    public void onDestroying(){
        Log.i(LOG_TAG, "onDestroying()");
    }

    /**
     * Inherited from CommInterface
     */

    @Override
    public void startConnection() {
        Log.i(LOG_TAG, "startConnection()");
    }

    @Override
    public void disconnect() {
        Log.i(LOG_TAG, "disconnect()");
    }

    @Override
    public boolean isDeviceConnected() {
        return false;
    }

    @Override
    public void sendData(byte[] data) {

    }


    public void setCommFeedback(BtCommPortListener commFeedback) {
        this.commFeedback = commFeedback;
    }
}
