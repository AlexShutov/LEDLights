package alex_shutov.com.ledlights.app_facade;

import alex_shutov.com.ledlights.device_commands.ControlPort.EmulationCallback;
import alex_shutov.com.ledlights.device_commands.main_logic.Command;

/**
 * Created by lodoss on 27/12/16.
 */

public interface AppFacade {

    /**
     * Device controls section
     */

    /**
     * App facade will try to connect to actual device or dummy device depending on
     * which is selected
     */
    void connectToDevice();

    void selectAnotherDevice();

    /**
     * Disconnect from device
     */
    void disconnectFromDevice();

    /**
     * ask module, responsible for communication with device if device is now connected
     * @return
     */
    boolean isDeviceConnected();

    void setDeviceListener(AppFacadeDeviceListener listener);

    /**
     * Command serialization section
     * @param command
     */

    void sendCommand(Command command);

    /**
     * Start emulating device. At this point callback has to be set
     */
    void enableEmulation();

    /**
     * Stop emulating Bluetooth device
     */
    void disableEmulation();

    /**
     * Set UI for emulated device
     * @param emulatedDevice
     */
    void setEmulationCallback(EmulationCallback emulatedDevice);





}
