package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex;

import alex_shutov.com.ledlights.hex_general.Adapter;
import alex_shutov.com.ledlights.hex_general.PortInfo;

/**
 * Created by lodoss on 12/10/16.
 */
public class BtCommAdapter extends Adapter implements BtCommPort {
    private static final String LOG_TAG = BtCommAdapter.class.getSimpleName();

    /**
     * Inherited from 'Adapter'
     */

    @Override
    public void initialize() {

    }

    @Override
    public PortInfo getPortInfo() {
        return null;
    }

    /**
     * Inherited from BtCommPort
     */




}
