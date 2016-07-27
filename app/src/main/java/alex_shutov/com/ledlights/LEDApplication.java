package alex_shutov.com.ledlights;

import android.app.Application;

import java.util.UUID;

import alex_shutov.com.ledlights.Bluetooth.BtConnectorPort.hex.BtConnAdapter;
import alex_shutov.com.ledlights.Bluetooth.BtScannerPort.BTDeviceScanner;
import alex_shutov.com.ledlights.Bluetooth.BtConnectorPort.LogListener;
import alex_shutov.com.ledlights.Bluetooth.BtConnectorPort.hex.BtConnPort;

/**
 * Created by lodoss on 30/06/16.
 */
public class LEDApplication extends Application{
    // Unique UUID for this application
    public static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    // HC-05 UUID  "00001101-0000-1000-8000-00805F9B34FB"
    public static final UUID HC_05_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BtConnAdapter btAdapter;
    BtConnPort btPort;
    BTDeviceScanner btScanner;

    private LogListener btPortListener;

    @Override
    public void onCreate() {
        super.onCreate();
        btScanner = new BTDeviceScanner(this);

        btPortListener = new LogListener(this);
        // hex
        btAdapter = new BtConnAdapter(this);
        btAdapter.setPortListener(btPortListener);
        btAdapter.initialize();
        /** adapter is a port */
        btPort = btAdapter;

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public BTDeviceScanner getDeviceScanner(){
        return btScanner;
    }


    private UUID uuidFromResource(int resId){
        String id = getResources().getString(resId);
        return UUID.fromString(id);
    }

    public BtConnPort getBtPort() {
        return btPort;
    }
}
