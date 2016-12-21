package alex_shutov.com.ledlights.device_commands.DeviceCommPort.response;

/**
 * Created by lodoss on 21/12/16.
 */

/**
 * Check if device informed of successful operation
 */
public interface ResponseParser {

    /**
     * Figure out if device recognized command
     * @param rawResponseData
     * @return
     */
    boolean isOperationSuccessful(byte[] rawResponseData);

}
