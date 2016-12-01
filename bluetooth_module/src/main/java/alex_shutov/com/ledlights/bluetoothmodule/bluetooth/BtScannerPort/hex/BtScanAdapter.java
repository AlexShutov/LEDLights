package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDeviceConverter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.BTDeviceScanner;
import alex_shutov.com.ledlights.hex_general.Adapter;
import alex_shutov.com.ledlights.hex_general.PortInfo;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by lodoss on 27/07/16.
 */
public class BtScanAdapter extends Adapter implements BtScanPort {
    private static final String LOG_TAG = BtScanAdapter.class.getSimpleName();
    private static final PortInfo portInfo;
    static {
        portInfo = new PortInfo();
        portInfo.setPortCode(PortInfo.PORT_BLUETOOTH_SCANNER);
        portInfo.setPortDescription("Port for scanning new and paired BT devices");
    }

    /**
     * Bluetooth is platform dependent
     */
    private Context context;
    /**
     * BluetoothChatService work with BluetoothDevice, so we need BluetoothAdapter to create one
     */
    private BluetoothAdapter btAdapter;

    /** Entity, doing actual work */
    BTDeviceScanner btScanner;
    /** This subscripton is active between requesting paired devices and
     * getting respoonse. Subscription is cancelled when results arrive
     */
    Subscription pairedDevicesSubscription;

    /**
     * Connects BTDeviceScanner and BtScanAdapter during scan process. It is cancelled on
     * scan completion.
     */
    Subscription discoverySubscription;

    public BtScanAdapter(Context context){
        super();
        this.context = context;
    }

    /**
     * TODO:
     */
    @Override
    public void initialize() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btScanner = new BTDeviceScanner(context);
    }

    @Override
    public PortInfo getPortInfo() {
        return portInfo;
    }

    /**  Inherited from BtScanPort */

    @Override
    public boolean isBluetoothEnabled() {
        boolean isEnabled = btScanner.isBluetoothEnabled();
        Log.w(LOG_TAG, "isBluetooth enabled? " + isEnabled);
        return isEnabled;
    }

    @Override
    public void turnOnBluetooth() throws IllegalStateException {
        try {
            btScanner.turnOnBluetooth();
        } catch (IllegalStateException e){
            Log.w(LOG_TAG, "Bluetooth is already ON");
            throw e;
        }
        Log.i(LOG_TAG, "Bluetooth turned ON");
    }

    @Override
    public void turnOffBluetooth() throws IllegalStateException {
        try {
            btScanner.turnOffBluetooth();
        } catch (IllegalStateException e){
            Log.w(LOG_TAG, "Bluetooth is already OFF");
            throw e;
        }
        Log.i(LOG_TAG, "Bluetooth turned OFF");
    }

    @Override
    public void makeDeviceDiscoverable() {
        Log.i(LOG_TAG, "requesting user to make device discoverable");
        btScanner.makeDiscoverable();
    }

    @Override
    public void getPairedDevices() {
        Log.i(LOG_TAG, "Trying to get paired Bluetooth devices");
        Observable<Set<BluetoothDevice>> src = btScanner.getPairedDevices();
        subscribeToPairedDevicesSource(src);
    }

    @Override
    public void startDiscovery() {
        Log.i(LOG_TAG, "Starting scanning for bluetooth devices");
        Observable<BluetoothDevice> src = btScanner.startDiscovery();
        subscribeToDiscoverySource(src);
    }

    @Override
    public void stopDiscovery() {
        Log.i(LOG_TAG, "Cancelling bluetooth scanning");
        btScanner.stopDiscovery();
    }

    /**
     * All results arrive to BtScanPortListener on background thread
     */
    private void subscribeToPairedDevicesSource(
            Observable<Set<BluetoothDevice>> pairedDevicesSource){
        if (null != pairedDevicesSubscription && ! pairedDevicesSubscription.isUnsubscribed()){
            Log.w(LOG_TAG, "Paired devices is already requested, resetting previous request");
            pairedDevicesSubscription.unsubscribe();
            pairedDevicesSubscription = null;
        }
        BtScanPortListener listener = (BtScanPortListener) getPortListener();
        pairedDevicesSubscription =
                pairedDevicesSource
                        .observeOn(Schedulers.computation())
                        .subscribe(devices -> {
                            if (devices.isEmpty()){
                                Log.i(LOG_TAG, "There is no paired devices");
                            } else {
                                Log.i(LOG_TAG, "Got " + devices.size() + " paired devices");
                            }
                            Set<BtDevice> btDevices = new HashSet<BtDevice>();
                            for (BluetoothDevice d : devices){
                                BtDevice converted = BtDeviceConverter.fromAndroidBluetoothDevice(d);
                                /** mark converted value as paired device */
                                converted.setPaired(true);
                                btDevices.add(converted);
                            }
                            listener.onPairedDevicesReceived(btDevices);
                            /** We passed result to listener, unsubscribe from result pipe */
                            pairedDevicesSubscription.unsubscribe();
                            pairedDevicesSubscription = null;
                        }, error -> {
                            Log.e(LOG_TAG, "Error getting paired devices");
                        }, () -> {
                        });
    }

    /**
     * Caller receive all discovered devices via BtScanPortListener
     * @param discoverySource
     */
    private void subscribeToDiscoverySource(Observable<BluetoothDevice> discoverySource) {
        if (null != discoverySubscription && !discoverySubscription.isUnsubscribed()){
            Log.w(LOG_TAG, "Discovery is already in progress, restarting it");
            // cancel discovery
            discoverySubscription.unsubscribe();
            discoverySubscription = null;
        }
        BtScanPortListener listener = (BtScanPortListener) getPortListener();
        discoverySubscription = discoverySource
                .observeOn(Schedulers.computation())
                .subscribe(discoveredDevice -> {
                    BtDevice d =
                            BtDeviceConverter.fromAndroidBluetoothDevice(discoveredDevice);
                    listener.onDeviceFound(d);
                }, error -> {
                    Log.e(LOG_TAG, "Error occured during scanning for bluetooth devices");
                    // consider scan complete
                    listener.onScanCompleted();
                }, () -> {
                    // discovery complete, inform listener about it
                    listener.onScanCompleted();
                });
    }
}
