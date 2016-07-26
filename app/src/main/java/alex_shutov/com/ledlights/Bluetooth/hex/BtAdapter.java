package alex_shutov.com.ledlights.Bluetooth.hex;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import alex_shutov.com.ledlights.Bluetooth.BluetoothChatService;
import alex_shutov.com.ledlights.Bluetooth.BtDevice;
import alex_shutov.com.ledlights.Bluetooth.Constants;
import alex_shutov.com.ledlights.HexGeneral.Adapter;
import alex_shutov.com.ledlights.HexGeneral.PortInfo;
import alex_shutov.com.ledlights.HexGeneral.PortListener;

/**
 * Created by Alex on 7/25/2016.
 */
public class BtAdapter extends Adapter implements BtPort {
    private static final String LOG_TAG = BtAdapter.class.getSimpleName();
    private static final String DISPATCHER_THREAD_NAME = "bluetooth_comm_dispatch_thread";
    /** priority of a thread, dispatching messages */
    private static final PortInfo portInfo;
    static {
        portInfo = new PortInfo();
        portInfo.setPortCode(PortInfo.PORT_BLUETOOTH_RAW);
        portInfo.setPortDescription("Port for communicating with bluetooth devices");
    }

    private Context context;
    /**
     * little modifier=d class from 'BluetoothChat' google sample,
     * managing Bluetooth connections as well as sending and receiving data.
     */
    private BluetoothChatService btService;

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


    public BtAdapter(Context context) {
        super();
        this.context = context;
        initialize();
    }

    private void initialize(){
        /** it will be initialized within Dispatcher thread (when Looper is ready)  */
        dispatcherHandler = null;
        dispatcherThread = new HandlerThread(DISPATCHER_THREAD_NAME);
        /** use Looper instance from dispatcher thread */
        dispatcherHandler = new DispatcherHandler(dispatcherThread.getLooper());
        btService = new BluetoothChatService(context, dispatcherHandler);
        /**
         * Inform listener that port is ready
         */
        PortListener listener = getPortListener();
        if (null != listener){
            listener.onPortReady();
        } else {
            Log.e(LOG_TAG, "Port listener reference is null during BtAdapter creation");
        }
    }

    @Override
    public PortInfo getPortInfo() {
        return portInfo;
    }


    @Override
    public String getUuidSecure() {
        return null;
    }

    @Override
    public void setUuidSecure(String uuidSecure) throws IllegalStateException {

    }

    @Override
    public String getUuidInsecure() {
        return null;
    }

    @Override
    public void setUuidInsecure(String uuidInsecure) throws IllegalStateException {

    }

    @Override
    public String getNameInsecure() {
        return null;
    }

    @Override
    public void setNameInsecure(String nameInsecure) throws IllegalStateException {

    }

    @Override
    public void startListening() {

    }

    @Override
    public void close() {

    }

    @Override
    public void connect(BtDevice device) {

    }

    @Override
    public void writeBytes(byte[] out) {

    }

    private class DispatcherHandler extends Handler{

        public DispatcherHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            PortListener feedback = getPortListener();
            if (null == feedback){
                Log.e(LOG_TAG, "Feedback interface is null, ignoring message");
                return;
            }
            switch (msg.what){
                case Constants.MESSAGE_STATE_CHANGE:
                    break;
            }
        }
    }
}
