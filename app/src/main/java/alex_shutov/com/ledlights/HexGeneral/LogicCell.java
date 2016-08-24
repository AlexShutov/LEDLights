package alex_shutov.com.ledlights.HexGeneral;

import android.content.Context;

import javax.inject.Inject;

/**
 * Created by lodoss on 24/08/16.
 */

/**
 * LogicCell is a base class for business logic fo this cell
 * Instance creation is done by di (Dagger2) - method init()
 * PortAdapterCreator - di component, providing instances of
 * all entities needed for this LogicCell.
 * CellDeployer creates PortAdapterCreator and passes it in
 * 'init()' method.
 */
public class LogicCell {

    private PortAdapterCreator adaperCreator;

    @Inject
    Context context;

    public void init(PortAdapterCreator creator){
        adaperCreator = creator;
        creator.injectLogicCell(this);
    }

    public Context getContext(){
        return context;
    }

    protected PortAdapterCreator getAdaperCreator(){
        return adaperCreator;
    }
}
