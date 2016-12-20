package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.transfer_data;

import android.util.Log;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;

/**
 * Created by lodoss on 19/12/16.
 */

public class TransferManagerMock extends TransferManagerBase {

    private static final String LOG_TAG = TransferManagerMock.class.getSimpleName();

    /**
     * Inherited from BtAlgorithm
     */

    /**
     * mock implementation need nothing
     * @param dataProvider
     */
    @Override
    protected void getDependenciesFromFacade(DataProvider dataProvider) {
    }

    @Override
    protected void start() {
        Log.i(LOG_TAG, "Mock transfer manager is selected");
    }

    @Override
    public void suspend() {

    }

    /**
     * Inherited from TransferManager
     */

    @Override
    public void sendData(byte[] data) {
        Log.w(LOG_TAG, "Attempting to send data via mock transfer manager");
        // consider data being sent
        getFeedback().onDataSent();
    }
}
