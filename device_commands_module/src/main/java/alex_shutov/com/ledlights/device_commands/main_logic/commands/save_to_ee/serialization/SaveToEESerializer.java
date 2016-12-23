package alex_shutov.com.ledlights.device_commands.main_logic.commands.save_to_ee.serialization;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.save_to_ee.SaveToEECommand;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CommandSerializer;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CompositeSerializer;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.DataHeader;

/**
 * Created by lodoss on 23/12/16.
 */

public class SaveToEESerializer extends CommandSerializer {

    // store, which is used for getting access to right serializer
    private CompositeSerializer serializerStore;

    /**
     * Inherited from CommandExecutor
     */

    @Override
    public boolean canExecute(Command command) {
        return command instanceof SaveToEECommand;
    }

    /**
     * Inherited from CommandSerializer
     */

    @Override
    public void serializeCommandDataPayload(Command command, byte[] buffer, int offset) {

    }

    @Override
    public byte calculateDataPayloadSize(Command command) {
        return 0;
    }

    @Override
    public DataHeader createDataHeader(Command command) {
        return null;
    }

    /**
     * Saving commands to storage is completely different from executing another commands.
     */

    @Override
    public void execute(Command command) {
        super.execute(command);
    }


    // accessors

}
