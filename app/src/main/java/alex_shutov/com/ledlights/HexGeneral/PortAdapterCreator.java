package alex_shutov.com.ledlights.HexGeneral;


/**
 * Created by lodoss on 24/08/16.
 */

public interface PortAdapterCreator {


    /**
     * It will be implemented by using DI in derived class
     * (Dagger2)
     * Creator is used not only for instantiating specific
     * port implementation, but for creating all objects,
     * used in concrete LogicCell implementatioin also
     * Create objects, used by concrete logic cell
     * @param cell
     */
    void injectLogicCell(LogicCell cell);

    /**
     * Instantiate ports for logic cell (called by CellDeployer)
     * @param cellDeployer
     */
    void injectCellDeployer(CellDeployer cellDeployer);

}
