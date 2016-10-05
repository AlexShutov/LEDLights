package alex_shutov.com.ledlights.hex_general.esb;

/**
 * Created by lodoss on 05/10/16.
 */
public interface TestReceiveInterface {
    void onReceiveString(String string);
    void onReceiveInteger(Integer integer);
    void onReceiveStringAndInteger(String string, Integer integer);
}
