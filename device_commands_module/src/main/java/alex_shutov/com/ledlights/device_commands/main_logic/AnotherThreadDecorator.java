package alex_shutov.com.ledlights.device_commands.main_logic;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Alex on 12/24/2016.
 */

public class AnotherThreadDecorator implements CommandExecutor {

    private CommandExecutor decoree;
    private boolean useAnotherThread;

    public AnotherThreadDecorator() {
        // use the same thread by default
        useAnotherThread = false;
    }

    @Override
    public boolean canExecute(Command command) {
        return decoree != null ? decoree.canExecute(command) : false;
    }

    @Override
    public void execute(Command command) {
        if (null == decoree) {
            return;
        }
        if (useAnotherThread) {
            // schedult deferred action on background thread
            Observable.defer(() -> Observable.just(command))
                    .subscribeOn(Schedulers.computation())
                    .subscribe(t -> decoree.execute(t));
        } else {
            // just dispatch message on the same thread
            decoree.execute(command);
        }
    }

    public CommandExecutor getDecoree() {
        return decoree;
    }

    public void setDecoree(CommandExecutor decoree) {
        this.decoree = decoree;
    }

    public boolean isUseAnotherThread() {
        return useAnotherThread;
    }

    public void setUseAnotherThread(boolean useAnotherThread) {
        this.useAnotherThread = useAnotherThread;
    }


}
