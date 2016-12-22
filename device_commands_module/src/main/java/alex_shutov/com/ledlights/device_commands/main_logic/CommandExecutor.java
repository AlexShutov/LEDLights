package alex_shutov.com.ledlights.device_commands.main_logic;

/**
 * Created by lodoss on 22/12/16.
 */

public interface CommandExecutor {

    /**
     * Check if this executor can execute a particular command. It is used in 'chain of command'
     * pattern
     * @param command
     * @return
     */
    boolean canExecute(Command command);

    /**
     * Handle command
     * @param command
     */
    void execute(Command command);
}
