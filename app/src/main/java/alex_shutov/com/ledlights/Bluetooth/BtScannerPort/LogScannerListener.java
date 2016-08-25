package alex_shutov.com.ledlights.Bluetooth.BtScannerPort;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.Set;

import alex_shutov.com.ledlights.Bluetooth.BtDevice;
import alex_shutov.com.ledlights.Bluetooth.BtScannerPort.hex.BtScanPortListener;
import rx.Observable;

/**
 * Created by Alex on 7/28/2016.
 */
public class LogScannerListener implements BtScanPortListener {
    private Context context;

    public LogScannerListener(Context context){
        this.context = context;
    }

    private void showMessage(final String message){
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onPairedDevicesReceived(Set<BtDevice> devices) {
        if (devices.isEmpty()) {
            showMessage("No paired devices found");
        }else {
            for (BtDevice device : devices){
                String description = device.getDeviceName() + ": " +
                        device.getDeviceAddress() + " " +
                        (device.getPaired() ? "paired" : " not paider");
                showMessage(description);
            }
        }
    }

    @Override
    public void onDeviceFound(BtDevice device) {

    }

    @Override
    public void onScanCompleted() {

    }

    @Override
    public void onCriticalFailure(int portID, Exception e) {

    }

    @Override
    public void onPortReady(int portID) {

    }
}
