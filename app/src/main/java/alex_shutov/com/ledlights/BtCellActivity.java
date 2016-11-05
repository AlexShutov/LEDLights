package alex_shutov.com.ledlights;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtLogicCell;
import alex_shutov.com.ledlights.hex_general.LogicCell;

/**
 * Created by Alex on 10/20/2016.
 */
public class BtCellActivity extends Activity {
    Button btnStart;
    TextView tvPrint;
    LEDApplication app;
    BtLogicCell btCell;
    private void showMessage(String msg){
        tvPrint.setText(msg);
    }

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
    }

    private void startPolling(){
        BtCommPort commPort = btCell.getBtCommPort();
        commPort.startConnection();

    }

}
