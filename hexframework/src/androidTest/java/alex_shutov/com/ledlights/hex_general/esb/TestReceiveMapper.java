package alex_shutov.com.ledlights.hex_general.esb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static alex_shutov.com.ledlights.hex_general.esb.TestEventStore.*;

/**
 * Created by lodoss on 05/10/16.
 */
public class TestReceiveMapper extends EsbMapper {
    TestReceiveInterface receiveInterface;

    public TestReceiveMapper(EventBus eventBus, TestReceiveInterface receiveInterface){
        super(eventBus);
        this.receiveInterface = receiveInterface;
    }

    @Override
    public void register() {
        eventBus.register(this);
    }

    @Override
    public void unregister() {
        eventBus.unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(ArgumentStringEvent stringEvent){
        receiveInterface.onReceiveString(stringEvent.string);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(ArgumentIntegerEvent integerEvent){
        receiveInterface.onReceiveInteger(integerEvent.integer);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(ArgumentStringAndIntegerEvent stringIntegerEvent){
        receiveInterface.onReceiveStringAndInteger(stringIntegerEvent.string,
                stringIntegerEvent.integer);
    }

}
