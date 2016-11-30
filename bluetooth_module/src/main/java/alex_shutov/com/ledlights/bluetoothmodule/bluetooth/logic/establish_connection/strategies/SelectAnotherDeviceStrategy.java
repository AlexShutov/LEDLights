package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies;

import android.util.Log;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtUiPort.BtUiPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionDataProvider;

/**
 * Created by Alex on 11/7/2016.
 */
public class SelectAnotherDeviceStrategy extends EstablishConnectionStrategy {
    private static final String LOG_TAG = SelectAnotherDeviceStrategy.class.getSimpleName();

    private BtUiPort uiPort;

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
        EstablishConnectionDataProvider provider = (EstablishConnectionDataProvider) dataProvider;
        uiPort = provider.provideBtUiPort();
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
        startDeviceSelectionProcessByUsingUI();
    }

    /**
     * Strategu is being told to stop connection attempt
     */
    @Override
    public void stopConnecting() {

    }

    /**
     * Start selectinf device by UI. In case of this strategy it is the same, as
     * attemptToEstablishConnection() method.
     */
    @Override
    public void selectDeviceByUi() {
        startDeviceSelectionProcessByUsingUI();
    }

    /**
     * Entry point to start device selection
     */
    private void startDeviceSelectionProcessByUsingUI() {

    }

}
