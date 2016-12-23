package alex_shutov.com.ledlights.device_commands.main_logic.commands.save_to_ee;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;

/**
 * Created by lodoss on 23/12/16.
 */

public class SaveToEECommand extends Command {

    @Override
    public int getCommandCode() {
        return 3;
    }

    /**
     * Saving commands to flash memoty is a single run background command.
     * @return
     */
    @Override
    public boolean isForegroundCommand() {
        return false;
    }
}
