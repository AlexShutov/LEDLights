package alex_shutov.com.ledlights;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnEsbStore;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtLogicCell;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.transfer_data.TransferManagerFeedback;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by Alex on 10/20/2016.
 */
public class BtCellActivity extends Activity {
    private static final String LOG_TAG = BtCellActivity.class.getSimpleName();
    Button btnStart;
    Button btnCloseConnection;
    Button btnSendData;
    TextView tvPrint;
    LEDApplication app;
    BtLogicCell btCell;
    private void showMessage(String msg){
        tvPrint.setText(msg);
    }

    private Subscription sendingSubscription;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_cell);
        btnStart = (Button) findViewById(R.id.abc_btn_start);
        tvPrint = (TextView) findViewById(R.id.abc_tv_print);
        app = (LEDApplication) getApplication();
        btCell = app.getCell();
        btnStart.setOnClickListener(v -> {
            startPolling();
        });
        btnCloseConnection = (Button) findViewById(R.id.abc_btn_close_connection);
        btnCloseConnection.setOnClickListener(v -> {
            closeConnection();
        });
        btnSendData = (Button) findViewById(R.id.abc_btn_send);
        btnSendData.setOnClickListener(v -> {
//            String hello = "Hello!";
//            byte[] bytes = hello.getBytes();
//            btCell.getBtCommPort();

            if (sendingSubscription != null && !sendingSubscription.isUnsubscribed()) {
                sendingSubscription.unsubscribe();
                sendingSubscription = null;
            }
            sendingSubscription =
                    Observable.interval(20, TimeUnit.MILLISECONDS)
                            .map(cnt -> {
                                if (cnt % 2 == 0) {
                                    sendColorToDevice(0, 0, 0);
                                } else {
                                    sendColorToDevice(255, 255, 255);
                                }
                                return cnt;
                            })
                            .subscribe(cnt -> {

                            }, error -> {

                            });

        });

        btCell.setBtCommPortListener(new BtCommPortListener() {
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
            }

            @Override
            public void onDataSendFailed() {
                Log.i(LOG_TAG, "onDataSendFailed()");
            }

            @Override
            public void receiveData(byte[] data) {
                Log.i(LOG_TAG, "Activity: Data received");
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

    private void sendColorToDevice(int red, int green, int blue) {
        Observable.defer(() -> Observable.just(""))
                .subscribeOn(Schedulers.computation())
                .map(t -> {
                    byte[] bytes = new byte[7];
                    bytes[0] = '!';
                    bytes[1] = 0;
                    bytes[2] = 3;
                    bytes[3] = '\n';
                    bytes[4] = (byte) red;
                    bytes[5] = (byte) green;
                    bytes[6] = (byte) blue;
                    return bytes;
                })
                .observeOn(Schedulers.io())
                .subscribe(d -> {
                    BtCommPort commPort = btCell.getBtCommPort();
                    commPort.sendData(d);
                });
    }
}
