package alex_shutov.com.ledlights.Bluetooth;

import android.content.Context;

import alex_shutov.com.ledlights.HexGeneral.CellDeployer;
import alex_shutov.com.ledlights.HexGeneral.PortAdapterCreator;

/**
 * Created by lodoss on 24/08/16.
 */

public class BtCellDeployer extends CellDeployer{

    public BtCellDeployer(Context context){
        super(context);
    }

    @Override
    public PortAdapterCreator createPortCreator() {
        PortAdapterCreator creator = DaggerBtPortAdapterCreator.builder()
                .systemModule(getSystemModule()).build();
        return creator;
    }

    @Override
    public void createPorts() {

    }

    @Override
    public void connectPorts() {

    }
}
