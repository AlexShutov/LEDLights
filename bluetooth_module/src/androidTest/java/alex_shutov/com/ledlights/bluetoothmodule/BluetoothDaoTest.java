package alex_shutov.com.ledlights.bluetoothmodule;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.BtDeviceStorageManager;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDaoImpl;
import io.realm.Realm;

/**
 * Created by lodoss on 11/10/16.
 */
public class BluetoothDaoTest extends ApplicationTestCase<Application>  {
    private static final int NUMBER_OF_TEST_RECORDS = 10;

    BtDeviceStorageManager storageManager;
    private BtDeviceDaoImpl dao;


    public BluetoothDaoTest(){
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        storageManager = new BtDeviceStorageManager(getContext());
        dao = new BtDeviceDaoImpl(storageManager);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * See if Realm instance with this particular schema were created in test mode
     */
    @SmallTest
    public void testRealmInstanceWereCreated(){
        Realm realm = storageManager.allocateInstance();
        assertNotNull(realm);
        storageManager.disposeOfInstance(realm);
    }

    private List<BtDevice> generateTestSet(){
        List<BtDevice> devices = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_TEST_RECORDS; ++i){
            BtDevice device = new BtDevice();
            device.setDeviceUuIdSecure(UUID.randomUUID().toString());
            device.setDeviceUuIdInsecure(UUID.randomUUID().toString());
            device.setSecureOperation(true);
            device.setPaired(true);
            device.setDeviceDescription("Some device #" + i);
            device.setDeviceAddress("Some mac address");
            device.setDeviceName("Device #" + i);
            devices.add(device);
        }
        return devices;
    }

    /**
     * Create test records, clear database and add test data into it
     */
    private void addTestDevices(){
        dao.clearConnectionHistory();
        List<BtDevice> devices =  generateTestSet();
        for (BtDevice device : devices){
            dao.addMotorcycleToHistory(device);
        }
    }

    /**
     * Insert test set into database, check there is records, clear database and verify that
     * db has no records at all
     */
    @SmallTest
    public void testHistoryRemoval(){
        addTestDevices();
        List<BtDevice> history = dao.getDeviceHistory();
        assertEquals(NUMBER_OF_TEST_RECORDS, history.size());
        dao.clearConnectionHistory();
        history = dao.getDeviceHistory();
        assertTrue(history.isEmpty());
    }

    @SmallTest
    public void testAddingDevicesToDb(){
        addTestDevices();
        List<BtDevice> savedDevices = dao.getDeviceHistory();
        // check if size of query (all records) the same as number of initially created records
        assertEquals(NUMBER_OF_TEST_RECORDS, savedDevices.size());
    }

    @SmallTest
    public void testHasConnectionHistoryMethod(){
        addTestDevices();
        boolean hasRecords = dao.hasConnectionHistory();
        assertTrue(hasRecords);
        dao.clearConnectionHistory();
        hasRecords = dao.hasConnectionHistory();
        assertFalse(hasRecords);
    }

    @SmallTest
    public void testLastDeviceAddAndRemoveMethods(){
        List<BtDevice> set1 = generateTestSet();
        for (BtDevice device : set1){
            dao.addMotorcycleToHistory(device);
        }
        dao.clearLastConnectedDeviceInfo();
        // there must not be last device saved
        BtDevice lastDevice = dao.getLastConnectedMotorcycleInfo();
        assertNull(lastDevice);
        // add device
        BtDevice device = set1.get(0);
        dao.setLastConnectedMotorcycleInfo(device);
        // query last device info we just saved
        lastDevice = dao.getLastConnectedMotorcycleInfo();
        assertNotNull(lastDevice);
        assertEquals(device.getDeviceUuIdSecure(), lastDevice.getDeviceUuIdSecure());
        assertEquals(device.getDeviceUuIdInsecure(), lastDevice.getDeviceUuIdInsecure());
    }

    @SmallTest
    public void testLastConnectionStartTime(){
        dao.clearLastConnectedDeviceInfo();
        Long startTime = dao.getLastConnectionStartTime();
        Long endTime = dao.getLastConnectionEndTime();
        assertNull(startTime);
        // add test device data
        List<BtDevice> set1 = generateTestSet();
        for (BtDevice device : set1){
            dao.addMotorcycleToHistory(device);
        }
        BtDevice device = set1.get(0);
        Long sTime = System.currentTimeMillis();
        Long eTime = sTime + 1000;

        // check setting time without last device info set
        dao.clearLastConnectedDeviceInfo();
        dao.setLastConnectionStartTime(sTime);
        dao.setLastConnectionEndTime(eTime);
        startTime = dao.getLastConnectionStartTime();
        endTime = dao.getLastConnectionEndTime();
        assertNotNull(startTime);
        assertNotNull(endTime);
        assertEquals(sTime, startTime);
        assertEquals(eTime, endTime);
        // now set last device first and then test setting time
        dao.clearLastConnectedDeviceInfo();
        dao.setLastConnectedMotorcycleInfo(device);
        dao.setLastConnectionStartTime(sTime);
        dao.setLastConnectionEndTime(eTime);
        startTime = dao.getLastConnectionStartTime();
        endTime = dao.getLastConnectionEndTime();
        assertNotNull(startTime);
        assertNotNull(endTime);
        assertEquals(sTime, startTime);
        assertEquals(eTime, endTime);
    }
}
