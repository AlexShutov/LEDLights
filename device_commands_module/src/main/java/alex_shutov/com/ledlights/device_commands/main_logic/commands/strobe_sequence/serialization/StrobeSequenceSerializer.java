package alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.serialization;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.StrobeSequenceCommand;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.model.StrobeFlash;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.model.StrobeSequence;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CommandSerializer;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.Constants;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.DataHeader;

import static alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.Constants.*;

/**
 * Created by lodoss on 22/12/16.
 */

public class StrobeSequenceSerializer extends CommandSerializer {

    /**
     * Inherited from CommandExecutor
     */
    @Override
    public boolean canExecute(Command command) {
        return command instanceof StrobeSequenceCommand;
    }

    /**
     * Inherited from CommandSerializer
     */

    @Override
    public void serializeCommandDataPayload(Command command, byte[] buffer, int offset) {
        StrobeSequenceCommand c = (StrobeSequenceCommand) command;
        StrobeSequence sequence = c.getSequence();
        //  check if flash is permanent
        if (sequence.isPermanent()) {
            // all information is already written from command data header, do nothing
            return;
        }
        // write flash infos one by one.
        for (StrobeFlash flash : sequence.getFlashes()) {
            long timeOn = flash.getTimeOn();
            writeTimeInterval(timeOn, buffer, offset);
            offset += TIME_INTERVAL_SERIALIZED_SIZE;
            long timeOff = flash.getTimeOff();
            writeTimeInterval(timeOff, buffer, offset);
            offset += TIME_INTERVAL_SERIALIZED_SIZE;
        }
    }

    /**
     * If this strobe is permanent (light is ON or OFF), consider sequence having no flashes,
     * even if there is some flashes.
     * @param command
     * @return
     */
    @Override
    public byte calculateDataPayloadSize(Command command) {
        StrobeSequenceCommand c = (StrobeSequenceCommand) command;
        StrobeSequence sequence = c.getSequence();
        if (sequence.isPermanent()) {
            return 0;
        }
        // flash defined by to times - On and Off.
        int flashSize = TIME_INTERVAL_SERIALIZED_SIZE * 2;
        int payloadSize = flashSize * sequence.getFlashes().size();
        return (byte) payloadSize;
    }

    @Override
    public DataHeader createDataHeader(Command command) {
        StrobeSequenceCommand c = (StrobeSequenceCommand) command;
        StrobeSequence sequence = c.getSequence();
        // create and fill new header.
        StrobeDataHeader header = new StrobeDataHeader();
        header.setOn(sequence.isOn());
        header.setPermanent(sequence.isPermanent());
        header.setRepeat(sequence.isRepeat());
        int numberOfFlashes = sequence.isPermanent() ? 0 : sequence.getFlashes().size();
        header.setNumberOfFlashes(numberOfFlashes);
        return header;
    }
}
