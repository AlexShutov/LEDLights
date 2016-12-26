package alex_shutov.com.ledlights.device_commands.main_logic.emulation_general;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;

/**
 * Created by Alex on 12/24/2016.
 */

/**
 * Emulation is achieved by using another implementation of CommandExecutor.
 * But, we need a way to stop all currently active commands (perhaps, foreground and
 * background command). It can be accomplished either by adding additional methods to
 * base class of emulation executor, or, by passing down command to stop all emulated
 * executors. This command will be sent by DeviceEmulator so it won't be received by any
 * executor, sending data to real device. Moreover, it can be, because all real executors will
 * ignore it.
 */
public class StopAllEmulatedCommands extends Command {
    @Override
    public int getCommandCode() {
        return -1;
    }

    @Override
    public boolean isForegroundCommand() {
        return false;
    }
}
