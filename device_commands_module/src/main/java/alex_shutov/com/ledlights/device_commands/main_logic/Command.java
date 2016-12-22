package alex_shutov.com.ledlights.device_commands.main_logic;

/**
 * Created by lodoss on 22/12/16.
 */

/**
 * Base class for all commands, supported by device. There is not too many commands and every
 * command has unique commands code (device firmware store command parsers in index array).
 * We can do that here too.
 * Other command parameters is defined in derived classes.
 * Command can be background and foreground. Foreground means, that this command will be restored
 * if another non- foreground command interrupt it. Example of foreground commands is
 * 'change color'. Background commands are 'flash sequence', 'save to memory'.
 */
public abstract class Command {
    /**
     * code of that command on device end. Those codes must be unique.
     */

    private byte[] data;

    public abstract int getCommandCode();

    /**
     * Should it be restored after interruption)
     * @return true if this is a foreground command
     */
    public abstract boolean isForegroundCommand();

    // accessors

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }



}
