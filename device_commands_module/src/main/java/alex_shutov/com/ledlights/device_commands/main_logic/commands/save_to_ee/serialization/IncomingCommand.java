package alex_shutov.com.ledlights.device_commands.main_logic.commands.save_to_ee.serialization;

/**
 * Created by Alex on 12/23/2016.
 */

/**
 * This class model class from device, carrying information about
 * command - command code, pointer to data block and data block size.
 * Device parse received byte array into IncomingCommand and data block.
 * Even though this isn't the best practice, it is convenient to store
 * saved commands header in this type on device
 */
public class IncomingCommand {
    int dataBlockSize;
    // just for indication here
    int ptrToDataBlock;
    // code of that command.
    int commandCode;


    public void writeToBlock(byte[] block, int offset) {
        block[offset + 0] = (byte) getDataBlockSize();
        // pointer to data block in unused when loading data - it is recomputed again
        block[offset + 1] = (byte) 0;
        // VERY nasty detail - device store it as pointer (chat*), which takes up two bytes.
        block[offset + 2] = 0;
        block[offset + 3] = (byte) getCommandCode();
    }

    /**
     * This object take 3 byte in resulting data block.
     * @return
     */
    public int getSerializedSize() {
        return 4;
    }

    public int getDataBlockSize() {
        return dataBlockSize;
    }

    public void setDataBlockSize(int dataBlockSize) {
        this.dataBlockSize = dataBlockSize;
    }

    public int getPtrToDataBlock() {
        return ptrToDataBlock;
    }

    public void setPtrToDataBlock(int ptrToDataBlock) {
        this.ptrToDataBlock = ptrToDataBlock;
    }

    public int getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(int commandCode) {
        this.commandCode = commandCode;
    }
}
