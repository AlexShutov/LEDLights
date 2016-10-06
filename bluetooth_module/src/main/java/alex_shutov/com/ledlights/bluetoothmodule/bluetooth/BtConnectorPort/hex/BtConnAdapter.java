package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.BluetoothChatService;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.Constants;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.hex_general.Adapter;
import alex_shutov.com.ledlights.hex_general.PortInfo;
import alex_shutov.com.ledlights.hex_general.PortListener;

/**
 * Created by Alex on 7/25/2016.
 */
public class BtConnAdapter extends Adapter implements BtConnPort {
    private static final String LOG_TAG = BtConnAdapter.class.getSimpleName();
    private static final String DISPATCHER_THREAD_NAME = "bluetooth_comm_dispatch_thread";
    /** PortInfo instance used by logic for checking port compatibility */
    private static final PortInfo portInfo;
    static {
        portInfo = new PortInfo();
        portInfo.setPortCode(PortInfo.PORT_BLUETOOTH_CONNECTOR);
        portInfo.setPortDescription("Port for communicating with bluetooth devices");
    }

    /**
     * Bluetooth is platform dependent
     */
    private Context context;
    /**
     * little modifier=d class from 'BluetoothChat' google sample,
     * managing Bluetooth connections as well as sending and receiving data.
     */
    private BluetoothChatService btService;
    /**
     * BluetoothChatService work with BluetoothDevice, so we need BluetoothAdapter to create one
     */
    private BluetoothAdapter btAdapter;

    /**
     * BluetoothChatService use Android's Handler class to send message to the rest of a
     * program - I want to use my own BtListener interface instead. But, those messages
     * should not be transferred or handled on any of BluetoothChatService's thread -
     * 'accept, connect, connected'. Solution - create another thread, which will listen
     * for any event and dispatch that event to BtListener. Here I use HandlerThread for
     * handling all messages consequently.
     * We first create DispatcherThread and then it initializes commHandler when its Looper is ready.
     */
    private HandlerThread dispatcherThread;
    private DispatcherHandler dispatcherHandler;

    public BtConnAdapter(Context context){
        super();
        this.context = context;
    }

    /**
     * Setup BluetooothChatService, dispatcher thread and DispatcherHandler.
     * Notice, we don't call 'startListening()' method here, because it need further
     * initialization - UUIDs for secure and insecure modes
     * We don't use DI in with this port so there is no use for
     */
    @Override
    public void initialize(){
        /** it will be initialized within Dispatcher thread (when Looper is ready)  */
        dispatcherHandler = null;
        dispatcherThread = new HandlerThread(DISPATCHER_THREAD_NAME);
        dispatcherThread.start();
        /** use Looper instance from dispatcher thread */
        dispatcherHandler = new DispatcherHandler(dispatcherThread.getLooper());
        btService = new BluetoothChatService(context, dispatcherHandler);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        /**
         * Inform listener that port is ready
         */
        PortListener listener = getPortListener();
        if (null != listener){
            listener.onPortReady(getPortInfo().getPortCode());
        } else {
            /** this is not 'critical failure - port will remain silent until we set
             * listener
             */
            Log.e(LOG_TAG, "Port listener reference is null during BtConnAdapter creation");
        }
    }

    @Override
    public PortInfo getPortInfo() {
        return portInfo;
    }


    @Override
    public String getUuidSecure() {
        String uuidSecure = btService.getUuidSecure();
        return uuidSecure;
    }

    /**
     * Method for checking if Bluetooth is busy right now. It is used in accessors for
     * BluetoothChatService - we cannot change device name or uuid if discovery (conection) is
     * active. If it is - it throw an exception. In that case stop service first before
     * using those accessors
     * @throws IllegalStateException
     */

    @Override
    public void setUuidSecure(String uuidSecure) throws IllegalStateException {
        btService.setUuidSecure(uuidSecure);
    }

    @Override
    public String getUuidInsecure() {
        String uuidInsecure = btService.getUuidInsecure();
        return uuidInsecure;
    }

    @Override
    public void setUuidInsecure(String uuidInsecure) throws IllegalStateException {
        btService.setUuidInsecure(uuidInsecure);
    }

    @Override
    public String getNameInsecure() {
        String nameInsecure = btService.getNameInsecure();
        return nameInsecure;
    }

    @Override
    public void setNameInsecure(String nameInsecure) throws IllegalStateException {
        btService.setNameInsecure(nameInsecure);
    }

    @Override
    public String getNameSecure() {
        String nameSecure = btService.getNameSecure();
        return nameSecure;
    }

    @Override
    public void setNameSecure(String nameInsecure) throws IllegalStateException {
        btService.setNameSecure(nameInsecure);
    }

    @Override
    public void startListening() {
        btService.start();
    }


