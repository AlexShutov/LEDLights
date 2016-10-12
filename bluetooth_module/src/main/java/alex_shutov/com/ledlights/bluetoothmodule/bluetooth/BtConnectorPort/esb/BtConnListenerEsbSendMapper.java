package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb;

import org.greenrobot.eventbus.EventBus;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.BluetoothChatService;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.hex_general.esb.EsbMapper;

import static alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnEsbStore.*;

/**
 * Created by lodoss on 06/10/16.
 */
public class BtConnListenerEsbSendMapper extends EsbMapper implements BtConnPortListener {
    private static final String LOG_TAG = EsbMapper.class.getSimpleName();

    public BtConnListenerEsbSendMapper(EventBus eventBus){
        super(eventBus);
    }

    @Override
    public void register() {
    }

    @Override
    public void unregister() {
    }


    /**
     * Inherited from BtConnPortListener - methods transforms calls from that interface into
     * state objects and send those objects via EventBus
     */

    @Override
    public void onStateChanged(int state) {
        ArgumentStateChangedEvent event = new ArgumentStateChangedEvent();
        // this event is caused by general method
        event.isGeneralCallbackFired = true;
        switch (state) {
            case BluetoothChatService.STATE_CONNECTED:
                event.portState = PortState.CONNECTED;
                break;
            case BluetoothChatService.STATE_CONNECTING:
                event.portState = PortState.CONNECTING;
                break;
            case BluetoothChatService.STATE_LISTEN:
                event.portState = PortState.LISTENING;
                break;
            case BluetoothChatService.STATE_NONE:
                event.portState = PortState.IDLE;
                break;
            default:
                event.portState = PortState.IDLE;
        }
        eventBus.post(event);
    }

    @Override
    public void onStateConnected() {
        ArgumentStateChangedEvent event = new ArgumentStateChangedEvent();
        event.isGeneralCallbackFired = false;
        event.portState = PortState.CONNECTED;
        eventBus.post(event);
    }

    @Override
    public void onStateConnecting() {
        ArgumentStateChangedEvent event = new ArgumentStateChangedEvent();
        event.isGeneralCallbackFired = false;
        event.portState = PortState.CONNECTING;
        eventBus.post(event);
    }

    @Override
    public void onStateListening() {
        ArgumentStateChangedEvent event = new ArgumentStateChangedEvent();
        event.isGeneralCallbackFired = false;
        event.portState = PortState.LISTENING;
        eventBus.post(event);
    }

    @Override
    public void onStateIdle() {
        ArgumentStateChangedEvent event = new ArgumentStateChangedEvent();
        event.isGeneralCallbackFired = false;
        event.portState = PortState.IDLE;
        eventBus.post(event);
    }

    @Override
    public void onMessageRead(byte[] message, int messageSize) {
        ArgumentMessageReadEvent event = new ArgumentMessageReadEvent();
        event.message = message;
        event.messageSize = messageSize;
        eventBus.post(event);
    }

    @Override
    public void onMessageSent() {
        ArgumentMessageSentEvent event = new ArgumentMessageSentEvent();
        eventBus.post(event);
    }

    @Override
    public void onDeviceConnected(BtDevice btDevice) {
        ArgumentDeviceConnectedEvent event = new ArgumentDeviceConnectedEvent();
        event.connectedDevice = btDevice;
        eventBus.post(btDevice);
    }

    @Override
    public void onConnectionFailed() {
        ArgumentConnectionFailedEvent event = new ArgumentConnectionFailedEvent();
        eventBus.post(event);
    }

    @Override
    public void onConnectionLost() {
        ArgumentConnectionLostEvent event = new ArgumentConnectionLostEvent();
        eventBus.post(event);
    }

    @Override
    public void onPortReady(int portID) {
        ArgumentBluetoothConnectionPortReady event = new ArgumentBluetoothConnectionPortReady();
        event.portId = portID;
        eventBus.post(event);
    }

    @Override
    public void onCriticalFailure(int portID, Exception e) {
        ArgumentBluetoothConnectionCriticalFailure event =
                new ArgumentBluetoothConnectionCriticalFailure();
        event.portId = portID;
        event.exception = e;
        eventBus.post(event);
    }
}
