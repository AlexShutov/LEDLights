package alex_shutov.com.ledlights.bluetooth.BtScannerPort;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by lodoss on 30/06/16.
 */
public class BTDeviceScanner {
    private static final String LOG_TAG = BTDeviceScanner.class.getSimpleName();

    private Context context;
    private BluetoothAdapter btAdapter;

    private DeviceReceiver deviceReceiver;
    private PublishSubject<Set<BluetoothDevice>> sourcePairedDevices;
    private boolean isReceiverRegistered;

    public BTDeviceScanner(Context context){
        this.context = context;
        init();
    }

    private void init(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceReceiver = new DeviceReceiver();
        sourcePairedDevices = PublishSubject.create();
        isReceiverRegistered = false;
    }

    public boolean isBluetoothEnabled(){
        return null != btAdapter && btAdapter.isEnabled();
    }

    /**
     * Turn Bluetooth ON or throw exception otherwise. It requires
     * BLUETOOTH_ADMIN permission
     * @throws IllegalStateException
     */
    public void turnOnBluetooth() throws IllegalStateException {
        if (isBluetoothEnabled()){
            throw new IllegalStateException("Bluetooth is already ON");
        }
        btAdapter.enable();
    }

    /**
     * Turn Bluetooth FF or throw exception otherwise. It requires
     * BLUETOOTH_ADMIN permission
     * @throws IllegalStateException
     */
    public void turnOffBluetooth() throws IllegalStateException {
        if (!isBluetoothEnabled()){
            throw new IllegalStateException("Bluetooth is already OFF");
        }
        btAdapter.disable();
    }

    public Observable<Set<BluetoothDevice>> getPairedDevices(){
        Observable<Set<BluetoothDevice>> res = Observable.create(s -> {
            Set<BluetoothDevice> devices = getPairedDevicesSet();
            s.onNext(devices);
            s.onCompleted();
        });
        return Observable.defer(()-> res)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation());
    }

    public Observable<Set<BluetoothDevice>> getPairedDevicesSource(){
        return sourcePairedDevices.asObservable().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }
    /**
     * get paired devices from bt adapter
     */
    private Set<BluetoothDevice> getPairedDevicesSet() {
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        for (BluetoothDevice device : devices){
            logBluetoothDevice(device);
        }
        Log.i(LOG_TAG, "There are " + devices.size() + " paired devices");
        return devices;
    }

    /**
     * Stops device discovery if it is active and start anew. New discovered devices come to
     * DeviceReceiver BroadcastReceiver which passes those to rx chain. If user does not
     * subscribe to returned Observable, all devices found in that time will be missed.
     * @return source of discovered devices.
     */
    public Observable<BluetoothDevice> startDiscovery(){
        stopDiscovery();
        // register for receiving discovered devices
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(deviceReceiver, filter);
        // register for discovery termination event
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(deviceReceiver, filter);
        isReceiverRegistered = true;
        btAdapter.startDiscovery();
        // use existing BroadcastReceiver as subscription.
        return Observable.defer(() -> Observable.create(deviceReceiver))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation());
    }

    /**
     * cancel BT discovery and unregister BroadcastReceiver receiving newly found devices
     */
    public void stopDiscovery(){
        if (null != btAdapter && btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        }
        if (isReceiverRegistered) {
            context.unregisterReceiver(deviceReceiver);
            isReceiverRegistered = false;
        }
    }

    public void makeDiscoverable(){
        if (btAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(discoverableIntent);
        }
    }

    /**
     * Print all bluetooth device info into log
     * @param device
     */
    private void logBluetoothDevice(BluetoothDevice device){
        Log.i(LOG_TAG, "-----------------------------------------");
        String deviceName = device.getName();
        Log.i(LOG_TAG, "Device name: " + deviceName);
        String deviceAddress = device.getAddress();
        Log.i(LOG_TAG, "Device address: " + deviceAddress);
        String bluetoothClass = device.getBluetoothClass().toString();
        Log.i(LOG_TAG, "Device bluetooth class: " + bluetoothClass);
    }

    /**
     * Receiver, which handle all discovered devices. We need only not paired devices, because we
     * can query paired devices explicitly and try to connect one of those.
     * This class implement Observable.OnSubscribe<BluetoothDevice> for passing discovered
     * devices down to rx chain.
     * 'drain'- chain input. drain = null if we scheduled discovery and saved source Observable's
     * reference, but by some reason did not subscribed to it yet.
     */
    class DeviceReceiver extends BroadcastReceiver implements Observable.OnSubscribe<BluetoothDevice> {

        Subscriber<? super BluetoothDevice> drain;
        @Override
        public void call(Subscriber<? super BluetoothDevice> subscriber) {
            drain = subscriber;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // if receiver received discovery notification
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // we need only not paired devices, paired ones it returned by different method
                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    // handle device
                    Log.i(LOG_TAG, "-----------------------------------------");
                    Log.i(LOG_TAG, "discovered not paired device ");
                    logBluetoothDevice(device);
                    if (null != drain) {
                        drain.onNext(device);
                    }
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Log.i(LOG_TAG, "discovery finished");
                if (btAdapter.isDiscovering()){
                    Log.i(LOG_TAG, "Disabling discovery from broadcast receiver");
                }
                if (null != drain) {
                    drain.onCompleted();
                    // operation complete, we don't need it anymore
                    drain = null;
                }
            }
        }
    }
}
