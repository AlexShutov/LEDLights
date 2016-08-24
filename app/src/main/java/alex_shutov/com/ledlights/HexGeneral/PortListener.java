package alex_shutov.com.ledlights.HexGeneral;

/**
 * Created by lodoss on 22/07/16.
 */

public interface PortListener {
    /**
     * @param portID  Logic cell implement callbacks from all ports so we
     * need to distinguish which port event come from - it is done by portID
     */
    void onPortReady(int portID);

    /**
     *
     * @param portID  Logic cell implement callbacks from all ports so we
     * need to distinguish which port event come from - it is done by portID
     * @param e
     */
    void onCriticalFailure(int portID, Exception e);
}
