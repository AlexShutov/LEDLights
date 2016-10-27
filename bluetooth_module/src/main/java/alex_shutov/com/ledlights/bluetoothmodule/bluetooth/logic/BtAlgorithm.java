package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtLogicCellFacade;

/**
 * Created by Alex on 10/26/2016.
 */

/**
 * Logic cell in hexagonal architecture is very high level container,
 * having all portrs, port listeners and all glue logic.
 * BtLogicCellFacade is a 'Facade' for business logic itself.
 * Compared to LogicCell, it keeps references only to port interfaces, not
 * adapters.
 * But, despite all that, this subsystem has quite complex logic - we has to
 * establish connectioin, transfer data, handle loss of connection seamlessly,
 * retry if connection is lost. There is a lot of stuff to do..
 * EventBus ease it a bit - we don't have to use complex Observables (a.k.a ESB pattern)
 * Despite ESB presence, all atomic algorithms will be having their own interfaces, so it
 * will be easy to unit test those algorithms.
 */
public abstract class BtAlgorithm {

    /**
     * Concrete algorithm know how to stop itself
     */
    public abstract void suspend();

    /**
     * At this point all references is ready, we can deploy algorithm
     */
    protected abstract void start();

    /**
     * Every algorithm needs limited set of entities from BtLogicCellFacade.
     * Concrete algorithm need to get those entities. This method does it.
     */
    protected abstract void getDependenciesFromFacade(DataProvider dataProvider);

    public void init(DataProvider dataProvider){
        getDependenciesFromFacade(dataProvider);
        start();
    }
}
