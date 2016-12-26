package alex_shutov.com.ledlights;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtLogicCell;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.service.BtCellService;
import alex_shutov.com.ledlights.device_commands.ControlPort.ControlPortAdapter;
import alex_shutov.com.ledlights.device_commands.ControlPort.EmulationCallback;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPort;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPortListener;
import alex_shutov.com.ledlights.device_commands.DeviceCommandsCellDeployer;
import alex_shutov.com.ledlights.device_commands.DeviceCommandsLogicCell;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.interval_sequence.IntervalSequencePlayer;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Alex on 10/20/2016.
 */
public class BtCellActivity extends Activity {
    private static final String LOG_TAG = BtCellActivity.class.getSimpleName();
    Button btnStart;
    Button btnCloseConnection;
    Button btnSendData;
    Button btnSequencePlayer;
    TextView tvPrint;
    LEDApplication app;
    View emulationLed;
    View emulationStrobe;

    int count = 0;

    private BtLogicCell btCell;
    private void showMessage(String msg){
        tvPrint.setText(msg);
    }
    private Subscription sendingSubscription;

    private DeviceCommandsLogicCell commCell;
    private DeviceCommandsCellDeployer commCellDeployer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_cell);
        btnStart = (Button) findViewById(R.id.abc_btn_start);
        tvPrint = (TextView) findViewById(R.id.abc_tv_print);
        emulationLed = (View) findViewById(R.id.abc_emulation_led);
        emulationStrobe = (View) findViewById(R.id.abc_emulation_strobe);
        emulationStrobe.setBackgroundColor(Color.BLACK);

