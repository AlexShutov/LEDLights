package alex_shutov.com.ledlights.device_commands.main_logic.emulation_general;

/**
 * Created by Alex on 12/24/2016.
 */

/**
 * Emulated device has to know of the moment when command completes.
 * DeviceEmulator will check if there is any paused command.
 * Emulation executor get reference to emulated device interface for altering state
 * of that device.
 */
public interface LinkToEmulator {

    /**
     * Inform emulator that this command ended. It will react accordingly.
     * @param isForegroundCommand
     */
    void onCommandCompleted(boolean isForegroundCommand);

    /**
     * Get reference to interface to emulated device.
     * @return
     */
    EmulatedDeviceControl getDeviceControl();
}
