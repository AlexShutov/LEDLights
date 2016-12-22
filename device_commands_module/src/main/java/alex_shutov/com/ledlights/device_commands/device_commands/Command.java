package alex_shutov.com.ledlights.device_commands.device_commands;

/**
 * Created by lodoss on 22/12/16.
 */

import java.util.Map;

/**
 * Base class for all commands, supported by device. There is not too many commands and every
 * command has unique commands code (device firmware store command parsers in index array).
 * We can do that here too.
 * Other command parameters is defined in derived classes.
 */
public class Command {
    /**
     * code of that command on device end. Those codes must be unique.
     */
    private int commandCode;
    private byte[] data;



    // accessors

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(int commandCode) {
        this.commandCode = commandCode;
    }
}
