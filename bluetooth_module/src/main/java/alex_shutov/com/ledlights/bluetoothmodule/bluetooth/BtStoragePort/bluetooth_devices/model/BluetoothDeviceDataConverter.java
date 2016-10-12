package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.model;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;

/**
 * Created by lodoss on 11/10/16.
 */
public class BluetoothDeviceDataConverter {
    /**
     * Convert application model into database model
     * @param src
     * @return
     */
    public static BluetoothDevice convertToDbModel(BtDevice src){
        BluetoothDevice device = new BluetoothDevice();
        device.setDeviceName(src.getDeviceName());
        device.setDeviceAddress(src.getDeviceAddress());
        device.setDeviceUuIdSecure(src.getDeviceUuIdSecure());
        device.setDeviceUuIdInsecure(src.getDeviceUuIdInsecure());
        device.setDeviceDescription(src.getDeviceDescription());
        device.setConnectionTime(0);
        return device;
    }

    public static BtDevice convertFromDbModel(BluetoothDevice dbModel){
        BtDevice device = new BtDevice();
        device.setDeviceName(dbModel.getDeviceName());
        device.setDeviceAddress(dbModel.getDeviceAddress());
        device.setDeviceUuIdSecure(dbModel.getDeviceUuIdSecure());
        device.setDeviceUuIdInsecure(dbModel.getDeviceUuIdInsecure());
        device.setDeviceDescription(dbModel.getDeviceDescription());
        // device has to be paired to be saved in db
        device.setPaired(true);
        // operation is secure by default
        device.setSecureOperation(true);
        return device;
    }

}
