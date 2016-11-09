package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtUiPort;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import alex_shutov.com.ledlights.bluetoothmodule.R;

/**
 * Created by lodoss on 09/11/16.
 */
public class ChooseDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_device);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }


}
