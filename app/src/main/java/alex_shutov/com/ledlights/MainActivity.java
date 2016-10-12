package alex_shutov.com.ledlights;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.BluetoothChatService;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.bluetooth_devices.dao.BtDeviceDao;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.hex.BtStoragePort;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // my Samsung Galaxy Tab 3 - initiates connection
    private static final String ADDRESS_TABLET = "18:1E:B0:52:42:AD";

    private static final String ADDRESS_NEXUS = "B0:EC:71:D9:BD:E9";
    // chinese phone
    private static final String ADDRESS_MI = "A0:86:C6:8F:73:1A";

    private static final String ADDRESS_GALAXY_S5 = "68:05:71:83:07:B3";
    private static final String NAME_GALAXY_S_5 = "Galaxy S5";

    private static final String ADDRESS_MY_PHONE = "A0:F4:50:9E:29:5C";
    private static final String NAME_MY_PHONE = "Desire C";

    // test hc05 adapter address
    private static final String ADDRESS_HC_05 = "98:D3:31:20:A0:08";

    LEDApplication app;

    private BtConnPort btPort;

    private String deviceName = "";
    private String deviceAddress = "";
    private String deviceUuidSecure = "";
    private String deviceUuidInsecure = "";

    private void chooseNexus(){
        deviceName = "Nexus One";
        deviceAddress = ADDRESS_NEXUS;
        deviceUuidSecure = BluetoothChatService.MY_UUID_SECURE.toString();
        deviceUuidInsecure = BluetoothChatService.MY_UUID_INSECURE.toString();
        Log.i(LOG_TAG, deviceName + " selected");
    }
    private void chooseMI(){
        deviceName = "chenese phone";
        deviceAddress = ADDRESS_MI;
        deviceUuidSecure = BluetoothChatService.MY_UUID_SECURE.toString();
        deviceUuidInsecure = BluetoothChatService.MY_UUID_INSECURE.toString();
        Log.i(LOG_TAG, deviceName + " selected");
    }
    private void chooseDesireC(){
        deviceName = NAME_MY_PHONE;
        deviceAddress = ADDRESS_MY_PHONE;
        deviceUuidSecure = BluetoothChatService.MY_UUID_SECURE.toString();
        deviceUuidInsecure = BluetoothChatService.MY_UUID_INSECURE.toString();
        Log.i(LOG_TAG, deviceName + " selected");
    }
    private void chooseHc05(){
        deviceName = "HC-05";
        deviceAddress = ADDRESS_HC_05;
        deviceUuidSecure = BluetoothChatService.HC_05_UUID.toString();
        deviceUuidInsecure = BluetoothChatService.HC_05_UUID.toString();
        Log.i(LOG_TAG, deviceName + " selected");
    }

    private void showToast(String msg){
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private Subscription sendingSubscription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (LEDApplication) getApplication();
        btPort = app.getBtConnPort();


        chooseNexus();


        // setup scanner 'test'
        Button btn = (Button) findViewById(R.id.btn_bt_scan);
        btn.setOnClickListener(v -> {
            scanDevices();
        });


        btn = (Button) findViewById(R.id.btn_bt_accept);
        btn.setOnClickListener(v -> {
            btPort.close();
            btPort.setUuidSecure(BluetoothChatService.MY_UUID_SECURE.toString());
            btPort.setUuidInsecure(BluetoothChatService.MY_UUID_INSECURE.toString());
            btPort.startListening();
        });
        // setup 'stop accept insecure' button
        btn = (Button) findViewById(R.id.btn_bt_stop_accepting);
        btn.setOnClickListener(v -> {
            btPort.close();
        });


        // setup 'connect insecure' button
        btn = (Button) findViewById(R.id.btn_bt_connect_insecure);
        btn.setOnClickListener(v -> {
            connectToDevice(false);
        });
        // setup 'connect secure' button
        btn = (Button) findViewById(R.id.btn_bt_connect_secure);
        btn.setOnClickListener(v -> {
            connectToDevice(true);
        });

/**
        // setup 'stop connect' button
        btn = (Button) findViewById(R.id.btn_bt_stop_connecting);
        btn.setOnClickListener(v -> {
            connPort.stopConnecting();
        });
*/

        // setup 'Close port' button
        btn = (Button) findViewById(R.id.btn_bt_close_BT_port);
        btn.setOnClickListener(v -> {
            btPort.close();
        });

        sendingSubscription = null;

        // setup 'Send data' button
        btn = (Button) findViewById(R.id.btn_bt_send_data);
        btn.setOnClickListener(v -> {
            sendingSubscription =
                    Observable.interval(50, TimeUnit.MILLISECONDS)
                    .map(cnt -> {
                        if (cnt % 2 == 0){
                            sendColorToDevice(255, 255, 255);
                        } else {
                            sendColorToDevice(0, 0, 0);
                        }
                        return cnt;
                    })
                    .subscribe(cnt -> {

                    }, error -> {

                    });
        });

        // setup 'Send data' button
        btn = (Button) findViewById(R.id.btn_bt_send_data2);
        btn.setOnClickListener(v -> {
            if (null != sendingSubscription && !sendingSubscription.isUnsubscribed()){
                sendingSubscription.unsubscribe();
            }
        });


        Button btnPhone = (Button) findViewById(R.id.btn_phone_nexus);
        btnPhone.setOnClickListener(v -> {
            chooseNexus();
        });
        btnPhone = (Button) findViewById(R.id.btn_phone_mi);
        btnPhone.setOnClickListener(v -> {
            chooseMI();
        });
        btnPhone = (Button) findViewById(R.id.btn_hc05);
        btnPhone.setOnClickListener(v -> {
            chooseHc05();
        });
        btnPhone = (Button) findViewById(R.id.btn_phone_desire);
        btnPhone.setOnClickListener(v -> {
            chooseDesireC();
        });

        btn = (Button) findViewById(R.id.btn_bt_test_db);
        btn.setOnClickListener(v -> {
            testDatabase();
        });

    }

    private void sendColorToDevice(int red, int green, int blue) {
        if (!btPort.isBtConnected()) {
            showToast("Bluetooth device not connected");
            return;
        }
        Observable.defer(() -> Observable.just(""))
                .subscribeOn(Schedulers.computation())
                .map(t -> {
                    byte[] bytes = new byte[7];
                    bytes[0] = '!';
                    bytes[1] = 0;
                    bytes[2] = 3;
                    bytes[3] = '\n';
                    bytes[4] = (byte) red;
                    bytes[5] = (byte) green;
                    bytes[6] = (byte) blue;
                    return bytes;
                })
                .observeOn(Schedulers.io())
                .subscribe(d -> {
                    btPort.writeBytes(d);
                });
    }

    void scanDevices(){
        app.getBtScanPort().getPairedDevices();
        /**
        //btScanner.getPairedevices();
        btScanner.getPairedDevices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sd -> {
                    String message = "Found " + sd.size() + " paired devices:";
                    showToast(message);
                    for (BluetoothDevice device : sd){
                        message = "device found: " + device.getName() + " " +
                                device.getAddress();
                        showToast(message);
                    }
        });

        btScanner.makeDiscoverable();
        btScanner.startDiscovery()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(device -> {
                   Toast.makeText(MainActivity.this, "paired device found: " + device.getName() + " " +
                   device.getAddress(), Toast.LENGTH_SHORT).show();
                }, e -> {}, () -> {
                    Toast.makeText(MainActivity.this, "Completed", Toast.LENGTH_SHORT).show();
                });
         */
    }

    void connectToDevice(boolean isSecure){
        BtDevice device = new BtDevice();
        device.setDeviceName(deviceName);
        device.setDeviceAddress(deviceAddress);
        // use UUID for application

        device.setDeviceUuIdSecure(deviceUuidSecure);
        device.setDeviceUuIdInsecure(deviceUuidInsecure);
        device.setSecureOperation(isSecure);
        device.setDeviceDescription("device to connect");

        btPort.connect(device);
    }

    @Override
    protected void onDestroy() {
        /**
        if (null != subscriptionPaired && !subscriptionPaired.isUnsubscribed()){
            subscriptionPaired.unsubscribe();
        }
         */
        super.onDestroy();
    }

    private void testDatabase(){
        BtStoragePort dbPort = app.getDbPort();
        BtDeviceDao deviceDao = dbPort.getHistoryDatabase();
        List<BtDevice> history = deviceDao.getDeviceHistory();
        BtDevice device = new BtDevice();
        chooseDesireC();
        device.setDeviceName(deviceName);
        device.setDeviceAddress(deviceAddress);
        device.setDeviceUuIdSecure(deviceUuidSecure);
        device.setDeviceUuIdInsecure(deviceUuidInsecure);
        device.setDeviceDescription("HTC desire C");
        deviceDao.addMotorcycleToHistory(device);
        chooseHc05();
        device.setDeviceName(deviceName);
        device.setDeviceAddress(deviceAddress);
        device.setDeviceUuIdSecure(deviceUuidSecure);
        device.setDeviceUuIdInsecure(deviceUuidInsecure);
        device.setDeviceDescription("Arduino HC05 Bluetooth module");
        deviceDao.addMotorcycleToHistory(device);
        chooseMI();
        device.setDeviceName(deviceName);
        device.setDeviceAddress(deviceAddress);
        device.setDeviceUuIdSecure(deviceUuidSecure);
        device.setDeviceUuIdInsecure(deviceUuidInsecure);
        device.setDeviceDescription("Chinese MI phone");
        deviceDao.addMotorcycleToHistory(device);
        chooseNexus();
        device.setDeviceName(deviceName);
        device.setDeviceAddress(deviceAddress);
        device.setDeviceUuIdSecure(deviceUuidSecure);
        device.setDeviceUuIdInsecure(deviceUuidInsecure);
        device.setDeviceDescription("Samsung Galaxy phone");
        deviceDao.addMotorcycleToHistory(device);

        history = deviceDao.getDeviceHistory();
        showToast("History has " + history.size() + " devices");

    }

}
