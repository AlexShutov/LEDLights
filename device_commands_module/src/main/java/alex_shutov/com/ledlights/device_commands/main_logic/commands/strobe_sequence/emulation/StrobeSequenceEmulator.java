package alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.emulation;

import java.util.ArrayList;
import java.util.List;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.StrobeSequenceCommand;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.model.StrobeFlash;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.model.StrobeSequence;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulatedDeviceControl;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.SequenceEmulator;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.interval_sequence.IntervalSequencePlayer;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Alex on 12/25/2016.
 */

public class StrobeSequenceEmulator extends SequenceEmulator {

    /**
     * List of actual time intervals (not strobes)
     */
    private List<Long> intervals;

    public StrobeSequenceEmulator(IntervalSequencePlayer sequencePlayer) {
        super(sequencePlayer);
    }

    /**
     * Inherited from EmulationExecutor
     */

    @Override
    public boolean canExecute(Command command) {
        boolean isStrobeCommand = command instanceof StrobeSequenceCommand;
        return super.canExecute(command) || isStrobeCommand;
    }

    /**
     * Assume that neutral state is with strobe OFF
     */
    @Override
    public void resetToNeutralState() {
        turnStrobeOff();
    }

    /**
     * Things gets a bit tricky around here:
     * Every strobe consist of two intervals - when strobe is ON and when it is OFF.
     * intervals with odd numbers (starting from 1) correspond to ON state, and with
     * even numbers - to ON state.
     * Create list of intervals from strobe and schedule sequence.
     * @param command
     */
    @Override
    protected void processActualCommand(Command command) {
        StrobeSequenceCommand strobeCmd = (StrobeSequenceCommand) command;
        StrobeSequence sequence = strobeCmd.getSequence();
        // check, maybe, if command is permanent, we don't even need to schedule
        // sequence
        if (sequence.isPermanent()) {
            // starting new sequence stop previous sequence automatically, but this
            // command is permanent (ON or OFF) so there will be no sequence. That's why
            // stop current sequence explicitly
            getSequencePlayer().stop();
            if (sequence.isOn()) {
                turnStrobeOn();
            } else {
                turnStrobeOff();
            }
            // we're done here
            return;
        }
        boolean repeatMode = sequence.isRepeat();
        // create sequence of intervals from strobe sequence
        intervals = new ArrayList<>();
        for (StrobeFlash flash : sequence.getFlashes()) {
            long timeOn = flash.getTimeOn();
            intervals.add(timeOn);
            long timeOff = flash.getTimeOff();
            intervals.add(timeOff);
        }
        // we have list of intervals at this point, start new sequence
        IntervalSequencePlayer sequencePlayer = getSequencePlayer();
        sequencePlayer.startSequence(intervals, repeatMode);
    }

    /**
     * Inherited from IntervalSequencePlayer.IntervalSequenceCallback
     */

    /**
     * Define if this interval number represent odd (ON) or even (OFF) number. To do so,
     * we have to count from 1 (not from 0), because first odd number is 1.
     * @param intervalNo index of time interval
     */
    @Override
    public void onIntervalStarted(int intervalNo) {
        int position = intervalNo + 1;
        boolean isOn = position % 2 != 0;
        if (isOn) {
            turnStrobeOn();
        } else {
            turnStrobeOff();
        }
    }

    @Override
    public void onIntevalEnded(int intervalNo) {

    }

    @Override
    public void onSequenceStarted() {

    }


    @Override
    public void onSequenceRestarted() {

    }


    // private methods

    /**
     * Run on thread, intended for emulated device updates.
     */
    private void turnStrobeOn() {
        EmulatedDeviceControl deviceControl = getLinkToEmulator().getDeviceControl();
        Observable.defer(() -> Observable.just(deviceControl))
                .subscribeOn(getUiThreadScheduler())
                .subscribe(dc -> {
                    deviceControl.turnStrobeOn();
                });
    }

    private void turnStrobeOff() {
        EmulatedDeviceControl deviceControl = getLinkToEmulator().getDeviceControl();
        Observable.defer(() -> Observable.just(deviceControl))
                .subscribeOn(getUiThreadScheduler())
                .subscribe(dc -> {
                    deviceControl.turnStrobeOff();
                });
    }


}
