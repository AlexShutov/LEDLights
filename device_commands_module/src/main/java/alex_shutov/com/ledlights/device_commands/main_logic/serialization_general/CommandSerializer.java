package alex_shutov.com.ledlights.device_commands.main_logic.serialization_general;

/**
 * Created by lodoss on 22/12/16.
 */

import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceSender;
import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.CommandExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CommandHeader;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.DataHeader;

/**
 * This is a base class for command serializer. Serializer know how to transform command to the
 * byte array device understand.
 * Serializer has single abstract method 'serialize', which take command and return byte array.
 * It doesn't implement method 'canExecute', because it is responsibility of derived class.
 */
public abstract class CommandSerializer implements CommandExecutor {

    private DeviceSender deviceSender;

    /**
     * Convert this command into byte array. At the time when this method is called,
     * 'canExecute' method gave positive answer, so we free to proceed.
     * @param command
     * @return
     */
    public abstract void serializeCommandDataPayload(Command command, byte[] buffer, int offset);

    /**
     * Command consist of a header, following by data block. In order to serialize command we
     * need to allocate buffer for that command first. Buffer size will be size of command header
     *  + size of data block.
     *  Calculate size of data block in this method based on command state. Serializer call
     *  this method before allocating buffer.
     * @return
     */
    public abstract byte calculateDataPayloadSize();

    /**
     * Create data header, used by this command and fill it in. This command is called
     * during serialization
     * @return
     */
    public abstract DataHeader createDataHeader(Command command);

    /**
     * Transform command to byte array and send it to device
     * @param command
     */
    @Override
    public void execute(Command command) {
        int payloadSize = calculateDataPayloadSize();
        // 4 bytes for command header + size of data block
        int total_size = 4 + payloadSize;
        // allocate memory for command
        byte[] result = new byte[total_size];
        // create command header
        CommandHeader commandHeader = new CommandHeader();
        // set size of data block to command header
        commandHeader.setDataSize(payloadSize);
        commandHeader.setCommandCode(command.getCommandCode());
        writeCommandHeader(commandHeader, result, 0);
        // command data goes  right after header (offset 4 bytes)
        int currOffset = 4;
        // create command header
        DataHeader dataHeader = createDataHeader(command);
        int headerSize = dataHeader.getHeaderSize();
        dataHeader.writeToResult(result, currOffset);
        // move current offset to data header size
        currOffset += headerSize;
        // now we can write the rest of command
        serializeCommandDataPayload(command, result, currOffset);
        //  command is ready now, send it to device
        deviceSender.sendData(result);
    }


    /**
     * Specify sender - interfce to connected device.
     * @param deviceSender
     */
    public void setDeviceSender(DeviceSender deviceSender) {
        this.deviceSender = deviceSender;
    }

    /**
     * Write command header into result byte array starting from positiion 'offset' (= 0)
     * @param header command header
     * @param block result byte array
     * @param offset offset of byte array (=0)
     */
    private void writeCommandHeader(CommandHeader header, byte[] block, int offset) {
        block[offset + 0] = CommandHeader.TRAILING_SYMBOL;
        block[offset + 1] = (byte) header.getCommandCode();
        block[offset + 2] = (byte) header.getDataSize();
        block[offset + 3] = (byte) CommandHeader.NEW_LINE_SYMBOL;
    }
}
