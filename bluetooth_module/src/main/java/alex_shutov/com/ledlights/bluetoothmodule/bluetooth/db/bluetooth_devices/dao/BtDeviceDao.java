package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.dao;

/**
 * Created by lodoss on 11/10/16.
 */

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.db.bluetooth_devices.model.BluetoothDevice;

/**
 * Interface for accessing database with history of ever connected Bluetooth devices (motorcycles).
 * Notice, we convert internal database format into application's device desctiption format.
 * It is importaint, because database is a specific implementation,
 */

public interface BtDeviceDao {

    /**
     * @return List of all ever connected motorcycles -
     * Motorcycle is considered connected if connection has been established successfully
     * even once.
     */
    @NonNull
    List<BtDevice> getDeviceHistory();

    /**
     * Remove all motorcycles from connection history
     */
    void clearConnectionHistory();

    /**
     * Add new motorcycle info if app were never connected to that motorcycle or
     * update existing info (perhaps, something has changed, say, description).
     * This device will be having current time set as connection time.
     * There is no t
     * @param device
     */
    void addMotorcycleToHistory(BtDevice device);

    /**
     * check if history has at least one motorcycle (device).
     * @return
     */
    boolean hasConnectionHistory();

    /**
     * Get information about last connected motorcycle. Notice, this field can be empty
     * if app has not ever been connected to any motorcycle.
     * @return
     */
    @Nullable
    BtDevice getLastConnectedMotorcycleInfo();

    /**
     * Remove record about last connected device from database
     */
    void clearLastConnectedDeviceInfo();

    /**
     * Update information about last connected motorcycle
     * @param motorcycleInfo
     */
    void setLastConnectedMotorcycleInfo(BtDevice motorcycleInfo);

    /**
     * database stores time when last connection were established and time when
     * last connection was closed or lost. Return 0 if app was never connected to motorcycle
     * @return
     */
    @NonNull
    Long getLastConnectionStartTime();

    /**
     * return 0 if connection is still active or has never been established, time when
     * connection were lost otherwise
     * @return
     */
    @NonNull
    Long getLastConnectionEndTime();

    /**
     * write time when last connection were established to db. This method is called
     * after app connects to motorcycle
     * @param startTime
     */
    void setLastConnectionStartTime(long startTime);

    /**
     * Update time when last connection ended.
     * @param endTime
     */
    void setLastConnectionEndTime(long endTime);


}
