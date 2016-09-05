package alex_shutov.com.ledlights.hex_general;

/**
 * Created by lodoss on 22/07/16.
 */
public interface Port {

    /**
     * Port is implemented by a concrete Adapter which is a 'Facade' in its nature, so
     * it may have complex initialization algorithm. Should be able to set all properties and
     * only then initialize that port. If initialization finish successfully, Port will
     * notify its PortListener by calling onPortReady() callback method. On the other hand,
     * if case of error which cannot be fixed port will call 'onCriticalFailure()' callback
     * method
     */
    void initialize();

    /**
     * Port implement output methods, but we need input methods also.
     * We can achieve this by mixing out and in methods within port 'doX()' and 'onY()' or
     * use separate interface for this. Here I use second approach.
     * @return
     */
    PortListener getPortListener();

    /**
     * Provide info about this port. Adapter will use for deciding whether
     * @return
     */
    PortInfo getPortInfo();

}
