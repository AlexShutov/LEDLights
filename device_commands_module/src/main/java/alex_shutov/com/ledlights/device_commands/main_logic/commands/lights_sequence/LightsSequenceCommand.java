package alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;

/**
 * Created by lodoss on 22/12/16.
 */

public class LightsSequenceCommand extends Command {

    @Override
    public int getCommandCode() {
        return 0;
    }

    @Override
    public boolean isForegroundCommand() {
        return false;
    }
}
