package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import alex_shutov.com.ledlights.bluetoothmodule.R;

/**
 * Created by lodoss on 09/11/16.
 */
public class ChooseDeviceActivity extends AppCompatActivity {

    private EventBus eventBus;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_device);
        eventBus = EventBus.getDefault();

    }

    @Override
    protected void onPause() {
        eventBus.unregister(this);
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        eventBus.register(this);
    }



}
