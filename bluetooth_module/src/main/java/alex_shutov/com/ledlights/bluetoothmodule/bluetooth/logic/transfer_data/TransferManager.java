package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.transfer_data;

/**
 * Created by lodoss on 19/12/16.
 */

public interface TransferManager {
    /**
     * Send some data to connected Bluetooth device. Result of this operation will be
     * delivered to output port either by onDataSent() or onDataSendFailed() methods.
     * @param data
     */
    void sendData(byte[] data);

}
