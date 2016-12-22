package alex_shutov.com.ledlights.device_commands.main_logic;

import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceSender;

/**
 * Created by lodoss on 22/12/16.
 */

public class CompositeSerializer extends CompositeExecutor {

    public CompositeSerializer() {
        super();
    }

    /**
     * We know that all executors here is of type CommandSerizlizer
     * @param sender
     */
    public void setDeviceSender(DeviceSender sender) {
        for (CommandExecutor e : executors) {
            CommandSerializer s = (CommandSerializer) e;
            s.setDeviceSender(sender);
        }
    }

}
