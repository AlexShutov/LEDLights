package alex_shutov.com.ledlights.Bluetooth.BtScannerPort.hex;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import alex_shutov.com.ledlights.HexGeneral.Adapter;
import alex_shutov.com.ledlights.HexGeneral.PortInfo;

/**
 * Created by lodoss on 27/07/16.
 */
public class BtScanAdapter extends Adapter implements BtScanPort {
    private static final String LOG_TAG = BtScanAdapter.class.getSimpleName();
    private static final PortInfo portInfo;
    static {
        portInfo = new PortInfo();
        portInfo.setPortCode(PortInfo.PORT_BLUETOOTH_SCANNER);
        portInfo.setPortDescription("Port for scanning new and paired BT devices");
    }

    /**
     * Bluetooth is platform dependent
     */
    private Context context;
    /**
     * BluetoothChatService work with BluetoothDevice, so we need BluetoothAdapter to create one
     */
    private BluetoothAdapter btAdapter;

    public BtScanAdapter(Context context){
        super();
        this.context = context;
    }

    @Override
    public void initialize() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public PortInfo getPortInfo() {
        return portInfo;
    }
}
