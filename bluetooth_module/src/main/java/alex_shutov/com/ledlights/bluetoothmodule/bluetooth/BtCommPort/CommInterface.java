package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.transfer_data.TransferManager;
import rx.Observable;

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
public interface CommInterface extends TransferManager {
    /**
     * Initiate connection with whatever device Bluetooth cell has connection with, or
     * dummy device, if Bluetooth is turned off or there is no available devices from
     * connection history.
     */
    void startConnection();

    /**
     * Check if phone have had any connected device before. Depending on that result UI will decide
     * what action to use. If there is no connection history, application should alter UI in a way,
     * allowing user to start selecting another device (from list of paired device, or discover
     * anew). In case if LogicCell cannot connect to known device it will use mock algorithm
     * for data transfer anyways.
     */
    Observable<Boolean> hasDeviceHistory();

    /**
     * User should be able to select another Bluetooth device by using UI
     */
    void selectAnotherDevice();

    /**
     *  Disconnect from connected Bluetooth device
     */
    void disconnect();

    /**
     * Check if Bluetooth is now connected.
     * There is method 'isConnected()' from hexagonal framework. It indicates if current port
     * is connected. Don't confuse it with checking Bluetooth device readiness. This is done
     * by this method.
     * @return
     */
    boolean isDeviceConnected();

}
