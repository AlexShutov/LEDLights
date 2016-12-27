package alex_shutov.com.ledlights.app_facade;

/**
 * Created by lodoss on 27/12/16.
 */

public interface AppFacadeDeviceListener {

    void onDeviceConnected();
    void onDeviceConnectionFailed();
    void onDeviceReconnected();
    void onDumyDeviceSelected();

}
