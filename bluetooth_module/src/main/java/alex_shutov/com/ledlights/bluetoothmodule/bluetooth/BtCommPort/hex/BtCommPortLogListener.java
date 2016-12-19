package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex;

import android.util.Log;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;

/**
 * Created by lodoss on 12/10/16.
 */
public class BtCommPortLogListener implements BtCommPortListener{
    private static final String LOG_TAG = BtCommPortLogListener.class.getSimpleName();


    @Override
    public void onConnectionStarted(BtDevice btDevice) {
        Log.w(LOG_TAG, "onConnectionStarted()");
    }

    @Override
    public void onConnectionFailed() {
        Log.w(LOG_TAG, "onConnectionFailed()");
    }

    @Override
    public void onDataSent() {
        Log.w(LOG_TAG, "onDataSent()");
    }

    @Override
    public void onDataSendFailed() {
        Log.w(LOG_TAG, "onDataSendFailed()");
    }

    @Override
    public void receiveData(byte[] data) {

    }

    @Override
    public void onReconnected(BtDevice btDevice) {
        Log.w(LOG_TAG, "onReconnected()");
    }

    @Override
    public void onDummyDeviceSelected() {
        Log.w(LOG_TAG, "onDummyDeviceSelected()");
    }

    @Override
    public void onReconnectAttemptFailed() {
        Log.w(LOG_TAG, "onReconnectAttemptFailed()");
    }

    @Override
    public void onPortReady(int portID) {
        Log.w(LOG_TAG, "onPortReady(" + portID + ")");
    }

    @Override
    public void onCriticalFailure(int portID, Exception e) {
        Log.w(LOG_TAG, "onCriticalFailure(" + portID + " " + e.getMessage() + ")");
    }
}
