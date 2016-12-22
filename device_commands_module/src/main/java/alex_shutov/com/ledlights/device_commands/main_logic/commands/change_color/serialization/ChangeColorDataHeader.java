package alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.serialization;

import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.DataHeader;

/**
 * Created by lodoss on 22/12/16.
 */

/**
 * Command 'change color' has no data header - this one is empty
 */
public class ChangeColorDataHeader implements DataHeader {

    @Override
    public int getHeaderSize() {
        return 0;
    }

    @Override
    public void writeToResult(byte[] result, int offset) {
        // do nothing
    }
}
