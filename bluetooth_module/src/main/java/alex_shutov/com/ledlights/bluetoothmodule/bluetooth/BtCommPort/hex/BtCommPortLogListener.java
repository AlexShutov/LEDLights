package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex;

import android.util.Log;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.hex_general.common.utils.impl.LogUtils;

import static alex_shutov.com.ledlights.hex_general.common.utils.impl.LogUtils.*;

/**
 * Created by lodoss on 12/10/16.
 */
public class BtCommPortLogListener implements BtCommPortListener{
    private static final String LOG_TAG = BtCommPortLogListener.class.getSimpleName();


    @Override
    public void onConnectionStarted(BtDevice btDevice) {
        LOGW(LOG_TAG, "onConnectionStarted()");
    }

    @Override
    public void onConnectionFailed() {
        LOGW(LOG_TAG, "onConnectionFailed()");
    }

    @Override
    public void onDataSent() {
        LOGW(LOG_TAG, "onDataSent()");
    }

    @Override
    public void onDataSendFailed() {
        LOGW(LOG_TAG, "onDataSendFailed()");
    }

    @Override
    public void receiveData(byte[] data, int size) {

    }

    @Override
    public void onReconnected(BtDevice btDevice) {
        LOGW(LOG_TAG, "onReconnected()");
    }

    @Override
    public void onDummyDeviceSelected() {
        LOGW(LOG_TAG, "onDummyDeviceSelected()");
    }

    @Override
    public void onPortReady(int portID) {
        LOGW(LOG_TAG, "onPortReady(" + portID + ")");
    }

    @Override
    public void onCriticalFailure(int portID, Exception e) {
        LOGW(LOG_TAG, "onCriticalFailure(" + portID + " " + e.getMessage() + ")");
    }
}
