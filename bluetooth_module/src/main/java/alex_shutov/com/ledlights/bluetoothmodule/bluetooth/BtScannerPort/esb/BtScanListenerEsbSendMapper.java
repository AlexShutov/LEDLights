package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.esb;

import org.greenrobot.eventbus.EventBus;

import java.util.Set;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPortListener;
import alex_shutov.com.ledlights.hex_general.esb.EsbMapper;

import static alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.esb.BtScanEsbStore.*;

/**
 * Created by lodoss on 06/10/16.
 */
public class BtScanListenerEsbSendMapper extends EsbMapper implements BtScanPortListener {
    public BtScanListenerEsbSendMapper(EventBus eventBus){
        super(eventBus);
    }
    // do nothing, we just posting event on a bus
    @Override
    public void register() {
    }
    @Override
    public void unregister() {
    }

    @Override
    public void onPairedDevicesReceived(Set<BtDevice> devices) {
        ArgumentPairedDevicesReceived event = new ArgumentPairedDevicesReceived();
        event.devices = devices;
        eventBus.post(event);
    }

    @Override
    public void onDeviceFound(BtDevice device) {
        ArgumentDeviceFound event = new ArgumentDeviceFound();
        event.device = device;
        eventBus.post(event);
    }

    @Override
    public void onScanCompleted() {
        ArgumentScanComplete event = new ArgumentScanComplete();
        eventBus.post(event);
    }

    @Override
    public void onPortReady(int portID) {
        ArgumentOnScanPortReady event = new ArgumentOnScanPortReady();
        event.portId = portID;
        eventBus.post(event);
    }

    @Override
    public void onCriticalFailure(int portID, Exception e) {
        ArgumentOnScanPortFailure event = new ArgumentOnScanPortFailure();
        event.portId = portID;
        event.exception = e;
        eventBus.post(event);
    }
}
