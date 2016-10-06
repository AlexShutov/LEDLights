package alex_shutov.com.ledlights.bluetoothmodule.bluetooth;

import android.content.Context;
import android.util.Log;

import java.util.Set;

import javax.inject.Inject;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPortListener;
import alex_shutov.com.ledlights.hex_general.LogicCell;

/**
 * Created by lodoss on 24/08/16.
 */

/**
 * Logic cell, connecting all ports from Bluetooth module.
 * For two- way communication with ports it implement listener interfaces from
 * every port :BtScanPortListener,  BtConnPortListener,
 */
public class BtLogicCell extends LogicCell implements
        BtScanPortListener, BtConnPortListener {
    private static final String LOG_TAG = LogicCell.class.getSimpleName();
    /** references to ports connectoed to this LogicCell */
    private BtScanPort btScanPort;
    private BtConnPort btConnPort;

    @Inject
    public Context context;

    /**
     *  Initialize all internal dependencies here
     *  do nothing for now, all BT pairing, data transfer algorithims
     *  has to be initialized here.
     */
    @Override
    public void init() {
        Log.i(LOG_TAG, "BtLogicCell.init()");
    }

    @Override
    protected void injectThisCell() {
        BtPortAdapterCreator creator = (BtPortAdapterCreator) getAdaperCreator();
        creator.injectBtLogicCell(this);
    }

    /**
     *  Accessors
     */
    public BtConnPort getBtConnPort() {
        return btConnPort;
    }

    public void setBtConnPort(BtConnPort btConnPort) {
        this.btConnPort = btConnPort;
    }

    public BtScanPort getBtScanPort() {
        return btScanPort;
    }

    public void setBtScanPort(BtScanPort btScanPort) {
        this.btScanPort = btScanPort;
    }

    public Context getContext() {
        return context;
    }

    /**
     *      Inherited from BtScanPortListener
     */

    @Override
    public void onDeviceFound(BtDevice device) {

    }

    @Override
    public void onPairedDevicesReceived(Set<BtDevice> devices) {

    }

    @Override
    public void onScanCompleted() {

    }

    @Override
    public void onCriticalFailure(int portID, Exception e) {

    }

    @Override
    public void onPortReady(int portID) {

    }

    /**
     *      Inherited from BtConnPortListener
     */

    @Override
    public void onConnectioinFailed() {

    }

    @Override
    public void onStateChanged(int state) {

    }

    @Override
    public void onStateConnected() {

    }

    @Override
    public void onStateConnecting() {

    }

    @Override
    public void onStateListening() {

    }

    @Override
    public void onStateIdle() {

    }

    @Override
    public void onMessageRead(byte[] message, int messageSize) {

    }

    @Override
    public void onMessageSent() {

    }

    @Override
    public void onDeviceConnected(BtDevice btDevice) {

    }

    @Override
    public void onConnectionLost() {

    }


}
