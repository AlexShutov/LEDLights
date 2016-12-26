package alex_shutov.com.ledlights.device_commands.main_logic.emulation_general;

/**
 * Created by Alex on 12/25/2016.
 */

import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.interval_sequence.IntervalSequencePlayer;
import rx.Scheduler;

/**
 * Base class for emulator, which depends on Sequence Player (light sequences and strobes)
 */
public abstract class SequenceEmulator extends EmulationExecutor
    implements IntervalSequencePlayer.IntervalSequenceCallback {

    private IntervalSequencePlayer sequencePlayer;

    /**
     * Set state of UI to some neutral value (say, black color or turn strobe off)
     */
    public abstract void resetToNeutralState();

    /**
     * Set this emulator as callback for sequence player
     * @param player
     */
    public SequenceEmulator(IntervalSequencePlayer player) {
        sequencePlayer = player;
        sequencePlayer.setCallback(this);
    }

    /**
     * Stop sequence player and reset UI state to some default value.
     */
    @Override
    protected void onStopCommand() {
        sequencePlayer.stop();
        // set neutral color. This is handy if device has no paused command
        resetToNeutralState();
    }

    /**
     * When sequence ends and will not repeat again, show light
     * with neutral color
     */
    @Override
    public void onSequenceEnded() {
        if (!getSequencePlayer().isInRepeatMode()) {
            resetToNeutralState();
        }
    }

    public IntervalSequencePlayer getSequencePlayer() {
        return sequencePlayer;
    }
}
