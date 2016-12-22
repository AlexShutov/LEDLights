package alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.models.LightsSequence;

/**
 * Created by lodoss on 22/12/16.
 */

public class LightsSequenceCommand extends Command {

    /**
     * Contain all lights with command properties
     */
    private LightsSequence lightsSequence;

    @Override
    public int getCommandCode() {
        return 1;
    }

    /**
     * Sequence of lights should be restored if interrupted.
     * @return
     */
    @Override
    public boolean isForegroundCommand() {
        return true;
    }

    // accessors
    public LightsSequence getLightsSequence() {
        return lightsSequence;
    }

    public void setLightsSequence(LightsSequence lightsSequence) {
        this.lightsSequence = lightsSequence;
    }
}
