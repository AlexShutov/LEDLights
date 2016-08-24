package alex_shutov.com.ledlights.HexGeneral;

import android.content.Context;

import javax.inject.Inject;

/**
 * Created by lodoss on 24/08/16.
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
}
