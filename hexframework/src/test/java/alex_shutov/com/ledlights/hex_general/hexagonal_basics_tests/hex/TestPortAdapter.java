package alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import javax.inject.Inject;
import javax.inject.Singleton;

import alex_shutov.com.ledlights.hex_general.Adapter;
import alex_shutov.com.ledlights.hex_general.PortInfo;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.test_logic.TestObjectA;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.test_logic.TestObjectBSingleton;

/**
 * Created by Alex on 10/1/2016.
 */
public class TestPortAdapter extends Adapter  implements TestPort {
    private static final String LOG_TAG = TestPortAdapter.class.getSimpleName();
    private static final int TEST_PORT = 3;

    @Inject
    TestObjectA objA;
    @Inject
    @Singleton
    TestObjectBSingleton objB;


    public TestPortAdapter(){
    }

    /**
     * Inherited from adapter
     */
    @Override
    public void initialize() {
        System.out.println(LOG_TAG + " Initializing test port adapter");
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
        System.out.println(LOG_TAG + " Displaying message: " + str);
    }

    @Override
    public void logMessage(String str) {
        System.out.println(LOG_TAG +  str);
    }

    @Override
    public void sendMessage(String str) {
        System.out.println(LOG_TAG + " Message sent: " + str);
    }

}
