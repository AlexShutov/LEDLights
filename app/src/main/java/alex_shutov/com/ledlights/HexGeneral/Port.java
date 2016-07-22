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
     * We may need to unplug connected adapter even if is connected and work fine
     * (say, to change implementation of some feature).
     * In this case workflow is following - we call this method, it imform adapter forcing it
     * to suspend any activity. After that adapter notifies Port about successfulness of
     * operation via 'onAdapterUnplugged' method
     */
    void unplugAdapter();

    /**
     * Adapter call this method in case of critical error if it can't function further or
     * if Port told Adapter to disconnect and disconnection ended successfully
     * @param adapter
     * @param somethingHappend
     */
    void onAdapterUnplugged(Adapter adapter, Exception somethingHappend);

    /**
     * Provide info about this port. Adapter will use for deciding whether
     * @return
     */
    PortInfo getPortInfo();

}
