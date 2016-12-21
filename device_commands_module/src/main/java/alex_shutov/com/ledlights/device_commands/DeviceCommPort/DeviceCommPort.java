package alex_shutov.com.ledlights.device_commands.DeviceCommPort;

import alex_shutov.com.ledlights.hex_general.Port;

/**
 * Created by lodoss on 21/12/16.
 */

/**
 * This logic cell is responsible for converting application commands.
 */
public interface DeviceCommPort extends Port {

    /**
     * Called when data sent to device
     */
    void onDataSent();

    /**
     * Phone received response from device. That response indicate success of operation.
     * @param response
     */
    void onResponse(byte[] response);

}
