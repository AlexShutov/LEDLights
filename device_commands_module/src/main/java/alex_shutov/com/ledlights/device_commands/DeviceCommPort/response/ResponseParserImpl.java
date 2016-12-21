package alex_shutov.com.ledlights.device_commands.DeviceCommPort.response;

/**
 * Created by lodoss on 21/12/16.
 */

public class ResponseParserImpl implements ResponseParser {

    /**
     * Device return single symbol for marking success or failure
     */
    private static final byte successSymbol = '+';
    // unused for now, check only for success.
    private static final byte failureSymbol = '-';

    @Override
    public boolean isOperationSuccessful(byte[] rawResponseData) {
        byte responseByte = rawResponseData[0];
        char c = (char)responseByte;
        return responseByte == successSymbol;
    }
}
