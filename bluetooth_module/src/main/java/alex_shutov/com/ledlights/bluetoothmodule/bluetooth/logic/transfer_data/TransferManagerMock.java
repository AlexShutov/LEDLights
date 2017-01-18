package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.transfer_data;

import android.util.Log;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;

import static alex_shutov.com.ledlights.hex_general.common.utils.impl.LogUtils.LOGI;
import static alex_shutov.com.ledlights.hex_general.common.utils.impl.LogUtils.LOGW;

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
        LOGI(LOG_TAG, "Mock transfer manager is selected");
    }

    @Override
    public void suspend() {

    }

    /**
     * Inherited from TransferManager
     */

    @Override
    public void sendData(byte[] data) {
        LOGW(LOG_TAG, "Attempting to send data via mock transfer manager");
        // consider data being sent
        getFeedback().onDataSent();
    }
}
