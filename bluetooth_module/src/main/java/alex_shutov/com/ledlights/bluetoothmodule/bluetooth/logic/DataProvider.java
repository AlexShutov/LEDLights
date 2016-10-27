package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Alex on 10/27/2016.
 */

/**
 * There is two way for passing dependencies into atomic algorithms -
 * either use DI or custom interface.
 * Second way is more appropriate, because if we do so, it will be much simpler to pass
 * mocked objects during unit testing.
 */
public interface DataProvider {
    /**
     * Almost all atomic algorithms use ESB in some way, so they need EventBus.
     * @return
     */
    EventBus provideEventBus();
}
