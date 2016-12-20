package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.reconnect;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.ConnectionManager;
import rx.Subscription;

/**
 * Created by lodoss on 20/12/16.
 */

public abstract class ReconnectSchedulingStrategy {
    private ReconnectManager boundInstance;

    /**
     * Called when connection manager is started after some break. Imagine, that we suspended
     * it for a while when user choose device by UI and then connection was lost and app
     * start this ReconnectManager again.
     * It is needed, because some reconnect strategies can run indefinite time (presume,
     * repeat connection attempt every 10 seconds
     */
    public abstract void onRestarted();

    public abstract void clearAttemptCounter();

    public abstract boolean shouldContinue();

    public abstract Subscription startReconnectAttempt();

    /**
     * Derived strategy need reference to decorated ConnectionManager, doing actual work.
     * Its reference is needed when strategy has to schedule new connection attempt.
     * @return
     */
    protected ConnectionManager getDecoreeManager() {
        return boundInstance.getDecoreeManager();
    }

    public void setBoundInstance(ReconnectManager boundInstance) {
        this.boundInstance = boundInstance;
    }
}
