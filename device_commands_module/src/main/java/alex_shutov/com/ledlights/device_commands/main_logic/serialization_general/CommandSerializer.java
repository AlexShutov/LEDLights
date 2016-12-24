package alex_shutov.com.ledlights.device_commands.main_logic.serialization_general;

/**
 * Created by lodoss on 22/12/16.
 */

import java.util.Calendar;
import java.util.Date;

import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceSender;
import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.CommandExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.model.Color;

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
    public abstract byte calculateDataPayloadSize(Command command);

    /**
     * Create data header, used by this command and fill it in. This command is called
     * during serialization
     * @return
     */
    public abstract DataHeader createDataHeader(Command command);

    /**
     * Narrow scope of command
     * @param command
     */
    @Override
    public void execute(Command command) {
        execute(command, deviceSender, true);
    }

    /**
     * Transform command to byte array and send it to device
     * @param command
     * @param sender
     */
    public void execute(Command command, DeviceSender sender, boolean writeCommandHeader) {
        // create command header
        CommandHeader commandHeader = new CommandHeader();
        // create data header
        DataHeader dataHeader = createDataHeader(command);
        // compute total size of data block, size = sizeOf(dataHeader) + sizeOf(data)
        int dataSize = dataHeader.getHeaderSize() + calculateDataPayloadSize(command);
        // 4 bytes for command header + size of data block (if we write command header)
        int total_size = dataSize;
        if (writeCommandHeader) {
            total_size += 4;
        }
        // allocate memory for command
        byte[] result = new byte[total_size];
        if (writeCommandHeader) {
            // set size of data block to command header
            commandHeader.setDataSize(dataSize);
            commandHeader.setCommandCode(command.getCommandCode());
            writeCommandHeader(commandHeader, result, 0);
        }
        // command data goes  right after header (offset 4 bytes)
        int currOffset =  writeCommandHeader ? 4 : 0;
        int headerSize = dataHeader.getHeaderSize();
        dataHeader.writeToResult(result, currOffset);
        // move current offset to data header size
        currOffset += headerSize;
        // now we can write the rest of command
        serializeCommandDataPayload(command, result, currOffset);
        //  command is ready now, send it to device
        sender.sendData(result);
    }

    /**
     * Specify sender - interfce to connected device.
     * @param deviceSender
     */
    public void setDeviceSender(DeviceSender deviceSender) {
        this.deviceSender = deviceSender;
    }

    /**
     * Write command header into result byte array starting from position 'offset' (= 0)
     * @param header command header
     * @param block result byte array
     * @param offset offset of byte array (=0)
     */
    private void writeCommandHeader(CommandHeader header, byte[] block, int offset) {
        block[offset + 0] = CommandHeader.TRAILING_SYMBOL;
        block[offset + 1] = (byte) header.getCommandCode();
        block[offset + 2] = (byte) header.getDataSize();
        block[offset + 3] = CommandHeader.NEW_LINE_SYMBOL;
    }

    /**
     * Helper serialization methods.
     */

    /**
     * Write color data to result byte array.
     * @param color
     * @param block
     * @param offset
     */
    protected void writeColor(int color, byte[] block, int offset) {
        // get color from command and convert it to device format
        Color c = Color.fromSystemColor(color);
        // write command data to the buffer
        block[offset + 0] = (byte) c.getRed();
        block[offset + 1] = (byte) c.getGreen();
        block[offset + 2] = (byte) c.getBlue();
    }

    /**
     * Write interval into result byte array.
     * @param timeMillis  Interval duration in milliseconds
     * @param block result destination array
     * @param offset offset of write position
     */
    protected void writeTimeInterval(long timeMillis, byte[] block, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        int milliseconds = calendar.get(Calendar.MILLISECOND);
        // TODO: check if readings is right

        byte millisLow = (byte) (0xFF & milliseconds);
        byte millisHigh = (byte) (((0xFF << 8) & milliseconds) >> 8);

        // write to array
        block[offset + 0] = millisLow;
        block[offset + 1] = millisHigh;
        block[offset + 2] = (byte) seconds;
        block[offset + 3] = (byte) minutes;
    }


}
