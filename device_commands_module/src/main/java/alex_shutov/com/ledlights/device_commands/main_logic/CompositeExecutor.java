package alex_shutov.com.ledlights.device_commands.main_logic;

/**
 * Created by lodoss on 22/12/16.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of 'Composite' pattern
 * It can be in two modes - first (and default) offer command to first suitable executor and
 * then stop process.
 * In second mode it offers command to all stored executors.
 */
public class CompositeExecutor implements CommandExecutor {
    // the way, command should be processed
    public static enum CompositeMode {
        Single,
        All
    }
    protected List<CommandExecutor> executors;
    private CompositeMode mode;

    public CompositeExecutor(){
        mode = CompositeMode.Single;
        executors = new ArrayList<>();
    }


    @Override
    public boolean canExecute(Command command) {
        // find first executor, which support this kind of command
        for (CommandExecutor executor : executors) {
            if (executor.canExecute(command)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void execute(Command command) {
        // loop over all available executores
        for (CommandExecutor e : executors) {
            // check if executor support this type of command
            if (e.canExecute(command)) {
                e.execute(command);
                // command is processed, check mode of this composite
                if (mode == CompositeMode.Single) {
                    // we're done
                    return;
                }
            }
        }
    }

    public CompositeMode getMode() {
        return mode;
    }

    public void setMode(CompositeMode mode) {
        this.mode = mode;
    }

    /**
     * Assume that many threads can request execution at the same time.
     */
    public void clearAll() {
        synchronized (this) {
            executors.clear();
        }
    }

    public void addExecutor(CommandExecutor executor) {
        synchronized (this) {
            executors.add(executor);
        }
    }


}
