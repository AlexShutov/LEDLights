package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.CommInterface;
import alex_shutov.com.ledlights.hex_general.Port;

/**
 * Created by lodoss on 12/10/16.
 */

/**
 * Wraps CommInterface for to be used in hexagonal surrounding
 */
public interface BtCommPort extends Port, CommInterface {

}
