package alex_shutov.com.ledlights.device_commands.ControlPort;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.CommandExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulatedDeviceControl;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulationControl;
import alex_shutov.com.ledlights.hex_general.Adapter;
import alex_shutov.com.ledlights.hex_general.PortInfo;

/**
 * Created by lodoss on 23/12/16.
 */

public class ControlPortAdapter extends Adapter implements ControlPort , EmulatedDeviceControl {

    /**
     * Inherited from Port
     */

    EmulationCallback callback;

    private EmulationControl emulationControl;

    private CommandExecutor executor;

    @Override
    public PortInfo getPortInfo() {
        PortInfo portInfo = new PortInfo();
        portInfo.setPortCode(PortInfo.PORT_DEVICE_COMMANDS_COMM);
        portInfo.setPortDescription("Port for communication with logic from outside of this cell");
        return portInfo;
    }

    @Override
    public void initialize() {

    }

    /**
     * Inherited from ControlPort
     */

    @Override
    public void enableEmulation() {
        emulationControl.turnEmulationOn();
    }

    @Override
    public void disableEmulation() {
        emulationControl.turnEmulationOff();
    }

    @Override
    public void setCallback(EmulationCallback callback) {
        this.callback = callback;
    }

    /**
     * Inherited from CommandExecutor
     */

    @Override
    public boolean canExecute(Command command) {
        return executor.canExecute(command);
    }

    @Override
    public void execute(Command command) {
        executor.execute(command);
    }

    /**
     * Inherited from EmulatedDeviceControl
     */

    @Override
    public void setColor(int color) {
        callback.onLEDColorChanged(color);
    }

    @Override
    public void turnStrobeOn() {
        callback.onStrobeOn();
    }

    @Override
    public void turnStrobeOff() {
        callback.onStrobeOff();
    }



    public void setEmulationControl(EmulationControl emulationControl) {
        this.emulationControl = emulationControl;
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }


}
