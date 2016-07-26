package alex_shutov.com.ledlights.HexGeneral;

/**
 * Created by lodoss on 22/07/16.
 */

public interface PortListener {
    void onPortReady();
    void onCriticalFailure(Exception e);
}
