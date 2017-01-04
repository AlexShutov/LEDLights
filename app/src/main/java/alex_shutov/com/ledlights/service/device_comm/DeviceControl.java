package alex_shutov.com.ledlights.service.device_comm;

/**
 * Created by lodoss on 04/01/17.
 */

public interface DeviceControl {

    /**
     * Part of interface for controlling device connection
     */
    void connectToDevice();
    void selectAnotherDevice();
    boolean isDeviceConnected();
    void disconnect();
}