    @Override
    public void close() {
        Log.i(LOG_TAG, "Stopping Bluetooth service");
        btService.stop();
    }

    @Override
    public boolean isBtConnected() {
         return btService.getState() == BluetoothChatService.STATE_CONNECTED;
    }

    @Override
    public void connect(BtDevice device) {
        String address = device.getDeviceAddress();
        if (null == address || address.equals("")){
            Log.e(LOG_TAG, "Address for device: " + device.getDeviceName() + " is not set");
        }
        BluetoothDevice androidBT = btAdapter.getRemoteDevice(device.getDeviceAddress());
        if (null == androidBT){
            Log.e(LOG_TAG, "Error while getting bt device: " + device.getDeviceName());
            return;
        }
        /**
         * We use BtDevice to set device name and UUID first and then BluetoothDevice
         * to trigger connection in BluetoothChatService
         */
        try {
            setDeviceAttributes(device);
        } catch (IllegalStateException e){
            Log.w(LOG_TAG, "Trying to initiate connection while bt adapter is busy. " +
                    "Aborting all current tasks");
            btService.stop();
            try {
                setDeviceAttributes(device);
            } catch (IllegalStateException e2){
                Log.e(LOG_TAG, "Failed to abort current tasks - bt adapter is still busy");
                return;
            }
        }
        if (device.isSecureOperation()){
            btService.setUuidSecure(device.getDeviceUuIdSecure());
        } else {
            btService.setUuidInsecure(device.getDeviceUuIdInsecure());
        }
        btService.connect(androidBT, device.isSecureOperation());
    }

    @Override
    public void stopConnecting() {
        if (btService.getState() != BluetoothChatService.STATE_CONNECTING){
            Log.w(LOG_TAG, "can't cancel connection because device isn't connecting");
            return;
        }
        /**
         * Here we can just suspend all activity and start accepting again
         */
        close();
        startListening();
    }

    @Override
    public void writeBytes(byte[] out) {
        btService.write(out);
    }

    /**
     * Verify that all bt device's
     * @return
     */
    private boolean checkDeviceParameters(){
        return true;
    }

    private void setDeviceAttributes(BtDevice device){
        boolean isSecure = device.isSecureOperation();

        setNameSecure(device.getDeviceName());
        setNameInsecure(device.getDeviceName());
        setUuidSecure(device.getDeviceUuIdSecure());
        setUuidInsecure(device.getDeviceUuIdInsecure());
    }

    private class DispatcherHandler extends Handler{

        public DispatcherHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            /** Assume everything is OK - no cast checking */
            BtConnPortListener feedback = (BtConnPortListener) getPortListener();
            if (null == feedback){
                Log.e(LOG_TAG, "Feedback interface is null, ignoring message");
                return;
            }
            String logMsg = "Dispatching message ";
            switch (msg.what){
                case Constants.MESSAGE_STATE_CHANGE:;
                    int newState = msg.arg1;
                    /** call general method and then parsed versions - for convenience */
                    feedback.onStateChanged(newState);
                    switch (newState){
                        case BluetoothChatService.STATE_CONNECTED:
                            logMsg += " device connected";
                            feedback.onStateConnected();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            logMsg += " device connecting";
                            feedback.onStateConnecting();
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            logMsg += " adapter is listning for incoming connections";
                            feedback.onStateListening();
                            break;
                        case BluetoothChatService.STATE_NONE:
                            logMsg += " adapter is IDLE";
                            feedback.onStateIdle();
                            break;
                    }
                    Log.d(LOG_TAG, logMsg);
                    break;
                case Constants.MESSAGE_READ:
                    int messageSize = msg.arg1;
                    byte[] message = (byte[]) msg.obj;
                    Log.i(LOG_TAG, "Received " + messageSize+ " bytes in message");
                    feedback.onMessageRead(message, messageSize);
                    break;
                case Constants.MESSAGE_WRITE:
                    Log.i(LOG_TAG, "Message sent");
                    feedback.onMessageSent();
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    Bundle b = msg.getData();
                    if (!b.containsKey(Constants.DEVICE_NAME)){
                        Log.e(LOG_TAG, "Device connected, but message doesn't have device " +
                                "name");
                        return;
                    }
                    BtDevice device = new BtDevice();
                    String deviceName = b.getString(Constants.DEVICE_NAME);
                    Log.i(LOG_TAG, "Device: " + deviceName + " is connected");
                    device.setDeviceName(deviceName);
                    feedback.onDeviceConnected(device);
                    break;
                case Constants.MESSAGE_CONNECTION_FAILED:
                    Log.i(LOG_TAG, "Connectioin failed");
                    feedback.onConnectioinFailed();
                    break;
                case Constants.MESSAGE_CONNECTION_LOST:
                    Log.i(LOG_TAG, "Connection lost");
                    feedback.onConnectionLost();
                    break;
            }
        }
    }
}
