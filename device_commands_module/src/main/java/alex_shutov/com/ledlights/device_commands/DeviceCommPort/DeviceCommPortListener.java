package alex_shutov.com.ledlights.device_commands.DeviceCommPort;

import alex_shutov.com.ledlights.hex_general.PortListener;

/**
 * Created by lodoss on 21/12/16.
 */

public interface DeviceCommPortListener extends PortListener {

    /**
     * Send byte array to device. Different commands has different command size, but this method
     * doesn't have argument for data length, so we'll create new array for every command, which
     * will be garbage collected later. Command has block up to 100bytes, so it is a reasonable
     * solution - keep it simple.
     * @param data
     */
    void sendData(byte[] data);

}
