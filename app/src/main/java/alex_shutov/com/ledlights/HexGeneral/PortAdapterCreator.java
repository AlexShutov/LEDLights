package alex_shutov.com.ledlights.HexGeneral;


/**
 * Created by lodoss on 24/08/16.
 */

public interface PortAdapterCreator {



    // will be overriden by Dagger2 in derived class
    void injectLogicCell(LogicCell cell);

}
