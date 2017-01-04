package alex_shutov.com.ledlights.device_commands.ControlPort;

import alex_shutov.com.ledlights.device_commands.main_logic.CommandExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulatedDeviceControl;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulationControl;
import alex_shutov.com.ledlights.hex_general.Port;

/**
 * Created by lodoss on 23/12/16.
 */

/**
 * Port, by which application control logic cell, converting commands, coming from application to
 * format, device can understand
 * Application hands commands to this cell by using this port, that is why it
 * implement DeviceSender.
 */
public interface ControlPort extends Port, CommandExecutor, EmulationControl {
    /**
     * UI get reference to this cell and enable emulation when application is opened or restored.
     * Usually all logic run in background in Service. Emulation is turned OFF for preserving
     * battery when app is minimized.
     */

    /**
     * Application is minimized or screen, displaying state is closed.
     */

    /**
     * Register UI as enulation callback callback.
     * @param callback
     */
    void setCallback(EmulationCallback callback);

}
