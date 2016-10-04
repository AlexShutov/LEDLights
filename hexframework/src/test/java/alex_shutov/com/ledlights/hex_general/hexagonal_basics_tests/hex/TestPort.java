package alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex;
import alex_shutov.com.ledlights.hex_general.Port;

/**
 * Created by Alex on 10/1/2016.
 */

/**
 * Some dummy methods
 */
public interface TestPort extends Port {

    void showMessage(String str);
    void logMessage(String str);
    void sendMessage(String str);
}
