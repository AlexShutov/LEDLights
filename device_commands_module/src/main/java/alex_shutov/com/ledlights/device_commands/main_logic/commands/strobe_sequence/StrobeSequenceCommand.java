package alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.model.StrobeSequence;

/**
 * Created by lodoss on 22/12/16.
 */

public class StrobeSequenceCommand extends Command {

    private StrobeSequence sequence;

    @Override
    public int getCommandCode() {
        return 2;
    }

    /**
     * Strobe is background command. It will not be restored after interrupting
     * command is finished.
     * @return
     */
    @Override
    public boolean isForegroundCommand() {
        return false;
    }

    public StrobeSequence getSequence() {
        return sequence;
    }

    public void setSequence(StrobeSequence sequence) {
        this.sequence = sequence;
    }
}
