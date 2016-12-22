package alex_shutov.com.ledlights.device_commands.main_logic.serialization_general;

/**
 * Created by lodoss on 22/12/16.
 */

/**
 * Serialized command begin with command header. After command header comes data blockg
 */
public class CommandHeader {
    public static final byte TRAILING_SYMBOL = '!';
    public static final byte NEW_LINE_SYMBOL = '\n';

    private int commandCode;
    private int dataSize;

    public int getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(int commandCode) {
        this.commandCode = commandCode;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }
}
