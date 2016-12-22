package alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.serialization;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CommandSerializer;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.ChangeColor;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.model.Color;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.DataHeader;

/**
 * Created by lodoss on 22/12/16.
 */

public class ChangeColorSerializer extends CommandSerializer {

    @Override
    public void serializeCommandDataPayload(Command command, byte[] buffer, int offset) {
        // cast to right command type
        ChangeColor changeColor = (ChangeColor) command;
        // get color from command and convert it to device format
        Color color = Color.fromSystemColor(changeColor.getColor());
        // write command data to the buffer
        buffer[offset + 0] = (byte) color.getRed();
        buffer[offset + 1] = (byte) color.getGreen();
        buffer[offset + 2] = (byte) color.getBlue();
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
    public byte calculateDataPayloadSize() {
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
