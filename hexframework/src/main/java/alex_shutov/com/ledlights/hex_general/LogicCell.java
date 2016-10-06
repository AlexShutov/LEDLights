package alex_shutov.com.ledlights.hex_general;

import android.content.Context;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created by lodoss on 24/08/16.
 */

/**
 * LogicCell is a base class for business logic fo this cell
 * Instance creation is done by di (Dagger2) - method createObjects()
 * PortAdapterCreator - di component, providing instances of
 * all entities needed for this LogicCell.
 * CellDeployer creates PortAdapterCreator and passes it in
 * 'createObjects()' method.
 */
public abstract class LogicCell {

    private PortAdapterCreator adaperCreator;


    /**
     * Called by deployer right after deployer initialized its
     * PortAdapterCreator (DI component). this component can create
     * all stuff, this logic cell need
     * @param creator
     */
    public void createObjects(PortAdapterCreator creator){
        adaperCreator = creator;
        injectThisCell();
    }

    /**
     * Deployment process is following:
     * Deployer creates creator and then passes it to this logic cell
     * Logic cell uses that creator to instantiate all entities it need.
     * after that deployer calls its other methods, such as 'createPorts()',
     * and, finally, when every entity in logic cell is set, it call
     * this 'init' method.
     * Intialize all internal dependencies here
     */
    public abstract void init();


    protected abstract void injectThisCell();

    protected PortAdapterCreator getAdaperCreator(){
        return adaperCreator;
    }
}
