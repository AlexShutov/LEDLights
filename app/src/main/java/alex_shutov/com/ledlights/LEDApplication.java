package alex_shutov.com.ledlights;

import android.app.Application;

import java.util.UUID;

import alex_shutov.com.ledlights.Bluetooth.BTConnector;
import alex_shutov.com.ledlights.Bluetooth.BTDeviceScanner;

/**
 * Created by lodoss on 30/06/16.
 */
public class LEDApplication extends Application{

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    BTDeviceScanner btScanner;
    BTConnector btConnector;

    @Override
    public void onCreate() {
        super.onCreate();

        btScanner = new BTDeviceScanner(this);
        btConnector = new BTConnector(this, getString(R.string.uuid_secure),
                getString(R.string.uuid_insecure));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public BTDeviceScanner getDeviceScanner(){
        return btScanner;
    }

    public BTConnector getBtConnector(){
        return btConnector;
    }


    private UUID uuidFromResource(int resId){
        String id = getResources().getString(resId);
        return UUID.fromString(id);
    }
}
