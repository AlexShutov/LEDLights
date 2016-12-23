package alex_shutov.com.ledlights.device_commands.ControlPort;

/**
 * Created by lodoss on 23/12/16.
 */

/**
 * Device command module use this interface to inform app about changes in state of emulated device.
 * This is an interface, used by outside app, cell use EmulatedDeviceControl interface.
 * This adapter is a glue logic, connecting two interfaces.
 */
public interface EmulationCallback {

    /**
     * Color of emulated device have changed
     * @param color
     */
    void onLEDColorChanged(int color);

    /**
     * Strobe turned ON
     */
    void onStrobeOn();

    /**
     * Strobe turned Off
     */
    void onStrobeOff();

}
