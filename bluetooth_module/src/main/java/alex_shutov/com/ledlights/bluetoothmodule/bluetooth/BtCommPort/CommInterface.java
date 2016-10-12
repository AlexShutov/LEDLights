package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;

/**
 * Created by lodoss on 12/10/16.
 */

/**
 * External input port for Bluetooth logic cell.
 * Interface, describing communication between Bluetooth logic cell and the rest of an app.
 * This interface describes basic functionality app need to know, the rest is up to
 * BtLogicCell implementation of LogicCell.
 * Call to each of those methods causes some feedback (see CommFeedbackInterface interface).
 * Another approach - use Observable pattern instead of two separate interfaces, etc. RxJava.
 * But, I can use it in app itself. Another reason not to use it here is because those
 * interfaces is supposed to be mapped to EventBus (ESB). It is simpler to do this way.
 *
 *
 */
public interface CommInterface  {
    /**
     * Initiate connection with whatever device Bluetooth cell has connection with, or
     * dummy device, if Bluetooth is turned off or there is no available devices from
     * connection history.
     */
    void startConnection();

    /**
     * Send some data to connected Bluetooth device. Result of this operation will be
     * delivered to output port either by onDataSent() or onDataSendFailed() methods.
     * @param data
     */
    void sendData(byte[] data);

    /**
     *
     * @return true if there is an active connection, false- otherwise
     */
    boolean hasConnection();

    /**
     * Get information about connected BLuetooth device
     * @return Info of currently connected device or null if there is no active
     * Bluetooth connection at hand.
     */
    BtDevice getDeviceInfo();

}
