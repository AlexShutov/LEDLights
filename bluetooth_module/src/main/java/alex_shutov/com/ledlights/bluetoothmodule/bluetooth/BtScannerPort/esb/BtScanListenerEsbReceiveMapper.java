package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.esb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPortListener;
import alex_shutov.com.ledlights.hex_general.esb.EsbMapper;

import static alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.esb.BtScanEsbStore.*;

/**
 * Created by lodoss on 06/10/16.
 */
public class BtScanListenerEsbReceiveMapper extends EsbMapper {

    private BtScanPortListener listener;

    public BtScanListenerEsbReceiveMapper(EventBus eventBus){
        super(eventBus);
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
    public void onPairedDevicesReceivedEvent(ArgumentPairedDevicesReceived event){
        listener.onPairedDevicesReceived(event.devices);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDeviceFoundEvent(ArgumentDeviceFound event){
        listener.onDeviceFound(event.device);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onScanCompleteEvent(ArgumentScanComplete event){
        listener.onScanCompleted();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onPortReadyEvent(ArgumentOnScanPortReady event){
        listener.onPortReady(event.portId);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onCriticalFailureEvent(ArgumentOnScanPortFailure event){
        listener.onCriticalFailure(event.portId, event.exception);
    }

    public void setListener(BtScanPortListener listener) {
        this.listener = listener;
    }
}
