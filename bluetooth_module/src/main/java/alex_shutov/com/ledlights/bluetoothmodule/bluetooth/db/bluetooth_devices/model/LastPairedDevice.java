package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.model;

import android.support.annotation.Nullable;

import io.realm.RealmObject;
import io.realm.annotations.RealmModule;

/**
 * Created by lodoss on 11/10/16.
 */

/**
 * We how to save information of last paired device. One way to so - introduce additional
 * field in BluetoothDevice, say String deviceServiceTag, which will indicate what device
 * kind those data correspond to.
 * But, Realm is noSql db, so we can explicitly introduce object, referencing last
 * paired device
 */
public class LastPairedDevice extends RealmObject {

    @Nullable
    private BluetoothDevice lastPairedDevice;

    /**
     * Time when last connection has been established
     */
    private Long pairingStartTime;
    /**
     * it == 0 if connection is still around and not null (time when connection were lost)
     * if connection were lost and there is no active connection.
     */
    private Long pairingEndTime;


    public BluetoothDevice getLastPairedDevice() {
        return lastPairedDevice;
    }

    public void setLastPairedDevice(BluetoothDevice lastPairedDevice) {
        this.lastPairedDevice = lastPairedDevice;
    }

    public Long getPairingStartTime() {
        return pairingStartTime;
    }

    public void setPairingStartTime(Long pairingStartTime) {
        this.pairingStartTime = pairingStartTime;
    }

    public Long getPairingEndTime() {
        return pairingEndTime;
    }

    public void setPairingEndTime(Long pairingEndTime) {
        this.pairingEndTime = pairingEndTime;
    }
}
