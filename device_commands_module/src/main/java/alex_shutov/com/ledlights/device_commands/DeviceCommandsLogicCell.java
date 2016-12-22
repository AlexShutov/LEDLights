package alex_shutov.com.ledlights.device_commands;

import android.graphics.Color;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPort;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPortAdapter;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPortListener;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceSender;
import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.CommandExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.LightsSequenceCommand;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.models.Light;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.models.LightsSequence;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CompositeSerializer;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.ChangeColor;
import alex_shutov.com.ledlights.hex_general.LogicCell;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by lodoss on 21/12/16.
 */

/**
 * This cell contains:
 *  - logic, used for converting Application's commands into device commands.
 *  - logic for sending commands to device. The point is, that sending data need some time, so,
 *    we have to wait until current command is sent before attempting to send another command.
 *  - Port, responsible for communication with actual device. This logic cell know nothing about
 *    how connection to device is established and how data transferred.
 *  - Emulation of device. User may not have assembled device yet (DIY), but want to see if
 *    this app is a good idea and worth buying details and soldering a device. To do so we need
 *    to emulate device workflow (UI will show changes in device state).
 */
public class DeviceCommandsLogicCell extends LogicCell implements CommandExecutor {

    private DeviceCommPortAdapter commPortAdapter;
    // Reference to interface for sending serialized command to connected device.
    // DeviceCommPortAdapter adapt this interface.
    private DeviceSender sendInterface;

    /**
     * This is a composite command executor, containing serializers for all commands.
     */
    @Inject
    @Named("CommandSerializationStore")
    CompositeSerializer serializationStore;


    /**
     * Initialize components, used in this logic cell in this method
     */
    @Override
    public void init() {
        commPortAdapter.initialize();
        // use adapter to output port for sending data.
        sendInterface = commPortAdapter;
        // connect serializers to device interface
        serializationStore.setDeviceSender(sendInterface);
    }

    /**
     * Stop activity of all components here - app is about to be destroyed
     */
    @Override
    public void suspend() {

    }

    /**
     * Initialize all objects in this cell by DI
     */
    @Override
    protected void injectThisCell() {
        // get reference to DI component
        DeviceCommandsPortAdapterCreator creator =
                (DeviceCommandsPortAdapterCreator) getAdaperCreator();
        // init all objects
        creator.injectLogicCell(this);
    }

    int count = 0;
    Random r = new Random();
    Subscription sendingSubscription;

    public void sendTestCommand() {

//        sendingSubscription =
//                    Observable.interval(10, TimeUnit.SECONDS)
//                            .map(cnt -> {
////                                ChangeColor command = new ChangeColor();
////                                int color = Color.argb(0xff, r.nextInt(255), r.nextInt(255), r.nextInt(255));
////                                command.setColor(color);
////                                execute(command);
//                                playSequence();
//                                return cnt;
//                            })
//                            .subscribe(cnt -> {
//
//                            }, error -> {
//
//                            });
//        ChangeColor command = new ChangeColor();
//        int color = Color.argb(0xff, r.nextInt(255), r.nextInt(255), r.nextInt(255));
//        command.setColor(color);
//        execute(command);

        playSequence();
    }


    private void playSequence() {
        LightsSequenceCommand command = new LightsSequenceCommand();
        LightsSequence lightsSequence = new LightsSequence();
        command.setLightsSequence(lightsSequence);

        lightsSequence.setSmoothSwitching(true);
        lightsSequence.setRepeating(true);

        Light l;

        l = new Light();
        l.setColor(0xad2f0f);
        l.setDuration(100);
        lightsSequence.addLight(l);

        l = new Light();
        l.setColor(0x0f88ad);
        l.setDuration(100);
        lightsSequence.addLight(l);
        execute(command);
    }

    /**
     * This is a top- level entity, it can execute any command (or die trying :) )
     * @param command
     * @return
     */
    @Override
    public boolean canExecute(Command command) {
        return true;
    }

    @Override
    public void execute(Command command) {
        Observable.defer(() -> Observable.just(command))
                .subscribeOn(Schedulers.io())
                .subscribe(c -> {
                    serializationStore.execute(c);
                });
    }

    /**
     * Inherited from CommandExecutor
     */



    // accessors

    public DeviceCommPort getCommPort() {
        return commPortAdapter;
    }

    public void setCommPortAdapter(DeviceCommPortAdapter commPortAdapter) {
        this.commPortAdapter = commPortAdapter;
    }

    /**
     * Set port listener to adapter, responsible for external communications.
     * @param listener
     */
    public void setDeviceCommPortListener(DeviceCommPortListener listener) {
        commPortAdapter.setPortListener(listener);
    }

    // private methods
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
                    sendInterface.sendData(d);
                });
    }

}
