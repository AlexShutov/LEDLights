package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.transfer_data;

/**
 * Created by lodoss on 19/12/16.
 */

public interface TransferManagerFeedback {

    /**
     * Tell app that data sent - result of .sendData() method
     */
    void onDataSent();

    /**
     * Tell app that data sending failed - result of .sendData() method.
     */
    void onDataSendFailed();

    /**
     * Process received data chunk
     * @param data array, containing received data chunk. Can be buffer larger than
     *             actual data
     * @param size size of data payload
     */
    void receiveData(byte[] data, int size);

}
