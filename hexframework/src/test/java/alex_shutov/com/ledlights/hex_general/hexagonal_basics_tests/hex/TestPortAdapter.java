package alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import alex_shutov.com.ledlights.hex_general.Adapter;
import alex_shutov.com.ledlights.hex_general.PortInfo;

/**
 * Created by Alex on 10/1/2016.
 */
public class TestPortAdapter extends Adapter  implements TestPort {
    private static final String LOG_TAG = TestPortAdapter.class.getSimpleName();
    private static final int TEST_PORT = 3;

    public TestPortAdapter(){
    }

    /**
     * Inherited from adapter
     */
    @Override
    public void initialize() {
        Log.i(LOG_TAG, "Initializing test port adapter");
    }

    @Override
    public PortInfo getPortInfo() {
        PortInfo portInfo = new PortInfo();
        portInfo.setPortCode(TEST_PORT);
        portInfo.setPortDescription(TestPortAdapter.class.getSimpleName());
        return portInfo;
    }

    /**
     * Inherited from TestPort
     */

    @Override
    public void showMessage(String str) {
        Log.i(LOG_TAG, "Displaying message: " + str);
    }

    @Override
    public void logMessage(String str) {
        Log.i(LOG_TAG, str);
    }

    @Override
    public void sendMessage(String str) {
        Log.i(LOG_TAG, "Message sent: " + str);
    }

}
