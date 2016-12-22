package alex_shutov.com.ledlights.device_commands.main_logic.serialization_general;

/**
 * Created by lodoss on 22/12/16.
 */

public interface DataHeader {

    /**
     * Get size of command's data header
     * @return
     */
    int getHeaderSize();

    /**
     * Write command's data header to result array.
     * @param result
     * @param offset
     */
    void writeToResult(byte[] result, int offset);

}
