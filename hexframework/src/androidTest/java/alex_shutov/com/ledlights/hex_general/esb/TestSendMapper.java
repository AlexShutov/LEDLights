package alex_shutov.com.ledlights.hex_general.esb;

import org.greenrobot.eventbus.EventBus;

import static alex_shutov.com.ledlights.hex_general.esb.TestEventStore.*;

/**
 * Created by lodoss on 05/10/16.
 */
public class TestSendMapper extends EsbMapper implements TestSendInterface {

    public TestSendMapper(EventBus eventBus){
        super(eventBus);
    }

    /**
     * This particular receiver doesn't need any notification from EventBus (it only posts events),
     * some other more complex sender may need notification about some system event. That is why
     * those methods is left blank
     */
    @Override
    public void register() {

    }

    @Override
    public void unregister() {

    }

    /** inherited from TestSendInterface */

    @Override
    public void sendString(String string) {
        ArgumentStringEvent arg = new ArgumentStringEvent();
        arg.string = string;
        eventBus.post(arg);
    }

    @Override
    public void sendInteger(Integer integer) {
        ArgumentIntegerEvent arg = new ArgumentIntegerEvent();
        arg.integer = integer;
        eventBus.post(arg);
    }

    @Override
    public void sendStringAndInteger(String string, Integer integer) {
        ArgumentStringAndIntegerEvent arg = new ArgumentStringAndIntegerEvent();
        arg.string = string;
        arg.integer = integer;
        eventBus.post(arg);
    }


}
