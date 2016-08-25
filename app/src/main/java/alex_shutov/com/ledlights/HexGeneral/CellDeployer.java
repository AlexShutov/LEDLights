package alex_shutov.com.ledlights.HexGeneral;

/**
 * Created by lodoss on 24/08/16.
 */

import android.content.Context;

import alex_shutov.com.ledlights.HexGeneral.di.SystemModule;

/**
 * Creates and keep instance of 'PortAdapterCreator'
 * subclass which can create all entities of 'LogicCell'.
 * Each CellDeployer has its own 'SystemModule', providing
 * system features (context, etc), nor single module.
 * It is neccessary, because logic cell is supposed to be
 * running in Service, cells connect to each other by
 * binding those services
 */
public abstract class CellDeployer {

    /** provides basic system features  */
    private SystemModule systemModule;

    PortAdapterCreator portsCreator;

    public CellDeployer(Context context){
        systemModule = new SystemModule(context);
    }

    /** override for deploying concrete cell type */
    public void deploy(LogicCell cell){
        portsCreator = createPortCreator();
        // create and set all instances in LogicCell by
        // using creator we just initialized
        cell.createObjects(portsCreator);
        // create ports, referenced by derived class
        portsCreator.injectCellDeployer(this);
        // connect ports we just created to logic cell
        connectPorts(cell);
    }

    /**
     * Fabric method creating PortAdapterCreator (DI component),
     * override it
     * @return created instance
     */
    public abstract PortAdapterCreator createPortCreator();

    /**
     * Connect all ports to 'LogicCell'
     */
    public abstract void connectPorts(LogicCell logicCell);

    public SystemModule getSystemModule(){
        return systemModule;
    }

    public PortAdapterCreator getPortsCreator() {
        return portsCreator;
    }
}
