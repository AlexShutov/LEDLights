package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex;

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

    private BtCommPort decoree;



    /**
     * Inherited from 'Adapter'
     */

    @Override
    public void initialize() {
        decoree.initialize();
    }

    @Override
    public PortInfo getPortInfo() {
        return decoree.getPortInfo();
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

    @Override
    public boolean hasConnection() {
        return decoree.hasConnection();
    }

    @Override
    public BtDevice getDeviceInfo() {
        return decoree.getDeviceInfo();
    }

    /**
     * Accessors
     */

    public BtCommPort getDecoree() {
        return decoree;
    }

    public void setDecoree(BtCommPort decoree) {
        this.decoree = decoree;
    }
}
