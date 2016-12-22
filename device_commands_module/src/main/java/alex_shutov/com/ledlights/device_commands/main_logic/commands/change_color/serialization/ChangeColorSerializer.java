package alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.serialization;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CommandSerializer;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.ChangeColor;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.DataHeader;

/**
 * Created by lodoss on 22/12/16.
 */

public class ChangeColorSerializer extends CommandSerializer {

    @Override
    public void serializeCommandDataPayload(Command command, byte[] buffer, int offset) {
        // cast to right command type
        ChangeColor changeColor = (ChangeColor) command;
        writeColor(changeColor.getColor(), buffer, offset);
    }

    /**
     * Data header is empty - this is a simple command, having only single color.
     * @param command
     * @return
     */
    @Override
    public DataHeader createDataHeader(Command command) {
        return new ChangeColorDataHeader();
    }

    /**
     * Command size is 3 bytes (1 byte for each color)
     * @return
     */
    @Override
    public byte calculateDataPayloadSize(Command command) {
        return 3;
    }

    /**
     * Check command type
     * @param command
     * @return
     */
    @Override
    public boolean canExecute(Command command) {
        return command instanceof ChangeColor;
    }

}
