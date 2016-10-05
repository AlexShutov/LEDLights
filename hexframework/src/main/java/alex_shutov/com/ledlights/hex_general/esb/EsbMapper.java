package alex_shutov.com.ledlights.hex_general.esb;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lodoss on 05/10/16.
 */
public abstract class EsbMapper {
    /**
     * Green Robot event bus is used here for ESB implementation
     */
    protected EventBus eventBus;

    public EsbMapper(EventBus eventBus){
        this.eventBus = eventBus;
    }

    /**
     * Register mapping in EventBus so it can receive events from event bus. It has to be
     * implemented in derived classes because number and kind of events depends of interface
     * being mapped.
     */
    public abstract void register();

    /**
     * Stop receiving events from event bus - unsubscripe all mappings from it.
     */
    public abstract void unregister();


}
