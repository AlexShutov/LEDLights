package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.model.LastPairedDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.model.BluetoothDevice;
import alex_shutov.com.ledlights.hex_general.db.StorageManager;
import io.realm.Realm;
import io.realm.RealmResults;

import static alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.model.BluetoothDeviceDataConverter.convertFromDbModel;
import static alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.model.BluetoothDeviceDataConverter.convertToDbModel;

/**
 * Created by lodoss on 11/10/16.
 */

/**
 * Database Realm implementation.
 */
public class BtDeviceDaoImpl implements BtDeviceDao {

    private StorageManager storageManager;

    public BtDeviceDaoImpl(StorageManager storageManager){
        this.storageManager = storageManager;
    }

    private Realm allocateDb(){
        return storageManager.allocateInstance();
    }

    private void disposeOfDb(Realm realm){
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
        Realm realm = allocateDb();
        List<BluetoothDevice> dbDevices = realm.where(BluetoothDevice.class).findAll();
        List<BtDevice> devices = new ArrayList<>();
        for (BluetoothDevice dd : dbDevices){
            devices.add(convertFromDbModel(dd));
        }
        disposeOfDb(realm);
        return devices;
    }

    /**
     * Remove all record (do it in transaction)
     */
    @Override
    public void clearConnectionHistory() {
        Realm realm = allocateDb();
        realm.beginTransaction();
        realm.where(BluetoothDevice.class).findAll().clear();
        realm.commitTransaction();
        disposeOfDb(realm);
    }

    @Override
    public void addMotorcycleToHistory(BtDevice device) {
        BluetoothDevice dbDevice = convertToDbModel(device);
        Realm realm = allocateDb();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(dbDevice);
        realm.commitTransaction();
        disposeOfDb(realm);
    }

    @Override
    public boolean hasConnectionHistory() {
        Realm realm = allocateDb();
        long numberOfDevices = realm.where(BluetoothDevice.class).count();
        disposeOfDb(realm);
        return numberOfDevices != 0;
    }

    @Nullable
    @Override
    public BtDevice getLastConnectedMotorcycleInfo() {
        Realm realm = allocateDb();
        RealmResults<LastPairedDevice> stats = realm.where(LastPairedDevice.class).findAll();
        LastPairedDevice stat = stats.isEmpty() ? null : stats.get(0);
        if (stat == null || stat.getLastPairedDevice() == null){
            disposeOfDb(realm);
            return null;
        }
        BtDevice device = convertFromDbModel(stat.getLastPairedDevice());
        disposeOfDb(realm);
        return device;
    }

    @Override
    public void clearLastConnectedDeviceInfo() {
        Realm realm = allocateDb();
        RealmResults<LastPairedDevice> stat = realm.where(LastPairedDevice.class).findAll();
        if (!stat.isEmpty()){
            realm.beginTransaction();
            stat.clear();
            realm.commitTransaction();
        }
        disposeOfDb(realm);
    }

    /**
     * Add info for that device first (dao automatically updates, not adds records),
     * create and save LastDevice info, containing that device
     * @param motorcycleInfo
     */
    @Override
    public void setLastConnectedMotorcycleInfo(BtDevice motorcycleInfo) {
        Realm realm = allocateDb();
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
        disposeOfDb(realm);
    }

    @NonNull
    @Override
    public Long getLastConnectionStartTime() {
        Realm realm = allocateDb();
        RealmResults<LastPairedDevice> lastInfo =  realm.where(LastPairedDevice.class).findAll();
        if (lastInfo.isEmpty()){
            // don't forget releasing database instance
            disposeOfDb(realm);
            return null;
        }
        LastPairedDevice lastDevice = lastInfo.first();
        Long startTime = lastDevice.getPairingStartTime();

        disposeOfDb(realm);
        return startTime;
    }

    @NonNull
    @Override
    public Long getLastConnectionEndTime() {
        Realm realm = allocateDb();
        RealmResults<LastPairedDevice> lastInfo =  realm.where(LastPairedDevice.class).findAll();
        if (lastInfo.isEmpty()){
            // don't forget releasing database instance
            disposeOfDb(realm);
            return null;
        }
        LastPairedDevice lastDevice = lastInfo.first();
        Long endTime = lastDevice.getPairingEndTime();

        disposeOfDb(realm);
        return endTime;
    }

    @Override
    public void setLastConnectionStartTime(long startTime) {
        Realm realm = allocateDb();
        RealmResults<LastPairedDevice> lastInfo =  realm.where(LastPairedDevice.class).findAll();
        if (lastInfo.isEmpty()){
            // last device info not specified, save empty value
            realm.beginTransaction();
            realm.createObject(LastPairedDevice.class);
            realm.commitTransaction();
        }
        // lastInfo is active query ('window' to database), reuse it, there must be an item
        if (lastInfo.isEmpty()){
            disposeOfDb(realm);
            throw new RuntimeException("Realm is broken");
        }
        LastPairedDevice device = lastInfo.first();
        realm.beginTransaction();
        device.setPairingStartTime(startTime);
        realm.commitTransaction();
        disposeOfDb(realm);
    }

    @Override
    public void setLastConnectionEndTime(long endTime) {
        Realm realm = allocateDb();
        RealmResults<LastPairedDevice> lastInfo =  realm.where(LastPairedDevice.class).findAll();
        if (lastInfo.isEmpty()){
            // last device info not specified, save empty value
            realm.beginTransaction();
            realm.createObject(LastPairedDevice.class);
            realm.commitTransaction();
        }
        // lastInfo is active query ('window' to database), reuse it, there must be an item
        if (lastInfo.isEmpty()){
            disposeOfDb(realm);
            throw new RuntimeException("Realm is broken");
        }
        LastPairedDevice device = lastInfo.first();
        realm.beginTransaction();
        device.setPairingEndTime(endTime);
        realm.commitTransaction();
        disposeOfDb(realm);
    }

}
