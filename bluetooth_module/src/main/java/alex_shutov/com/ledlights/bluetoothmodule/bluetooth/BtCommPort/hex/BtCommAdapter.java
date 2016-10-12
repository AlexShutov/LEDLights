package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex;

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

    private CommInterface decoree;



    /**
     * Inherited from 'Adapter'
     */

    @Override
    public void initialize() {
        // do nothing
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
    public void sendData(byte[] data) {
        decoree.sendData(data);
    }

    public void setDecoree(CommInterface decoree) {
        this.decoree = decoree;
    }
}
