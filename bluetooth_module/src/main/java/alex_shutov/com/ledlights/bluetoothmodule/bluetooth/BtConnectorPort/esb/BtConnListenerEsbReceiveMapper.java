package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.BluetoothChatService;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPortListener;
import alex_shutov.com.ledlights.hex_general.esb.EsbMapper;

import static alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnEsbStore.*;

/**
 * Created by lodoss on 06/10/16.
 */
public class BtConnListenerEsbReceiveMapper extends EsbMapper {

    private BtConnPortListener listener;

    public BtConnListenerEsbReceiveMapper(EventBus eventBus){
        super(eventBus);
        this.listener = listener;
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
    public void onStateChangedEvent(ArgumentStateChangedEvent event){
        int state = BluetoothChatService.STATE_NONE;
        switch (event.portState) {
            case CONNECTED:
                if (!event.isGeneralCallbackFired){
                    listener.onStateConnected();
                    return;
                }
                state = BluetoothChatService.STATE_CONNECTED;
                break;
            case CONNECTING:
                state = BluetoothChatService.STATE_CONNECTING;
                if (!event.isGeneralCallbackFired){
                    listener.onStateConnecting();
                    return;
                }
                break;
            case LISTENING:
                state = BluetoothChatService.STATE_LISTEN;
                if (!event.isGeneralCallbackFired){
                    listener.onStateListening();
                    return;
                }
                break;
            case IDLE:
                state = BluetoothChatService.STATE_NONE;
                if (!event.isGeneralCallbackFired){
                    listener.onStateIdle();
                    return;
                }
                break;
            default:
        }
        if (!event.isGeneralCallbackFired){
            throw new RuntimeException("Supposed to be general callback");
        }
        listener.onStateChanged(state);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageReadEvent(ArgumentMessageReadEvent event){
        listener.onMessageRead(event.message, event.messageSize);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageSentEvent(ArgumentMessageSentEvent event){
        listener.onMessageSent();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDeviceConnectedEvent(ArgumentDeviceConnectedEvent event){
        listener.onDeviceConnected(event.connectedDevice);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onConnectionFailedEvent(ArgumentConnectionFailedEvent event){
        listener.onConnectionFailed();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onConnectionLostEvent(ArgumentConnectionLostEvent event){
        listener.onConnectionLost();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onPortReadyEvent(ArgumentBluetoothConnectionPortReady event){
        listener.onPortReady(event.portId);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onCriticalFailureEvent(ArgumentBluetoothConnectionCriticalFailure event){
        listener.onCriticalFailure(event.portId, event.exception);
    }

    public void setListener(BtConnPortListener listener) {
        this.listener = listener;
    }
}
