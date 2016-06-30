package alex_shutov.com.ledlights.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by lodoss on 30/06/16.
 */
public class BTDeviceScanner {
    private static final String LOG_TAG = BTDeviceScanner.class.getSimpleName();

    private Context context;
    private BluetoothAdapter btAdapter;

    private PublishSubject<Set<BluetoothDevice>> sourcePairedDevices;


    public BTDeviceScanner(Context context){
        this.context = context;
        init();
    }

    private void init(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        sourcePairedDevices = PublishSubject.create();
    }


    public Observable<Set<BluetoothDevice>> getPairedDevicesSource(){
        return sourcePairedDevices.asObservable().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    /**
     * get paired devices from bt adapter and pass to 'paired devices' pipe
     */
    public void getPairedevices() {
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        for (BluetoothDevice device : devices){
            logBluetoothDevice(device);
        }
        Log.i(LOG_TAG, "There are " + devices.size() + " paired devices");
        // notify source of paired devices
        sourcePairedDevices.onNext(devices);
    }

    /** Stop discovery first and then start anew */
    public void startDiscovery(){
        stopDiscovery();
        // register for receiving events when device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(deviceDiscoveryReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(deviceDiscoveryReceiver, filter);
    }

    /**
     * cancel BT discovery and unregister BroadcastReceiver receiving newly found devices
     */
    public void stopDiscovery(){
        if (null != btAdapter){
            btAdapter.cancelDiscovery();
        }
        context.unregisterReceiver(deviceDiscoveryReceiver);
    }


    /**
     * Print all bluetooth device info into log
     * @param device
     */
    private void logBluetoothDevice(BluetoothDevice device){
        Log.i(LOG_TAG, "-----------------------------------------");
        Log.i(LOG_TAG, "Device name: " + device.getName());
        Log.i(LOG_TAG, "Device address: " + device.getAddress());
        Log.i(LOG_TAG, "Device bluetooth class: " + device.getBluetoothClass().toString());
    }

    private void turnOnBluetooth(){}

    private BroadcastReceiver deviceDiscoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // if receiver received discovery notification
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // we need only not paired devices, paired ones it returned by different method
                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    // handle device
                    Log.i(LOG_TAG, "discovered not paired device: " + device.getName() + " "
                    + device.getAddress());
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Log.i(LOG_TAG, "discovery finished");
            }
        }
    };
}
