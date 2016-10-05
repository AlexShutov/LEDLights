package alex_shutov.com.ledlights.hex_general;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import alex_shutov.com.ledlights.hex_general.esb.TestReceiveInterface;
import alex_shutov.com.ledlights.hex_general.esb.TestReceiveMapper;
import alex_shutov.com.ledlights.hex_general.esb.TestSendMapper;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class EsbTest extends ApplicationTestCase<Application> implements TestReceiveInterface {

    private EventBus eventBus;
    private TestSendMapper sendMapper;
    private TestReceiveMapper receiveMapper;

    private Integer receivedInteger = null;
    private String receivedString = null;
    private String received2String = null;
    private Integer received2Integer = null;

    public EsbTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        createObjects();
    }



    private void createObjects(){
        eventBus = EventBus.getDefault();
        sendMapper = new TestSendMapper(eventBus);
        receiveMapper = new TestReceiveMapper(eventBus, this);
    }

    public void testObjectsInitialized(){
        assertNotNull(eventBus);
        assertNotNull(sendMapper);
        assertNotNull(receiveMapper);

        assertNull(receivedInteger);
        assertNull(receivedString);
        assertNull(received2String);
        assertNull(received2Integer);
    }

    public void testMapping(){
        Log.i("123", "Testing mapping");
        sendMapper.register();
        receiveMapper.register();

        // test sending string
        String testString = "Some string to be sent";
        assertNull(receivedString);
        sendMapper.sendString(testString);
        assertNotNull(receivedString);
        assertEquals(testString, receivedString );

        // send integer
        Integer testInteger = 123;
        assertNull(receivedInteger);
        sendMapper.sendInteger(testInteger);
        assertNotNull(receivedInteger);
        assertEquals(testInteger, receivedInteger);

        // send integer and string
        String str2 = "Second string";
        Integer int2 = 321;
        assertNull(received2String);
        assertNull(received2Integer);
        sendMapper.sendStringAndInteger(str2, int2);
        // check if values sent correctly
        assertNotNull(received2String);
        assertNotNull(received2Integer);
        assertEquals(str2, received2String);
        assertEquals(int2, received2Integer);

        sendMapper.unregister();
        receiveMapper.unregister();
    }


    @Override
    public void onReceiveString(String string) {
        receivedString = string;
    }

    @Override
    public void onReceiveInteger(Integer integer) {
        receivedInteger = integer;
    }

    @Override
    public void onReceiveStringAndInteger(String string, Integer integer) {
        received2String = string;
        received2Integer = integer;
    }


}