package alex_shutov.com.ledlights;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

import alex_shutov.com.ledlights.Bluetooth.BTDeviceScanner;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    private BTDeviceScanner btScanner;
    Subscription subscriptionPaired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LEDApplication app = (LEDApplication) getApplication();

        btScanner = app.getDeviceScanner();

        Observable<Set<BluetoothDevice>> pairedDevicesSrc = btScanner.getPairedDevicesSource();
        subscriptionPaired = pairedDevicesSrc
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(devices -> {
            Toast.makeText(MainActivity.this, "There are " + devices.size() + " devices", Toast.LENGTH_SHORT).show();

        });

        Button btn = (Button) findViewById(R.id.btnTest);
        btn.setOnClickListener(v -> {
            scanDevices();
        });

    }

    void scanDevices(){

        //btScanner.getPairedevices();

        btScanner.getPairedDevices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sd -> {
                    Toast.makeText(MainActivity.this, "Found " + sd.size() + " paired devices",
                            Toast.LENGTH_SHORT).show();
        });

        btScanner.makeDiscoverable();
        btScanner.startDiscovery()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(device -> {
                   Toast.makeText(MainActivity.this, "device found: " + device.getName() + " " +
                   device.getAddress(), Toast.LENGTH_SHORT).show();
                }, e -> {}, () -> {
                    Toast.makeText(MainActivity.this, "Completed", Toast.LENGTH_SHORT).show();
                });

    }


    @Override
    protected void onDestroy() {
        if (null != subscriptionPaired && !subscriptionPaired.isUnsubscribed()){
            subscriptionPaired.unsubscribe();
        }
        super.onDestroy();
    }
}
