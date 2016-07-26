package alex_shutov.com.ledlights.Bluetooth.hex;

import alex_shutov.com.ledlights.Bluetooth.BtDevice;
import alex_shutov.com.ledlights.HexGeneral.PortListener;

/**
 * Created by Alex on 7/25/2016.
 */
public interface BtPortListener extends PortListener {

    /**
     * Is called whenever BT service's state changes - methods below represent parsed state in
     * 'state' argument - see BluetoothChatService's state variables
     * @param state
     */
    void onStateChanged(int state);
    /**
     * Those callback is called when MESSAGE_STATE_CHANGE message is dispatched
     */
    void onStateConnected();
    void onStateConnecting();
    void onStateListening();
    void onStateIdle();

    /**
     * Notifies that bytes were received from connected device
     * @param message   actual message
     * @param messageSize message length
     */
    void onMessageRead(byte[] message, int messageSize);

    /**
     * Inform program that Bluetooth adapter accepted incoming
     * connection from another device
     * @param btDevice
     */
    void onDeviceConnected(BtDevice btDevice);

    /**
     *  Notify program - it should decide what to do next. This is
     *  slightly modified version of 'MESSAGE_TOAST' command.
     */
    void onConnectioinFailed();

    /**
     *  Notify program that connection were lost
     */
    void onConnectionLost();

}
