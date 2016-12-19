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

    void receiveData(byte[] data);

}
