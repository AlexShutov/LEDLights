package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.select_another_device_strategy;

/**
 * Created by lodoss on 15/12/16.
 */

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnEsbStore;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * When user want to select another device, we have to disconnect from current device first
 * (if any connected).
 * This is a one shot latch
 */
public class BtDisconnectDelayer {

    private EventBus eventBus;
    private BtConnPort connPort;
    private PublishSubject<Boolean> readyPipe;

    public BtDisconnectDelayer(EventBus bus, BtConnPort connPort) {
        this.connPort = connPort;
        this.eventBus = bus;
        readyPipe = PublishSubject.create();
    }

    @Subscribe
    public void onDisconnectedEvent(BtConnEsbStore.ArgumentStateChangedEvent event) {
        if (event.isGeneralCallbackFired && event.portState == BtConnEsbStore.PortState.IDLE) {
            readyPipe.onNext(true);
            readyPipe.onCompleted();
            stop();
        }
    }

    public Observable<Boolean> getEventSource() {
        return readyPipe.asObservable();
    }

    /**
     * If connection isn't active, fire event immediately, otherwise cancel connection and
     * wait for disconnect event
     */
    public void start() {
        if (!connPort.isBtConnected()) {
            readyPipe.onNext(true);
        } else {
            eventBus.register(this);
            connPort.close();
        }
    }

    public void stop() {
        eventBus.unregister(this);
    }


}
