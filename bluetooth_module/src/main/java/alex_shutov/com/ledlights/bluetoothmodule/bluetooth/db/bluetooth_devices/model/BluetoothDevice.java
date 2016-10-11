package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.model;

import io.realm.RealmObject;
import io.realm.annotations.RealmModule;

/**
 * Created by lodoss on 11/10/16.
 */

/**
 * Bluetooth device data stored in database
 */
public class BluetoothDevice extends RealmObject {


    private String deviceName = "";
    private String deviceAddress = "";
    private String deviceUuIdSecure = "";
    private String deviceUuIdInsecure = "";
    private String deviceDescription = "";

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceUuIdSecure() {
        return deviceUuIdSecure;
    }

    public void setDeviceUuIdSecure(String deviceUuIdSecure) {
        this.deviceUuIdSecure = deviceUuIdSecure;
    }

    public String getDeviceUuIdInsecure() {
        return deviceUuIdInsecure;
    }

    public void setDeviceUuIdInsecure(String deviceUuIdInsecure) {
        this.deviceUuIdInsecure = deviceUuIdInsecure;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public void setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }

}
