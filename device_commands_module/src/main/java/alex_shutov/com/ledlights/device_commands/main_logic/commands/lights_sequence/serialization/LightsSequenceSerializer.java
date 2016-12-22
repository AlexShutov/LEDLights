package alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.serialization;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.LightsSequenceCommand;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.models.Light;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.models.LightsSequence;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CommandSerializer;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.DataHeader;

/**
 * Created by lodoss on 22/12/16.
 */

public class LightsSequenceSerializer extends CommandSerializer {

    private static final int COLOR_SIZE = 3;
    private static final int INTERVAL_SIZE = 4;
    /**
     * Serialized data for one light takes 7 bytes: 3 bytes for color and 4 for duration.
     * Duration: 2 bytes : milliseconds, 1 byte: seconds, 1 byte: minutes
     */
    private static final int LIGHT_DATA_SIZE = COLOR_SIZE + INTERVAL_SIZE;


    /**
     * Inherited from CommandExecutor
     */

    @Override
    public boolean canExecute(Command command) {
        return command instanceof LightsSequenceCommand;
    }

    /**
     * Inherited from CommandSerializer
     */

    @Override
    public void serializeCommandDataPayload(Command command, byte[] buffer, int offset) {
        LightsSequenceCommand c = (LightsSequenceCommand) command;
        LightsSequence sequence = c.getLightsSequence();
        for (Light l : sequence.getLights()) {
            int color = l.getColor();
            writeColor(color, buffer, offset);
            offset += COLOR_SIZE;
            long duration = l.getDuration();
            writeTimeInterval(duration, buffer, offset);
            offset += INTERVAL_SIZE;
        }
    }

    @Override
    public byte calculateDataPayloadSize(Command command) {
        LightsSequenceCommand c = (LightsSequenceCommand) command;
        LightsSequence sequence = c.getLightsSequence();
        int numberOfLights = sequence.getLights().size();
        return (byte) ( numberOfLights * LIGHT_DATA_SIZE);
    }

    @Override
    public DataHeader createDataHeader(Command command) {
        LightsSequenceCommand c = (LightsSequenceCommand) command;
        // get reference to sequence itself
        LightsSequence sequence = c.getLightsSequence();
        // create data header
        LightsSequenceDataHeader dataHeader = new LightsSequenceDataHeader();
        int numberOfLights = sequence.getLights().size();
        dataHeader.setNumberOfLights(numberOfLights);
        boolean isRepeating = sequence.isRepeating();
        dataHeader.setRepeating(isRepeating);
        boolean isSmoothSwitching = sequence.isSmoothSwitching();
        dataHeader.setSmoothSwitching(isSmoothSwitching);
        return dataHeader;
    }
}
