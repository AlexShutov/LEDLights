package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy;

import android.util.Log;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionCallback;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionDataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.EstablishConnectionStrategy;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Alex on 11/7/2016.
 */
public class SelectAnotherDeviceStrategy extends EstablishConnectionStrategy
        implements BtUiDeviceSelectionViewPartial {
    private static final String LOG_TAG = SelectAnotherDeviceStrategy.class.getSimpleName();


    private BtScanPort scanPort;
    private BtConnPort connPort;


    public SelectAnotherDeviceStrategy(){
        super();
    }

    @Override
    public void suspend() {
        super.suspend();
    }

    @Override
    protected void start() {
        super.start();
    }

    @Override
    protected void getDependenciesFromFacade(DataProvider dataProvider) {
        super.getDependenciesFromFacade(dataProvider);
        EstablishConnectionDataProvider partsProvider = (EstablishConnectionDataProvider) dataProvider;
        scanPort = partsProvider.provideBtScanPort();
        connPort = partsProvider.provideBtConnPort();
    }

    /**
     * Callback, called whenever app is connected to another Bluetooth device. Connection
     * to device has to be initiated by createPendingConnectTask(BtDevice device) method in
     * base class (EstablishConnectionStrategy)
     * @param device
     */
    @Override
    protected void doOnConnectionSuccessful(BtDevice device) {
        Log.i(LOG_TAG, "Device successfully connected: " + device.getDeviceName());
    }

    @Override
    protected void doOnConnectionAttemptFailed() {
        Log.w(LOG_TAG, "Connection attempt failed");
    }

    /**
     * User tell this strategy to start working on establishing connection by this method.
     *
     */
    @Override
    public void attemptToEstablishConnection() {
        triggerUi();
    }

    /**
     * Strategy is being told to stop connection attempt
     */
    @Override
    public void stopConnecting() {
        cancelOngoingConnectionAttemptsAndDiscovery();
        unregisterFromUiPortAndCloseUi();
    }

    /**
     * Start selectinf device by UI. In case of this strategy it is the same, as
     * attemptToEstablishConnection() method.
     */
    @Override
    public void selectDeviceByUi() {
        triggerUi();
    }


    /**
     * Inherited from BtUiDeviceSelectionViewPartial
     */


    /**
     * User picked a device, but app isn't connected to it yet.
     * Attempt to create connection to that device by using methods from base class.
     * @param device
     */
    @Override
    public void onUserChooseDevice(BtDevice device) {
        Log.i(LOG_TAG, "onUserChooseDevice()" + ( device == null ? "" : device.getDeviceName()));
    }

    /**
     * User refused to select device. suspend all activity, close Ui and notify callback about it
     */
    @Override
    public void onCancelledByUser() {
        Log.i(LOG_TAG, "onCancelledByUser()");
        cancelOngoingConnectionAttemptsAndDiscovery();
        EstablishConnectionCallback callback = getCallback();
        if (null != callback) {
            callback.onAttemptFailed();
        }
    }

    /**
     * Ui request to stop all activities with bluetooth. This method return Observable so Ui
     * will know when it can proceed
     * @return
     */
    @Override
    public Observable<Boolean> stopBluetoothCommunication() {
        return Observable.just("")
                .subscribeOn(Schedulers.io())
                .map(t -> {
                    cancelOngoingConnectionAttemptsAndDiscovery();
                    return true;
                });
    }

    /**
     * Inherited from PortListener (ui port listener)
     */

    @Override
    public void onPortReady(int portID) {
        Log.i(LOG_TAG, "onPortReady(" + portID + ")");

    }

    @Override
    public void onCriticalFailure(int portID, Exception e) {
        Log.i(LOG_TAG, "onCriticalFailure( portID: " + portID + ", error: " + e.getMessage());
    }

    /**
     * Private logic methods
     */


    private void cancelOngoingConnectionAttemptsAndDiscovery() {
        Log.i(LOG_TAG, "Cancelling all Bluetooth activities before attempting to select another " +
                "device");
        scanPort.stopDiscovery();
        connPort.stopConnecting();
        connPort.close();
    }

    /**
     * Discard all changes made in device picking UI and unbind this strategy from
     * ui adapter (it is set as listener now).
     */
    private void unregisterFromUiPortAndCloseUi(){

    }


    /**
     * Strategy tell UI port to select UI first, and, when UI is shown, it will tell port to
     * clear all previous UI data. This might be the case, because strategy can be triggered
     * from backgound (not from UI).
     * We have to set this strategy as port listener first and then tell port to display UI.
     */
    private void triggerUi() {


    }

}



