package alex_shutov.com.ledlights.hex_general;

/**
 * Created by lodoss on 24/08/16.
 */

import android.util.Log;

/**
 * Creates and keep instance of 'PortAdapterCreator'
 * subclass which can create all entities of 'LogicCell'.
 * Each CellDeployer has its own 'SystemModule', providing
 * system features (context, etc), nor single module.
 * It is neccessary, because logic cell is supposed to be
 * running in Service, cells connect to each other by
 * binding those services.
 * Initial version of this class have had 'System module', responsible for
 * injecting (di) Context instance and all Android- specific systems. But,
 * apparently, it was not very good solution, because this class is a part of hexagonal
 * architecture and should be used in unit tests, that is why it MUST be independent of
 * Android framework (Context). Now SystemModule has to be defined in derived class.
 */
public abstract class CellDeployer {

    PortAdapterCreator portsCreator;

    /** override for deploying concrete cell type */
    public void deploy(LogicCell cell){
        portsCreator = createPortCreator();
        // create and set all instances in LogicCell by
        // using creator we just initialized
        cell.createObjects(portsCreator);
        // create objects in this
        injectCellDeployer(portsCreator);
        // connect ports we just created to logic cell
        connectPorts(cell);
    }

    /**
     * Fabric method creating PortAdapterCreator (DI component),
     * override it.
     * This method is protected, because it is called just once by CellDeployed during
     * deploying logic cell. PortAdapter creater contains DI modules, which should be
     * created once.
     * @return created instance
     */
    protected abstract PortAdapterCreator createPortCreator();

    /**
     * Connect and initialize all ports to 'LogicCell'.
     * The point is that CellDeployer incapsulates entie deployment process, including
     * initialization of ports inside a cell.
     * Port know how to initialize and bind its internal dependencies, but CellDeployer
     * know when to call that method and what arguments to use
     */
    public abstract void connectPorts(LogicCell logicCell);


    protected abstract void injectCellDeployer(PortAdapterCreator injector);

}
