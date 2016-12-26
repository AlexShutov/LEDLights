package alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.emulation;


import java.util.ArrayList;
import java.util.List;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.LightsSequenceCommand;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.models.Light;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulatedDeviceControl;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.SequenceEmulator;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.interval_sequence.IntervalSequencePlayer;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Alex on 12/25/2016.
 */

public class LightSequenceEmulator extends SequenceEmulator {

    /**
     * Reference to light sequence
     */
    private List<Light> lights;

    public LightSequenceEmulator(IntervalSequencePlayer player) {
        super(player);
    }

    @Override
    public void resetToNeutralState() {
        setNeutralColor();
    }

    @Override
    public boolean canExecute(Command command) {
        boolean isLightSequenceCommand = command instanceof LightsSequenceCommand;
        return super.canExecute(command) || isLightSequenceCommand;
    }



    @Override
    protected void processActualCommand(Command command) {
        LightsSequenceCommand lightsCmd = (LightsSequenceCommand) command;
        lights = lightsCmd.getLightsSequence().getLights();
        boolean isRepeating = lightsCmd.getLightsSequence().isRepeating();
        // create list of light durations;
        List<Long> lightDurations = new ArrayList<>();
        for (Light light : lights) {
            lightDurations.add(light.getDuration());
        }
        // schedule sequence emulation
       getSequencePlayer().startSequence(lightDurations, isRepeating);
    }

    /**
     *  Inherited from IntervalSequencePlayer.IntervalSequenceCallback
     */

    /**
     * Pick right color and show it
     * @param intervalNo index of time interval
     */
    @Override
    public void onIntervalStarted(int intervalNo) {
        Light light = lights.get(intervalNo);
        EmulatedDeviceControl deviceControl = getLinkToEmulator().getDeviceControl();
        // show that color (Run on Main Thread for UI)
        Observable.defer(() -> Observable.just(deviceControl))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(dc -> {
                    int currColor = light.getColor();
                    deviceControl.setColor(currColor);
                });
    }

    @Override
    public void onIntevalEnded(int intervalNo) {

    }

    @Override
    public void onSequenceStarted() {

    }

    /**
     * When sequence ends and will not repeat again, show light
     * with neutral color
     */
    @Override
    public void onSequenceEnded() {
        if (!getSequencePlayer().isInRepeatMode()) {
            setNeutralColor();
        }
    }

    @Override
    public void onSequenceRestarted() {

    }

    // private methods


}
