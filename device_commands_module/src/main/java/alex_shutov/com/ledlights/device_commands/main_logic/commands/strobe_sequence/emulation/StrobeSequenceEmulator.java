package alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.emulation;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.StrobeSequenceCommand;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulatedDeviceControl;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulationExecutor;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Alex on 12/25/2016.
 */

public class StrobeSequenceEmulator extends EmulationExecutor {



    /**
     * Inherited from EmulationExecutor
     */

    @Override
    public boolean canExecute(Command command) {
        boolean isStrobeCommand = command instanceof StrobeSequenceCommand;
        return super.canExecute(command) || isStrobeCommand;
    }

    @Override
    protected void onStopCommand() {
        turnStrobeOff();
    }

    @Override
    protected void processActualCommand(Command command) {

    }



    // private methods

    /**
     * Run on main thread
     */
    private void turnStrobeOn() {
        EmulatedDeviceControl deviceControl = getLinkToEmulator().getDeviceControl();
        Observable.defer(() -> Observable.just(deviceControl))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(dc -> {
                    deviceControl.turnStrobeOn();
                });
    }

    private void turnStrobeOff() {
        EmulatedDeviceControl deviceControl = getLinkToEmulator().getDeviceControl();
        Observable.defer(() -> Observable.just(deviceControl))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(dc -> {
                    deviceControl.turnStrobeOff();
                });
    }


}
