package alex_shutov.com.ledlights.bluetooth;

import android.content.Context;
import android.util.Log;

import javax.inject.Inject;

import alex_shutov.com.ledlights.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetooth.di.BtPortModule;
import alex_shutov.com.ledlights.hex_general.CellDeployer;
import alex_shutov.com.ledlights.hex_general.LogicCell;
import alex_shutov.com.ledlights.hex_general.PortAdapterCreator;
import alex_shutov.com.ledlights.hex_general.di.SystemModule;

/**
 * Created by lodoss on 24/08/16.
 */

public class BtCellDeployer extends CellDeployer{

    private static final String LOG_TAG = CellDeployer.class.getSimpleName();
    private Context context;

    // Dagger can only inject public fields
    @Inject
    public BtScanPort btScanPort;
    @Inject
    public BtConnPort btConnPort;

    public BtCellDeployer(Context context){
        this.context = context;
    }

    @Override
    public PortAdapterCreator createPortCreator() {
        BtPortModule portModule = new BtPortModule();
        SystemModule systemModule = new SystemModule(context);
        PortAdapterCreator creator = DaggerBtPortAdapterCreator.builder()
                .systemModule(systemModule)
                .btPortModule(portModule)
                .build();
        return creator;
    }

    @Override
    public void connectPorts(LogicCell logicCell) {
        if (null != btScanPort){
            Log.e(LOG_TAG, "DI did not work , something is broken");
            return;
        }
        BtLogicCell btCell = (BtLogicCell) logicCell;
        /** set port instance, we didn't do that by DI, because logic cell
         * must not know about its ports
         */
        btCell.setBtScanPort(btScanPort);
        btCell.setBtConnPort(btConnPort);
        // TODO: add the rest of ports here

        /* all ports is set, call 'init method from logic cell so
         * it can finish initialization */
        btCell.init();

    }

    @Override
    protected void injectCellDeployer(PortAdapterCreator injector) {
        BtPortAdapterCreator btPortAdapterCreator = (BtPortAdapterCreator) injector;
        btPortAdapterCreator.injectBtCellDeployer(this);
    }


}
