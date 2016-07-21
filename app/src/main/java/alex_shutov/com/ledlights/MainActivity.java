package alex_shutov.com.ledlights;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

import alex_shutov.com.ledlights.Bluetooth.BTConnector;
import alex_shutov.com.ledlights.Bluetooth.BTDeviceScanner;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // my Samsung Galaxy Tab 3 - initiates connection
    private static final String ADDRESS_TABLET = "18:1E:B0:52:42:AD";

    private static final String ADDRESS_NEXUS = "B0:EC:71:D9:BD:E9";
    // test phone from work (Xiaomi MI) - accept connection
    private static final String ADDRESS_PHONE = "A0:86:C6:8F:73:1A";
    // test hc05 adapter address
    private static final String HC_05 = "98:D3:31:20:A0:08";

    private BTDeviceScanner btScanner;
    private BTConnector btConnector;

    Subscription subscriptionPaired;

    private void showToast(String msg){
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LEDApplication app = (LEDApplication) getApplication();

        btScanner = app.getDeviceScanner();
        btConnector = app.getBtConnector();

        Observable<Set<BluetoothDevice>> pairedDevicesSrc = btScanner.getPairedDevicesSource();
        subscriptionPaired = pairedDevicesSrc
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(devices -> {
            Toast.makeText(MainActivity.this, "There are " + devices.size() + " devices", Toast.LENGTH_SHORT).show();

        });

        // setup scanner 'test'
        Button btn = (Button) findViewById(R.id.btn_bt_scan);
        btn.setOnClickListener(v -> {
            scanDevices();
        });

        // setup 'accept insecure' for connector
        btn = (Button) findViewById(R.id.btn_bt_accept);
        btn.setOnClickListener(v -> {

        });
        // setup 'stop accept insecure' button
        btn = (Button) findViewById(R.id.btn_bt_stop_accepting);
        btn.setOnClickListener(v -> {

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


        // setup 'stop connect insecure' button
        btn = (Button) findViewById(R.id.btn_bt_stop_conn_insecure);
        btn.setOnClickListener(v -> {


        });



    }

    void scanDevices(){

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
    }

    void connectToDevice(boolean isSecure){
        BluetoothDevice device = null;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            device = adapter.getRemoteDevice(ADDRESS_NEXUS);
        } catch (IllegalArgumentException e){
        }
        Toast.makeText(this, "Device exist", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (null != subscriptionPaired && !subscriptionPaired.isUnsubscribed()){
            subscriptionPaired.unsubscribe();
        }
        super.onDestroy();
    }
}
