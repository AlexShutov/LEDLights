package alex_shutov.com.ledlights.device_commands.main_logic.commands.save_to_ee.serialization;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.save_to_ee.SaveToEECommand;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.serialization.DataSenderStorage;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CommandSerializer;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CompositeSerializer;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.DataHeader;

/**
 * Created by lodoss on 23/12/16.
 */

public class SaveToEESerializer extends CommandSerializer {

    // store, which is used for getting access to right serializer
    private CompositeSerializer serializerStore;

    private DataSenderStorage deviceMockForWrappedCommand;

    // serialization results of foreground and background commands.
    private byte[] foregroundCommandData;
    private byte[] backgroundCommandData;
    int payloadSize;

    public SaveToEESerializer(CompositeSerializer serializer) {
        serializerStore = serializer;
        deviceMockForWrappedCommand = new DataSenderStorage();
    }

    /**
     * Inherited from CommandExecutor
     */

    @Override
    public boolean canExecute(Command command) {
        return command instanceof SaveToEECommand;
    }

    /**
     * This executor store temporary serialization results, so easiest way to ensure
     * thread safety is to make one serialization at a time.
     * @param command
     */
    @Override
    public void execute(Command command) {
        synchronized (this) {
            super.execute(command);
        }
    }

    /**
     * Inherited from CommandSerializer
     */

    /**
     * At this point serializer has foreground and background command data
     * @param command
     * @param buffer
     * @param offset
     */
    @Override
    public void serializeCommandDataPayload(Command command, byte[] buffer, int offset) {
        // write foreground command
        if (null != foregroundCommandData) {
            for (int i = 0; i < foregroundCommandData.length; ++i) {
                buffer[offset + i] = foregroundCommandData[i];
            }
        }
        offset += foregroundCommandData.length;
        if (null != backgroundCommandData) {
            for (int i = 0; i < backgroundCommandData.length; ++i) {
                buffer[offset + i] = backgroundCommandData[i];
            }
        }
        // remove old serialization data
        foregroundCommandData = null;
        backgroundCommandData = null;
        payloadSize = 0;
    }

    /**
     * Payload size is used by device for figuring out data size
     * @param command
     * @return
     */
    @Override
    public byte calculateDataPayloadSize(Command command) {
        return (byte) payloadSize;
    }

    /**
     * This is a first method, called after serialization started. Prepare data in it.
     * @param command
     * @return
     */
    @Override
    public DataHeader createDataHeader(Command command) {
        // write arguments
        serializeWrappedCommands(command);
        //
        SaveToEECommand c = (SaveToEECommand) command;
        SaveToEEDataHeader header = new SaveToEEDataHeader();
        header.setCellIndex(c.getCellIndex());
        header.setLoadCommand(c.isLoadCommand());
        header.setEraseCell(c.isEraseCell());
        boolean hasBackgroundCommand = c.getBackgroundCommand() != null;
        header.setHasBackgroundCommand(hasBackgroundCommand);
        return header;
    }

    private void serializeWrappedCommands(Command command) {
        SaveToEECommand c = (SaveToEECommand) command;
        payloadSize = 0;
        if (c.isLoadCommand()) {
            // we serialize wrapped commands only for save commands.
            return;
        }
        // process foreground command
        Command foreground = c.getForegroundCommand();
        CommandSerializer foregroundSerializer = serializerStore.getRightExecutor(foreground);
        // serialize foreground command, result will be saved in mocked device
        foregroundSerializer.execute(foreground, deviceMockForWrappedCommand, false);
        foregroundCommandData = deviceMockForWrappedCommand.getSerializedCommand();
        Command backgroundCommand = c.getBackgroundCommand();
        if (null == backgroundCommand) {
            // we're done here
            return;
        }
        // serialize background command
        CommandSerializer backgroundSerializer =
                serializerStore.getRightExecutor(backgroundCommand);
        backgroundSerializer.execute(backgroundCommand, deviceMockForWrappedCommand, false);
        backgroundCommandData = deviceMockForWrappedCommand.getSerializedCommand();
        // payload is a data for foreground and background command
        payloadSize = foregroundCommandData.length + backgroundCommandData.length;
    }


}
