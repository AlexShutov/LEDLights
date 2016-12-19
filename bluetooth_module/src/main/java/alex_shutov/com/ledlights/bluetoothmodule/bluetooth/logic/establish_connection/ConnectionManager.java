package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection;

/**
 * Created by Alex on 11/5/2016.
 */

/**
 * Algorithm for establishing connection to bluetooth device has
 * few strategies :
 * - connect to last device,
 * - connect to device from history,
 * - run UI, discover all devices and let user choose one (last resort, optional)
 * ConnectionManagerImpl implement that interface, too and it has all
 * strategies in it.
 *
 */
public interface ConnectionManager {

    void attemptToEstablishConnection();

    boolean isAttemptingToConnect();

    void stopConnecting();

    void selectDeviceByUi();

    public void setCallback(ConnectionManagerCallback callback);
}
