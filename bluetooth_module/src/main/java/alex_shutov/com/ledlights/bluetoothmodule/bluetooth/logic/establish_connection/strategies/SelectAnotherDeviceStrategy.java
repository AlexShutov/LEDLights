package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;

/**
 * Created by Alex on 11/7/2016.
 */
public class SelectAnotherDeviceStrategy extends EstablishConnectionStrategy {
    private static final String LOG_TAG = SelectAnotherDeviceStrategy.class.getSimpleName();

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
        // TODO: read UI port from data provider
    }

    @Override
    protected void doOnConnectionSuccessful(BtDevice device) {

    }

    @Override
    protected void doOnConnectionAttemptFailed() {

    }

    @Override
    public void attemptToEstablishConnection() {

    }

    @Override
    public void stopConnecting() {

    }

    @Override
    public void selectDeviceByUi() {

    }



}
