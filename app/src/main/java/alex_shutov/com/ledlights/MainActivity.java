package alex_shutov.com.ledlights;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import alex_shutov.com.ledlights.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetooth.BtConnectorPort.hex.BtConnPort;
import rx.Observable;
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
        deviceUuidSecure = LEDApplication.MY_UUID_SECURE.toString();
        deviceUuidInsecure = LEDApplication.MY_UUID_INSECURE.toString();
        Log.i(LOG_TAG, deviceName + " selected");
    }
    private void chooseMI(){
        deviceName = "chenese phone";
        deviceAddress = ADDRESS_MI;
        deviceUuidSecure = LEDApplication.MY_UUID_SECURE.toString();
        deviceUuidInsecure = LEDApplication.MY_UUID_INSECURE.toString();
        Log.i(LOG_TAG, deviceName + " selected");
    }
    private void chooseDesireC(){
        deviceName = NAME_MY_PHONE;
        deviceAddress = ADDRESS_MY_PHONE;
        deviceUuidSecure = LEDApplication.MY_UUID_SECURE.toString();
        deviceUuidInsecure = LEDApplication.MY_UUID_INSECURE.toString();
        Log.i(LOG_TAG, deviceName + " selected");
    }
    private void chooseHc05(){
        deviceName = "HC-05";
        deviceAddress = ADDRESS_HC_05;
        deviceUuidSecure = LEDApplication.HC_05_UUID.toString();
        deviceUuidInsecure = LEDApplication.HC_05_UUID.toString();
        Log.i(LOG_TAG, deviceName + " selected");
    }

    private void showToast(String msg){
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (LEDApplication) getApplication();
        btPort = app.getBtPort();


        chooseNexus();


        // setup scanner 'test'
        Button btn = (Button) findViewById(R.id.btn_bt_scan);
        btn.setOnClickListener(v -> {
            scanDevices();
        });


        btn = (Button) findViewById(R.id.btn_bt_accept);
        btn.setOnClickListener(v -> {
            btPort.close();
            btPort.setUuidSecure(LEDApplication.MY_UUID_SECURE.toString());
            btPort.setUuidInsecure(LEDApplication.MY_UUID_INSECURE.toString());
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
            btPort.stopConnecting();
        });
*/

        // setup 'Close port' button
        btn = (Button) findViewById(R.id.btn_bt_close_BT_port);
        btn.setOnClickListener(v -> {
            btPort.close();
        });

        // setup 'Send data' button
        btn = (Button) findViewById(R.id.btn_bt_send_data);
        btn.setOnClickListener(v -> {
            if (!btPort.isBtConnected()){
                showToast("Bluetooth device not connected");
                return;
            }
            String msg = "Hello";
           // byte[] bytes = msg.getBytes();
            byte[] bytes = new byte[100];
            bytes[0] = '!';
            bytes[1] = 0;
            bytes[2] = 3;
            bytes[3] = '\n';
            bytes[4] = (byte)255;
            bytes[5] = (byte) 0;
            bytes[6] = (byte) 0;

            Observable.defer(() -> Observable.just(bytes))
                    .subscribeOn(Schedulers.io())
                    .subscribe( d -> {
                        btPort.writeBytes(d);
                    });
            //btPort.writeBytes(bytes);
        });

        // setup 'Send data' button
        btn = (Button) findViewById(R.id.btn_bt_send_data2);
        btn.setOnClickListener(v -> {
            if (!btPort.isBtConnected()){
                showToast("Bluetooth device not connected");
                return;
            }
            String msg = "Hello";
            // byte[] bytes = msg.getBytes();
            byte[] bytes = new byte[100];
            bytes[0] = '!';
            bytes[1] = 0;
            bytes[2] = 3;
            bytes[3] = '\n';
            bytes[4] = (byte) 30;
            bytes[5] = (byte) 150;
            bytes[6] = (byte) 200;

            Observable.defer(() -> Observable.just(bytes))
                    .subscribeOn(Schedulers.io())
                    .subscribe( d -> {
                        btPort.writeBytes(d);
                    });
            //btPort.writeBytes(bytes);
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
}
