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
    private byte[] fgCommandData;
    private byte[] bgCommandData;

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
        if (null != fgCommandData) {
            for (int i = 0; i < fgCommandData.length; ++i) {
                buffer[offset + i] = fgCommandData[i];
            }
        }
        // if background command present
        if (null != bgCommandData) {
            // shift offset to length of foreground command
            offset += fgCommandData.length;
            for (int i = 0; i < bgCommandData.length; ++i) {
                buffer[offset + i] = bgCommandData[i];
            }
        }
        // remove old serialization data
        fgCommandData = null;
        bgCommandData = null;
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
        // serialize foreground command
        Command foreground = c.getForegroundCommand();
        CommandSerializer fgSerializer = serializerStore.getRightExecutor(foreground);
        // serialize foreground command, result will be saved in mocked device
        fgSerializer.execute(foreground, deviceMockForWrappedCommand, false);
        byte[] fgDataBlock = deviceMockForWrappedCommand.getSerializedCommand();
        byte[] fgSerializationResult = appendIncomingCommandBlock(foreground, fgDataBlock);
        fgCommandData = fgSerializationResult;

        // don't check it for now, just stick to foreground command.
        Command background = c.getBackgroundCommand();
        if (null == background) {
            // we're done here, set total data area size to size of foreground command
            payloadSize = fgCommandData.length;
            return;
        }
        // serialize background command
        CommandSerializer bgSerializer = serializerStore.getRightExecutor(background);
        // serialize background command, result will be saved in mocked device
        bgSerializer.execute(background, deviceMockForWrappedCommand, false);
        byte[] bgDataBlock = deviceMockForWrappedCommand.getSerializedCommand();
        byte[] bgSerializationResult = appendIncomingCommandBlock(background, bgDataBlock);
        bgCommandData = bgSerializationResult;
        // total data block consist of foreground and background serialized commands
        payloadSize = fgCommandData.length + bgCommandData.length;
    }

    private byte[] appendIncomingCommandBlock(Command command, byte[] serializedDataBlock) {
        IncomingCommand commandHeader = new IncomingCommand();
        commandHeader.setCommandCode(command.getCommandCode());
        // it is unused anyways
        commandHeader.setPtrToDataBlock(0);
        commandHeader.setDataBlockSize(serializedDataBlock.length);
        int headerSize = commandHeader.getSerializedSize();
        byte[] result = new byte[headerSize + serializedDataBlock.length];
        // write header first
        commandHeader.writeToBlock(result, 0);
        // copy data block
        for (int i = 0; i < serializedDataBlock.length; ++i) {
            result[headerSize +  i] = serializedDataBlock[i];
        }
        return result;
    }

}
