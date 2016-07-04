package alex_shutov.com.ledlights;

import android.app.Application;

import alex_shutov.com.ledlights.Bluetooth.BTConnector;
import alex_shutov.com.ledlights.Bluetooth.BTDeviceScanner;

/**
 * Created by lodoss on 30/06/16.
 */
public class LEDApplication extends Application{

    BTDeviceScanner btScanner;
    BTConnector btConnector;

    @Override
    public void onCreate() {
        super.onCreate();

        btScanner = new BTDeviceScanner(this);
        btConnector = new BTConnector(this);
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

}
