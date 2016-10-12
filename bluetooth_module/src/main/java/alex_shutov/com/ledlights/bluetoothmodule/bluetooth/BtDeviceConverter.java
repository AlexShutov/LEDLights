package alex_shutov.com.ledlights.bluetoothmodule.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

/**
 * Created by Alex on 7/28/2016.
 */
public class BtDeviceConverter {
    @SuppressLint("NewApi")
    public static BtDevice fromAndroidBluetoothDevice(BluetoothDevice btDevice){
        BtDevice device = new BtDevice();
        /** BluetoothDevice has no in formation about connection type */
        device.setSecureOperation(false);
        String deviceName = btDevice.getName();
        String address = btDevice.getAddress();
        String description = "";
        device.setDeviceName(deviceName);
        device.setDeviceAddress(address);
        device.setDeviceDescription(description);
        ParcelUuid uuids[] = btDevice.getUuids();
        device.setDeviceUuIdSecure("");
        device.setDeviceUuIdInsecure("");

        return device;
    }
}
