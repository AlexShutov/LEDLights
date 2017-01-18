package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.transfer_data;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManagerDataProvider;

import static alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnEsbStore.*;
import static alex_shutov.com.ledlights.hex_general.common.utils.impl.LogUtils.LOGI;

/**
 * Created by lodoss on 19/12/16.
 */

public class TransferManagerImpl extends TransferManagerBase {
    private static final String LOG_TAG = TransferManagerImpl.class.getSimpleName();

    private EventBus eventBus;
    private BtConnPort connPort;
    /**
     * Inherited from BtAlgorithm
     */

    @Override
    protected void getDependenciesFromFacade(DataProvider dataProvider) {
        eventBus = dataProvider.provideEventBus();
        ConnectionManagerDataProvider provider = (ConnectionManagerDataProvider) dataProvider;
        connPort = provider.provideBtConnPort();
    }

    @Override
    protected void start() {
        LOGI(LOG_TAG, "Selecting real TransferManager");
        eventBus.register(this);
    }

    @Override
    public void suspend() {
        LOGI(LOG_TAG, "Putting aside real TransferManager");
        eventBus.unregister(this);
    }

    /**
     * Inherited from TransferManager
     */

    @Override
    public void sendData(byte[] data) {
        connPort.writeBytes(data);
    }

    @Subscribe
    public void onDataSent(ArgumentMessageSentEvent event) {
        // inform listener that data package sent successfully
        TransferManagerFeedback feedback = getFeedback();
        feedback.onDataSent();
    }

    @Subscribe
    public void onMessageReceived(ArgumentMessageReadEvent readEvent) {
        TransferManagerFeedback feedback = getFeedback();
        feedback.receiveData(readEvent.message, readEvent.messageSize);
    }
}
