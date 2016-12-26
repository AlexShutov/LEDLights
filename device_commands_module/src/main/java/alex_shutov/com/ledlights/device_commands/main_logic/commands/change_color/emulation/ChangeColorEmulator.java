package alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.emulation;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.ChangeColorCommand;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulatedDeviceControl;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulationExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.LinkToEmulator;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Alex on 12/24/2016.
 */

public class ChangeColorEmulator extends EmulationExecutor {

    /**
     * Command for changing color doesn't need to know of 'stop' command -
     * ignore base class implementation
     * @param command
     * @return
     */
    @Override
    public boolean canExecute(Command command) {
        return command instanceof ChangeColorCommand;
    }

    /**
     * Do nothing
     */
    @Override
    protected void onStopCommand() {

    }

    @Override
    protected void processActualCommand(Command command) {
        // get link to emulator
        LinkToEmulator emulatorLink = getLinkToEmulator();
        // get reference to emulated device
        EmulatedDeviceControl device = emulatorLink.getDeviceControl();
        // cast command and change color on device
        ChangeColorCommand c = (ChangeColorCommand) command;
        int color = c.getColor();
        // inform device on main thread (should be displayed in UI) and
        // notify emulator on background thread
        Observable.defer(() -> Observable.just(color))
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(t -> {
                    device.setColor(t);
                    return t;
                })
                .observeOn(Schedulers.computation())
                .subscribe(t -> {
                    // inform emulator about command completion
                    emulatorLink.onCommandCompleted(command.isForegroundCommand());
                });
    }

}
