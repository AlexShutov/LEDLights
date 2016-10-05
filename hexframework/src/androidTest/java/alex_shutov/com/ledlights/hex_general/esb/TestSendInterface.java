package alex_shutov.com.ledlights.hex_general.esb;

import android.support.annotation.IntegerRes;

/**
 * Created by lodoss on 05/10/16.
 */
public interface TestSendInterface {

    void sendString(String string);
    void sendInteger(Integer integer);
    void sendStringAndInteger(String string, Integer integer);

}
