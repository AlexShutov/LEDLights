package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.CommFeedbackInterface;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.CommInterface;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.hex_general.Adapter;
import alex_shutov.com.ledlights.hex_general.PortInfo;

/**
 * Created by lodoss on 12/10/16.
 */

/**
 * This adapter is a Decorator, tunneling all calls to wrapped instance (to ESB mapper).
 */
public class BtCommAdapter extends Adapter implements BtCommPort {
    private static final String LOG_TAG = BtCommAdapter.class.getSimpleName();
    public static CommInterface dummyCommInterface = new CommInterface() {
        @Override
        public void startConnection() {

        }

        @Override
        public void disconnect() {

        }

        @Override
        public boolean isDeviceConnected() {
            return false;
        }

        @Override
        public void sendData(byte[] data) {

        }
    };
    public static CommFeedbackInterface dummyCommFeedback = new CommFeedbackInterface() {
        @Override
        public void onConnectionStarted(BtDevice btDevice) {

        }

        @Override
        public void onConnectionFailed() {

        }

        @Override
        public void onDataSent() {

        }

        @Override
        public void onDataSendFailed() {

        }

        @Override
        public void onReconnected(boolean isSameDevice) {

        }

        @Override
        public void onDummyDeviceSelected() {

        }

        @Override
        public void onReconnectAttemptFailed() {

        }
    };

    private CommInterface decoree;

    /**
     * Inherited from 'Adapter'
     * use dummy value as decoree, which simply does nothing, instead
     * of checking null reference all time.
     */

    @Override
    public void initialize() {
        decoree = dummyCommInterface;
    }

    @Override
    public PortInfo getPortInfo() {
        PortInfo info = new PortInfo();
        info.setPortCode(PortInfo.PORT_BLUETOOTH_EXTERNAL_INTERFACE);
        info.setPortDescription("External interface port");
        return info;
    }

    /**
     * Inherited from BtCommPort
     */

    @Override
    public void startConnection() {
        decoree.startConnection();
    }


    @Override
    public void disconnect() {
        decoree.disconnect();
    }

    @Override
    public boolean isDeviceConnected() {
        return decoree.isDeviceConnected();
    }

    @Override
    public void sendData(byte[] data) {
        decoree.sendData(data);
    }

    public void setDecoree(CommInterface decoree) {
        this.decoree = decoree;
    }
}
