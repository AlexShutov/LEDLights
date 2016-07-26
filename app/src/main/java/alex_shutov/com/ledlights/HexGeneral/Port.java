package alex_shutov.com.ledlights.HexGeneral;

/**
 * Created by lodoss on 22/07/16.
 */
public interface Port {
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
