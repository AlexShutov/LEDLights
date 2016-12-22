package alex_shutov.com.ledlights.device_commands.main_logic;

/**
 * Created by lodoss on 22/12/16.
 */

import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceSender;
import alex_shutov.com.ledlights.device_commands.main_logic.device_model.CommandHeader;

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
    public abstract void serializeCommand(Command command, byte[] buffer, int offset);

    /**
     * Command consist of a header, following by data block. In order to serialize command we
     * need to allocate buffer for that command first. Buffer size will be size of command header
     *  + size of data block.
     *  Calculate size of data block in this method based on command state. Serializer call
     *  this method before allocating buffer.
     * @return
     */
    public abstract byte calculateDataBlockSize();

    /**
     * Transform command to byte array and send it to device
     * @param command
     */
    @Override
    public void execute(Command command) {
        int payloadSize = calculateDataBlockSize();
        // 4 bytes for command header + size of data block
        int total_size = 4 + payloadSize;
        // allocate memory for command
        byte[] result = new byte[total_size];
        // create command header
        CommandHeader header = new CommandHeader();
        // set size of data block to command header
        header.setDataSize(payloadSize);
        header.setCommandCode(command.getCommandCode());
        writeCommandHeader(header, result, 0);
        // write the rest of command right after header (offset 4 bytes)
        serializeCommand(command, result, 4);
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
