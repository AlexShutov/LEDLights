package alex_shutov.com.ledlights.device_commands.main_logic.emulation_general;

import android.util.Log;

import alex_shutov.com.ledlights.device_commands.main_logic.AnotherThreadDecorator;
import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.CommandExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.CompositeExecutor;

/**
 * Created by Alex on 12/24/2016.
 */

/**
 * This is a base class for device emulator. Its main purpose is to have memory for one
 * pending foreground command and serve as 'container' for all concrete command executors.
 * All concrete executors is added in DI module and this emulator is registered as
 * LinkToEmulator in every executor for completion feedback.
 */
public class DeviceEmulationFrame extends CompositeExecutor<EmulationExecutor>
        implements EmulationControl, CommandExecutor, LinkToEmulator {
    private static final EmulatedDeviceControl dummydevice = new EmulatedDeviceControl() {
        @Override
        public void setColor(int color) {}

        @Override
        public void turnStrobeOn() {}

        @Override
        public void turnStrobeOff() {}
    };

    protected EmulatedDeviceControl device;

    // decorate this instance for resuming paused foreground command on background thread.
    private AnotherThreadDecorator resumeCommandInBackgroundExec;
    private Command pausedForegroundCommand;
    private Command currentForegroundCommand;
    // indicates if emulation is On. If not, commands will not be dispatched.
    private boolean isOn;


    /**
     * Create and initialize all parts of emulator and bring up.
     * Set this emulator as completion listener for all executors within this composite.
     * It is assumed that those executors added during construction of this emulator and is
     * here at a time of initialization
     */
    public void init() {
        // sanity check
        if (getExecutors().isEmpty()) {
            throw new RuntimeException("Emulator has no executors at initialization time");
        }
        for (EmulationExecutor executor : getExecutors()) {
            executor.setLinkToEmulator(this);
        }
    }

    /**
     * stop and teardown emulator.
     * Send 'stop' command to all executors
     */
    public void suspend() {
        resumeCommandInBackgroundExec.execute(new StopAllEmulatedCommands());
    }

    public DeviceEmulationFrame() {
        setDevice(dummydevice);
        // decorate this instance by async decorator.
        resumeCommandInBackgroundExec = new AnotherThreadDecorator();
        resumeCommandInBackgroundExec.setUseAnotherThread(true);
        resumeCommandInBackgroundExec.setDecoree(this);
        // offer every executor to process the same command, it is necessary,
        // because 'stop' command must be received by every executor, other commands
        // will be rejected if command not supported.
        this.setMode(CompositeMode.All);
    }

    public EmulatedDeviceControl getDevice() {
        return device;
    }
    public void setDevice(EmulatedDeviceControl device) {
        // if user decided to disconnect from emulated device
        if (null == this.device) {
            this.device = dummydevice;
        } else {
            this.device = device;
        }
    }

    /**
     * Inherited from LinkToEmulator
     */

    @Override
    public EmulatedDeviceControl getDeviceControl() {
        return device;
    }

    /**
     * Check if this command is foreground. If it is foreground, verify that there
     * is no pending foreground command. If there is command pending, execute that command.
     * @param isForegroundCommand
     */
    @Override
    public void onCommandCompleted(boolean isForegroundCommand) {
        if (!isForegroundCommand) {
            // restore only foreground command
            return;
        }
        // current command completed, don't store it anymore
        currentForegroundCommand = null;
        // if there is paused command
        if (pausedForegroundCommand != null) {
            // mark it as current one
            currentForegroundCommand = pausedForegroundCommand;
            pausedForegroundCommand = null;
            // and execute that command.
            resumeCommandInBackgroundExec.execute(currentForegroundCommand);
        }
    }

    /**
     * Dispatch command if emulation is On
     * @param command
     */

    @Override
    public void execute(Command command) {
        if (isOn) {
            super.execute(command);
        }
    }

    /**
     * Inherited from EmulationControl
     */

    /**
     *
     */
    @Override
    public void turnEmulationOn() {
        isOn = true;
    }

    /**
     * Switch to dummy device and broadcast 'stop' command in background
     */
    @Override
    public void turnEmulationOff() {
        isOn = false;
        setDevice(dummydevice);
        Command stopCommand = new StopAllEmulatedCommands();
        resumeCommandInBackgroundExec.execute(stopCommand);
    }
}
