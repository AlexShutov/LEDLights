package alex_shutov.com.ledlights;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import alex_shutov.com.ledlights.app_facade.AppFacade;
import alex_shutov.com.ledlights.app_facade.AppFacadeDeviceListener;
import alex_shutov.com.ledlights.app_facade.AppHubInitializedEvent;
import alex_shutov.com.ledlights.app_facade.AppHubService;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtLogicCell;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.service.BtCellService;
import alex_shutov.com.ledlights.device_commands.ControlPort.EmulationCallback;
import alex_shutov.com.ledlights.device_commands.DeviceCommandsCellDeployer;
import alex_shutov.com.ledlights.device_commands.DeviceCommandsLogicCell;
import rx.Subscription;


/**
 * Created by Alex on 10/20/2016.
 */
public class BtCellActivity extends Activity {
    private static final String LOG_TAG = BtCellActivity.class.getSimpleName();
    Button btnStart;
    Button btnCloseConnection;
    Button btnSendData;
    TextView tvPrint;
    View emulationLed;
    View emulationStrobe;

    private EventBus eventBus;
    private AppFacade facade;

    private void showMessage(String msg){
        tvPrint.setText(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_cell);
        btnStart = (Button) findViewById(R.id.abc_btn_start);
        tvPrint = (TextView) findViewById(R.id.abc_tv_print);
        emulationLed = (View) findViewById(R.id.abc_emulation_led);
        emulationStrobe = (View) findViewById(R.id.abc_emulation_strobe);
        emulationStrobe.setBackgroundColor(Color.BLACK);

        btnStart.setOnClickListener(v -> {
            selectAnotherDevice();
        });
        btnCloseConnection = (Button) findViewById(R.id.abc_btn_close_connection);
        btnCloseConnection.setOnClickListener(v -> {
            closeConnection();
        });
        btnSendData = (Button) findViewById(R.id.abc_btn_send);
        btnSendData.setOnClickListener(v -> {

        });
        eventBus = EventBus.getDefault();
    }

    private void selectAnotherDevice() {
        facade.selectAnotherDevice();
    }
    private void closeConnection(){
        facade.disconnectFromDevice();
    }


    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    protected void onStop() {
        eventBus.unregister(this);
        if (null == facade) {
            facade.disableEmulation();
        }
        unbindService(mConnection);
        super.onStop();
    }

    @Subscribe
    public void onAppHubReady(AppHubInitializedEvent event){
        if (null == facade) {
            bindToBtService();
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AppHubService.AppHubBinder binder =
                    (AppHubService.AppHubBinder) service;
            facade = binder.getAppFacade();
            init();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    

    private void bindToBtService() {
//        Intent startIntent = new Intent(this, BtCellService.class);
//        mConnection = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//                BtCellService.BtCellBinder binder = (BtCellService.BtCellBinder) iBinder;
//                btCell = binder.getService().getCell();
//                // init when we have a reference to bound Service
//                init();
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName componentName) {
//
//            }
//        };
//        bindService(startIntent, mConnection, BIND_AUTO_CREATE);

        Intent startIntent = new Intent(this, AppHubService.class);
        bindService(startIntent, mConnection, BIND_AUTO_CREATE);
    }


    private void init() {
        AppFacadeDeviceListener deviceListener = new AppFacadeDeviceListener() {
            @Override
            public void onDeviceConnected() {

            }

            @Override
            public void onDeviceConnectionFailed() {

            }

            @Override
            public void onDeviceReconnected() {

            }

            @Override
            public void onDumyDeviceSelected() {

            }
        };
        EmulationCallback emulatedDevice = new EmulationCallback() {
            @Override
            public void onLEDColorChanged(int color) {

            }

            @Override
            public void onStrobeOn() {

            }

            @Override
            public void onStrobeOff() {

            }
        };
        facade.setDeviceListener(deviceListener);
        facade.setEmulationCallback(emulatedDevice);
        facade.enableEmulation();
    }


}