//        app = (LEDApplication) getApplication();
//        btCell = app.getCell();



        btnStart.setOnClickListener(v -> {
            startPolling();
        });
        btnCloseConnection = (Button) findViewById(R.id.abc_btn_close_connection);
        btnCloseConnection.setOnClickListener(v -> {
            closeConnection();
        });
        btnSendData = (Button) findViewById(R.id.abc_btn_send);
        btnSendData.setOnClickListener(v -> {

//            if (sendingSubscription != null && !sendingSubscription.isUnsubscribed()) {
//                sendingSubscription.unsubscribe();
//                sendingSubscription = null;
//            }
//            sendingSubscription =
//                    Observable.interval(20, TimeUnit.MILLISECONDS)
//                            .map(cnt -> {
//                                if (cnt % 2 == 0) {
//                                    sendColorToDevice(0, 0, 0);
//                                } else {
//                                    sendColorToDevice(255, 255, 255);
//                                }
//                                return cnt;
//                            })
//                            .subscribe(cnt -> {
//
//                            }, error -> {
//
//                            });
            commCell.sendTestCommand();
        });
        btnSequencePlayer = (Button) findViewById(R.id.abc_btn_test_interval);
        btnSequencePlayer.setOnClickListener(v -> {
            testSequencePlayer();
        });
    }

    private void startPolling(){
        BtCommPort commPort = btCell.getBtCommPort();
        commPort.selectAnotherDevice();
    }
    private void closeConnection(){
        BtCommPort commPort = btCell.getBtCommPort();
        commPort.disconnect();
    }



    @Override
    protected void onStart() {
        super.onStart();
        bindToBtService();
    }

    @Override
    protected void onStop() {
        unbindService(mConnection);
        commCell.suspend();
        commCell.getControlPort().disableEmulation();
        super.onStop();
    }


    private ServiceConnection mConnection;


    private void bindToBtService() {
        Intent startIntent = new Intent(this, BtCellService.class);
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                BtCellService.BtCellBinder binder = (BtCellService.BtCellBinder) iBinder;
                btCell = binder.getService().getCell();
                // init when we have a reference to bound Service
                init();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(startIntent, mConnection, BIND_AUTO_CREATE);
    }


    private void init() {
        // connect command logic cell with bluetooth logic cell (forward)
        BtCommPortListener commListener = new BtCommPortListener() {
            @Override
            public void onConnectionStarted(BtDevice btDevice) {
                Log.i(LOG_TAG, "onConnectionStarted() " + (btDevice == null ? "" :
                        btDevice.getDeviceName()) );
            }

            @Override
            public void onConnectionFailed() {
                Log.i(LOG_TAG, "onConnectionFailed()");
            }

            @Override
            public void onDataSent() {
                Log.i(LOG_TAG, "onDataSent()");
                DeviceCommPort commPort = commCell.getCommPort();
                commPort.onDataSent();
            }

            @Override
            public void onDataSendFailed() {
                Log.i(LOG_TAG, "onDataSendFailed()");
            }

            @Override
            public void receiveData(byte[] data, int size) {
                Log.i(LOG_TAG, "Activity: Data received");
                DeviceCommPort commPort = commCell.getCommPort();
                commPort.onResponse(data);
            }

            @Override
            public void onReconnected(BtDevice btDevice) {
                Log.i(LOG_TAG, "onReconnected()");
            }

            @Override
            public void onDummyDeviceSelected() {
                Log.i(LOG_TAG, "onDummyDeviceSelected()");
            }

            @Override
            public void onPortReady(int portID) {
                Log.i(LOG_TAG, "onPortReady(), id: " + portID);
            }

            @Override
            public void onCriticalFailure(int portID, Exception e) {
                Log.i(LOG_TAG, "onCriticalFailure(), id: " + portID + " message: " +
                        e.getMessage());
            }
        };
        btCell.setBtCommPortListener(commListener);
        commCell = new DeviceCommandsLogicCell();
        commCellDeployer = new DeviceCommandsCellDeployer();
        commCellDeployer.deploy(commCell);


        // connect command logic cell with bluetooth logic cell (backward)
        commCell.setDeviceCommPortListener(new DeviceCommPortListener() {
            @Override
            public void sendData(byte[] data) {
                btCell.getBtCommPort().sendData(data);
            }

            @Override
            public void onPortReady(int portID) {

            }

            @Override
            public void onCriticalFailure(int portID, Exception e) {

            }
        });

        commCell.setEmulationCallback(new EmulationCallback() {
            @Override
            public void onLEDColorChanged(int color) {
                emulationLed.setBackgroundColor(color);
            }

            @Override
            public void onStrobeOn() {
                emulationStrobe.setBackgroundColor(Color.WHITE);
            }

            @Override
            public void onStrobeOff() {
                emulationStrobe.setBackgroundColor(Color.BLACK);
            }
        });
        commCell.getControlPort().enableEmulation();
    }

    private IntervalSequencePlayer sequencePlayer;
    private void testSequencePlayer() {
        if (null != sequencePlayer) {
            sequencePlayer.stop();
        }
        sequencePlayer = new IntervalSequencePlayer();
        List<Long> intervals = new ArrayList<>();
        intervals.add(20000l);
        intervals.add(10000l);
        sequencePlayer.setCallback(sequenceCallback);
        boolean repeat = true;
        sequencePlayer.startSequence(intervals, repeat);



    }

    private void showToast(String message){
        Observable.defer(() -> Observable.just(message))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(m -> Toast.makeText(this, m, Toast.LENGTH_SHORT).show());
    }

    private IntervalSequencePlayer.IntervalSequenceCallback sequenceCallback =
            new IntervalSequencePlayer.IntervalSequenceCallback() {
                @Override
                public void onIntervalStarted(int intervalNo) {
                    String msg = "Interval started: " + intervalNo;
                    Log.i(LOG_TAG, msg);
                    showToast(msg);
                }

                @Override
                public void onIntevalEnded(int intervalNo) {
                    String msg = "interval ended " + intervalNo;
                    Log.i(LOG_TAG, msg);
                    showToast(msg);
                }

                @Override
                public void onSequenceStarted() {
                    String msg = "Sequence started";
                    Log.i(LOG_TAG, msg);
                    showToast(msg);
                }

                @Override
                public void onSequenceEnded() {
                    String msg = "Sequence ended";
                    Log.i(LOG_TAG, msg);
                    showToast(msg);
                }

                @Override
                public void onSequenceRestarted() {
                    String msg = "Sequence restarted";
                    Log.i(LOG_TAG, msg);
                    showToast(msg);
                }
            };
}
