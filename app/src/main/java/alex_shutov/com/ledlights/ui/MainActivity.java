package alex_shutov.com.ledlights.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import alex_shutov.com.ledlights.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.service.BackgroundService;
import alex_shutov.com.ledlights.service.ServiceInterface;
import alex_shutov.com.ledlights.service.device_comm.DeviceControl;
import alex_shutov.com.ledlights.service.device_comm.DeviceControlFeedback;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by lodoss on 04/01/17.
 */

public class MainActivity extends AppCompatActivity {

    private ServiceInterface serviceInterface;
    private DeviceControl control;

    private DeviceControlFeedback deviceFeedback = new DeviceControlFeedback() {
        @Override
        public void onConnected(BtDevice device) {
            showMessage("connected: " + device.getDeviceName());
        }

        @Override
        public void onDummyDeviceSelected() {
            showMessage("Dummy device selected");
        }

        @Override
        public void onReconnected(BtDevice device) {

        }
    };

    /**
     * connects to background service
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder b) {
            BackgroundService.Binder binder = (BackgroundService.Binder) b;
            serviceInterface = binder.getServiceInterface();
            control = serviceInterface.getDeviceControl();
            serviceInterface.setDeviceControlFeedback(deviceFeedback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent startIntent = new Intent(this, BackgroundService.class);
        bindService(startIntent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        // disconnect from service
        serviceInterface.setDeviceControlFeedback(null);
        control = null;
        unbindService(connection);
        super.onStop();
    }

    private void showMessage(String msg) {
        Observable.defer(() -> Observable.just(msg))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> {
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                });
    }

}
