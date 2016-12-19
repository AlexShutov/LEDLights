package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.hex_general.esb.EsbEventStore;

/**
 * Created by lodoss on 06/10/16.
 */

/**
 * Some methods has no argumet - store has separate tag class for every method.
 * State methods is defined by BtConnEsbStore state enum
 */
public class BtConnEsbStore extends EsbEventStore {

    public static enum PortState {
        CONNECTED,
        CONNECTING,
        LISTENING,
        IDLE
    }

    /**
     * State event may be caused by onStateChanged(int state) method or by
     * one of onState.. methods either. isGeneralCallbackFired indicates source of event.
     */
    public static class ArgumentStateChangedEvent {
        public boolean isGeneralCallbackFired;
        public PortState portState;
    }

    public static class ArgumentMessageReadEvent {
        public byte[] message;
        public int messageSize;
    }

    // Posted on EventBust when message is sent
    public static class ArgumentMessageSentEvent {
        // tag class, nothing to pass
    }

    public static class ArgumentDeviceConnectedEvent {
        public BtDevice connectedDevice;
    }

    public static class ArgumentConnectionFailedEvent {
        // tag class
    }

    public static class ArgumentConnectionLostEvent {
        // tag class
    }

    public static class ArgumentBluetoothConnectionPortReady {
        public int portId;
    }

    public static class ArgumentBluetoothConnectionCriticalFailure {
        public int portId;
        public Exception exception;
    }

}
