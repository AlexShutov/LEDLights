package alex_shutov.com.ledlights.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Random;

import alex_shutov.com.ledlights.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.databinding.MainActivityBinding;
import alex_shutov.com.ledlights.device_commands.ControlPort.EmulationCallback;
import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.ChangeColorCommand;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.LightsSequenceCommand;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.models.Light;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.models.LightsSequence;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.save_to_ee.SaveToEECommand;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.StrobeSequenceCommand;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.model.StrobeFlash;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.model.StrobeSequence;
import alex_shutov.com.ledlights.service.BackgroundService;
import alex_shutov.com.ledlights.service.ServiceInterface;
import alex_shutov.com.ledlights.service.device_comm.DeviceControl;
import alex_shutov.com.ledlights.service.device_comm.DeviceControlFeedback;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by lodoss on 04/01/17.
 */

public class MainActivity extends AppCompatActivity  implements EmulationCallback {

    private MainActivityBinding binding;

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

            // activity can show
            serviceInterface.setEmulatedDevice(MainActivity.this);
            serviceInterface.getEmulationControl().turnEmulationOn();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        binding.maBtnPickDevice.setOnClickListener(v -> {
            control.selectAnotherDevice();
        });
        binding.maBtnSendTestCommand.setOnClickListener(v -> {
            sendTestCommand();
        });
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

        // disable emulation first
        serviceInterface.getEmulationControl().turnEmulationOff();
        serviceInterface.setEmulatedDevice(null);

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


    /**
     * Inherited from EmulationCallback
     */

    @Override
    public void onLEDColorChanged(int color) {
        binding.maViewColor.setBackgroundColor(color);
    }

    @Override
    public void onStrobeOn() {
        binding.maViewStrobe.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onStrobeOff() {
        binding.maViewStrobe.setBackgroundColor(Color.BLACK);
    }




    /**
     * testing sending commands
     */

    int count = 0;
    Random r = new Random();
    Subscription sendingSubscription;

    public void sendTestCommand() {

//        sendingSubscription =
//                    Observable.interval(30, TimeUnit.MILLISECONDS)
//                            .map(cnt -> {
////                                ChangeColorCommand command = new ChangeColorCommand();
////                                int color = Color.argb(0xff, r.nextInt(255), r.nextInt(255), r.nextInt(255));
////                                command.setColor(color);
////                                execute(command);
////                                playSequence();
//                                switchFlash(count++ % 2 == 0);
//                                return cnt;
//                            })
//                            .subscribe(cnt -> {
//
//                            }, error -> {
//
//                            });
//        ChangeColorCommand command = new ChangeColorCommand();
//        int color = Color.argb(0xff, r.nextInt(255), r.nextInt(255), r.nextInt(255));
//        command.setColor(color);
//        execute(command);
//        switchFlash(count++ % 2 == 0);
        playSequence();
        playFlashSequence();
//        testSaveCommand();
    }

    private void changeColor(){
        ChangeColorCommand command = new ChangeColorCommand();
        int color = Color.argb(0xff, r.nextInt(255), r.nextInt(255), r.nextInt(255));
        command.setColor(color);
        execute(command);
    }

    private void playSequence() {
        LightsSequenceCommand command = new LightsSequenceCommand();
        LightsSequence lightsSequence = new LightsSequence();
        command.setLightsSequence(lightsSequence);

        lightsSequence.setSmoothSwitching(true);
        lightsSequence.setRepeating(true);

        Light l;

        l = new Light();
        l.setColor(Color.RED);
        l.setDuration(500);
        lightsSequence.addLight(l);

        l = new Light();
        l.setColor(Color.BLUE);
        l.setDuration(500);
        lightsSequence.addLight(l);

        execute(command);
    }

    private void switchFlash(boolean isOn) {
        StrobeSequenceCommand command = new StrobeSequenceCommand();
        StrobeSequence sequence = new StrobeSequence();
        command.setSequence(sequence);
        // setup sequence
        sequence.setPermanent(true);
        sequence.setOn(isOn);

        execute(command);
    }

    private void playFlashSequence() {
        StrobeSequenceCommand command = new StrobeSequenceCommand();
        StrobeSequence sequence = new StrobeSequence();
        command.setSequence(sequence);

        sequence.setPermanent(false);
        sequence.setOn(false);
        sequence.setRepeat(count++ % 2 == 0);

        StrobeFlash flash;

        flash = new StrobeFlash();
        flash.setTimeOn(10);
        flash.setTimeOff(30);
        sequence.addFlash(flash);

        flash = new StrobeFlash();
        flash.setTimeOn(20);
        flash.setTimeOff(50);
        sequence.addFlash(flash);

        flash = new StrobeFlash();
        flash.setTimeOn(50);
        flash.setTimeOff(300);
        sequence.addFlash(flash);

        execute(command);
    }

    private void testSaveCommand() {
        // generate foreground command
        LightsSequenceCommand foregroundCommand = new LightsSequenceCommand();
        LightsSequence lightsSequence = new LightsSequence();
        foregroundCommand.setLightsSequence(lightsSequence);

        lightsSequence.setSmoothSwitching(true);
        lightsSequence.setRepeating(true);

        Light l;
        l = new Light();
        l.setColor(Color.GREEN);
        l.setDuration(200);
        lightsSequence.addLight(l);
        l = new Light();
        l.setColor(Color.RED);
        l.setDuration(200);
        lightsSequence.addLight(l);
        l = new Light();
        l.setColor(Color.BLUE);
        l.setDuration(200);
        lightsSequence.addLight(l);

//        execute(foregroundCommand);

        // generate background command
        StrobeSequenceCommand backgroundCommand = new StrobeSequenceCommand();
        StrobeSequence sequence = new StrobeSequence();
        backgroundCommand.setSequence(sequence);
        sequence.setPermanent(false);
        sequence.setOn(false);
        sequence.setRepeat(true);

        StrobeFlash flash;
        flash = new StrobeFlash();
        flash.setTimeOn(1000);
        flash.setTimeOff(1000);
        sequence.addFlash(flash);

//        execute(backgroundCommand);

        SaveToEECommand saveCommand = new SaveToEECommand();
        saveCommand.setForegroundCommand(foregroundCommand);
        saveCommand.setBackgroundCommand(backgroundCommand);
        saveCommand.setLoadCommand(false);
        saveCommand.setEraseCell(false);
        saveCommand.setCellIndex(1);

        execute(saveCommand);

        SaveToEECommand loadCommand = new SaveToEECommand();
        loadCommand.setCellIndex(1);
        loadCommand.setLoadCommand(true);
        execute(loadCommand);
    }

    /**
     * This is test method for now. Just dispatch command to control port
     * @param command
     */
    public void execute(Command command) {
        serviceInterface.execute(command);
    }

}
