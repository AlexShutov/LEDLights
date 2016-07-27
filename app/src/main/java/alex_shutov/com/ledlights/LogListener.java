package alex_shutov.com.ledlights;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import alex_shutov.com.ledlights.Bluetooth.BtDevice;
import alex_shutov.com.ledlights.Bluetooth.hex.BtPortListener;

/**
 * Created by lodoss on 26/07/16.
 */
public class LogListener implements BtPortListener {
    private final static String LOG_TAG = LogListener.class.getSimpleName();

    private Context context;

    public LogListener(Context context){
        this.context = context;
    }

    private void showMessage(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /** Inherited from BtPortListener */

    @Override
    public void onConnectioinFailed() {
        showMessage("Connection failed");
        Log.i(LOG_TAG, "Connection failed");
    }

    @Override
    public void onStateChanged(int state) {
        Log.i(LOG_TAG, "Bt state changed to: " + state);
    }

    @Override
    public void onStateConnected() {
        showMessage("Connected");
        Log.i(LOG_TAG, "Connected");
    }

    @Override
    public void onStateConnecting() {
        String m = "Connecting";
        Log.i(LOG_TAG, m);
        showMessage(m);
    }

    @Override
    public void onStateListening() {
        String m = "listening for connection";
        Log.i(LOG_TAG, m);
        showMessage(m);
    }

    @Override
    public void onStateIdle() {
        String m = "Bt doing nothing right now";
        showMessage(m);
        Log.i(LOG_TAG, m);
    }

    @Override
    public void onMessageRead(byte[] message, int messageSize) {
        String msg = new String(message,0,  messageSize);
        String m = "Received " + messageSize + " bytes: " + msg;
        showMessage(m);
        Log.i(LOG_TAG, m);
    }

    @Override
    public void onMessageSent() {
        String m = "Message sent";
        Log.i(LOG_TAG, m);
        showMessage(m);
    }

    @Override
    public void onDeviceConnected(BtDevice btDevice) {
        String uuid = btDevice.isSecureOperation() ? btDevice.getDeviceUuIdSecure() :
                btDevice.getDeviceUuIdInsecure();
        String m = "device connected: " +btDevice.getDeviceName() + " " + btDevice.getDeviceAddress() +
                " " + uuid + " " + btDevice.getDeviceDescription();
        Log.i(LOG_TAG, m);
        showMessage(m);
    }

    @Override
    public void onConnectionLost() {
        String m = "Connection lost";
        showMessage(m);
        Log.i(LOG_TAG, m);
    }

    @Override
    public void onPortReady() {
        String m = "Port is ready";
        Log.i(LOG_TAG, m);
        showMessage(m);
    }

    @Override
    public void onCriticalFailure(Exception e) {
        String m = "Critical failure has occured";
        Log.i(LOG_TAG, m);
        showMessage(m);
    }

}