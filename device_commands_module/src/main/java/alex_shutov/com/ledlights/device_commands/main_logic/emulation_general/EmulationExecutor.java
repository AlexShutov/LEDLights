package alex_shutov.com.ledlights.device_commands.main_logic.emulation_general;

import android.graphics.Color;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.CommandExecutor;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Alex on 12/24/2016.
 */

public abstract class EmulationExecutor implements CommandExecutor {

    /**
     * This listener is used by concrete listener for notifying emulator abour
     * command completion (for example, when sequence of lights is complete and should
     * not be repeated
     */
    private LinkToEmulator linkToEmulator;

    /**
     * Handle stop command here
     */
    protected abstract void onStopCommand();

    /**
     * Emulation may receive few service commands - there is single 'stop' command
     * for now. Identify those commands in this base class and process actual
     * commands in derived class by this methods
     * @param command
     */
    protected abstract void processActualCommand(Command command);

    public LinkToEmulator getLinkToEmulator() {
        return linkToEmulator;
    }

    public void setLinkToEmulator(LinkToEmulator linkToEmulator) {
        this.linkToEmulator = linkToEmulator;
    }

    /**
     * Every EmulationExecutor is meant to be used in device emulation. That means, if
     * command is running, emulator has to be able to stop it.
     * @param command
     * @return
     */
    @Override
    public boolean canExecute(Command command) {
        boolean isStopCommand = command instanceof StopAllEmulatedCommands;
        return isStopCommand;
    }

    /**
     * Separate 'stop' command from other actual commands
     * @param command
     */
    @Override
    public void execute(Command command) {
        if (isStopCommand(command)) {
            onStopCommand();
            return;
        }
        processActualCommand(command);
    }

    /**
     * Check if this is a stop command
     * @param command
     * @return
     */
    protected boolean isStopCommand(Command command) {
        return command instanceof StopAllEmulatedCommands;
    }

    /**
     * set some neutral color after emulation completes.
     * If we don't do so, it will look like emulation is hanging.
     * Assume that neutral color is a black color.
     */
    protected void setNeutralColor() {
        EmulatedDeviceControl deviceControl = getLinkToEmulator().getDeviceControl();
        Observable.defer(() -> Observable.just(deviceControl))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(dc -> {
                    int neutralColor = Color.BLACK;
                    deviceControl.setColor(neutralColor);
                });
    }
}
