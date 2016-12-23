package alex_shutov.com.ledlights.device_commands.main_logic.emulation_general;

/**
 * Created by lodoss on 23/12/16.
 */

/**
 * Interface for controlling state of emulated device.
 */
public interface EmulatedDeviceControl {
    /**
     * Change color
     * @param color
     */
    void setColor(int color);

    void turnStrobeOn();
    void turnStrobeOff();

}
