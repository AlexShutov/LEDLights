package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.BtDeviceStorageManager;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.model.BluetoothDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.model.BluetoothDeviceDataConverter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.model.LastPairedDevice;
import io.realm.Realm;
import io.realm.RealmResults;

import static alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.model.BluetoothDeviceDataConverter.*;

/**
 * Created by lodoss on 11/10/16.
 */

/**
 * Database Realm implementation.
 */
public class BtDeviceDaoImpl implements BtDeviceDao {

    private BtDeviceStorageManager storageManager;
    private Realm realm;

    public BtDeviceDaoImpl(BtDeviceStorageManager storageManager){
        this.storageManager = storageManager;
        realm = null;
    }

    private void allocateDb(){
        realm = storageManager.allocateInstance();
    }

    private void disposeOfDb(){
        storageManager.disposeOfInstance(realm);
        realm = null;
    }

    /**
     * Methods, inherited from BtDeviceDao
     */

    /**
     * Query list of devices from database (read doesn't need transaction) and convert
     * results to app's format from database format
     * @return
     */
    @NonNull
    @Override
    public List<BtDevice> getDeviceHistory() {
        allocateDb();
        List<BluetoothDevice> dbDevices = realm.where(BluetoothDevice.class).findAll();
        List<BtDevice> devices = new ArrayList<>();
        for (BluetoothDevice dd : dbDevices){
            devices.add(convertFromDbModel(dd));
        }
        disposeOfDb();
        return devices;
    }

    /**
     * Remove all record (do it in transaction)
     */
    @Override
    public void clearConnectionHistory() {
        allocateDb();
        realm.beginTransaction();
        realm.where(BluetoothDevice.class).findAll().clear();
        realm.commitTransaction();
        disposeOfDb();
    }

    @Override
    public void addMotorcycleToHistory(BtDevice device) {
        BluetoothDevice dbDevice = convertToDbModel(device);
        allocateDb();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(dbDevice);
        realm.commitTransaction();
        disposeOfDb();
    }

    @Override
    public boolean hasConnectionHistory() {
        allocateDb();
        long numberOfDevices = realm.where(BluetoothDevice.class).count();
        disposeOfDb();
        return numberOfDevices != 0;
    }

    @Nullable
    @Override
    public BtDevice getLastConnectedMotorcycleInfo() {
        allocateDb();
        RealmResults<LastPairedDevice> stats = realm.where(LastPairedDevice.class).findAll();
        LastPairedDevice stat = stats.isEmpty() ? null : stats.get(0);
        if (stat == null || stat.getLastPairedDevice() == null){
            disposeOfDb();
            return null;
        }
        BtDevice device = convertFromDbModel(stat.getLastPairedDevice());
        disposeOfDb();
        return device;
    }

    @Override
    public void clearLastConnectedDeviceInfo() {
        allocateDb();
        RealmResults<LastPairedDevice> stat = realm.where(LastPairedDevice.class).findAll();
        if (!stat.isEmpty()){
            realm.beginTransaction();
            stat.clear();
            realm.commitTransaction();
        }
        disposeOfDb();
    }

    /**
     * Add info for that device first (dao automatically updates, not adds records),
     * create and save LastDevice info, containing that device
     * @param motorcycleInfo
     */
    @Override
    public void setLastConnectedMotorcycleInfo(BtDevice motorcycleInfo) {
        allocateDb();
        // LastDeviceInfo has no primary key, so we can't update this field, have to remove
        // it first
        RealmResults<LastPairedDevice> lastInfo =  realm.where(LastPairedDevice.class).findAll();
        if (!lastInfo.isEmpty()){
            // remove record
            realm.beginTransaction();
            lastInfo.clear();
            realm.commitTransaction();
        }
        BluetoothDevice lastDevice = convertToDbModel(motorcycleInfo);
        LastPairedDevice lastDeviceInfo = new LastPairedDevice();
        // save newly created objects into database and reference one from the other
        realm.beginTransaction();
        lastDevice = realm.copyToRealmOrUpdate(lastDevice);
        lastDeviceInfo = realm.copyToRealm(lastDeviceInfo);
        lastDeviceInfo.setLastPairedDevice(lastDevice);
        realm.commitTransaction();
    }

    @NonNull
    @Override
    public Long getLastConnectionStartTime() {
        allocateDb();
        RealmResults<LastPairedDevice> lastInfo =  realm.where(LastPairedDevice.class).findAll();
        if (lastInfo.isEmpty()){
            // don't forget releasing database instance
            disposeOfDb();
            return null;
        }



        disposeOfDb();
        return null;
    }

    @NonNull
    @Override
    public Long getLastConnectionEndTime() {
        return null;
    }

    @Override
    public void setLastConnectionStartTime(long startTime) {

    }

    @Override
    public void setLastConnectionEndTime(long endTime) {

    }

}
