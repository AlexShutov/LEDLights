package alex_shutov.com.ledlights.device_commands.main_logic.serialization_general;

import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceSender;
import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.CommandExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.CompositeExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CommandSerializer;

/**
 * Created by lodoss on 22/12/16.
 */

public class CompositeSerializer extends CompositeExecutor<CommandSerializer> {

    public CompositeSerializer() {
        super();
    }

    /**
     * We know that all executors here is of type CommandSerializer
     * @param sender
     */
    public void setDeviceSender(DeviceSender sender) {
        for (CommandSerializer s : executors) {
            s.setDeviceSender(sender);
        }
    }

    public CommandSerializer getRightExecutor(Command command) throws IllegalArgumentException {
        for (CommandSerializer s : executors) {
            if (s.canExecute(command)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unsupported command");
    }


}
