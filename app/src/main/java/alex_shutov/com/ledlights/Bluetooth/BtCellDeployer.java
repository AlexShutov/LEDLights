package alex_shutov.com.ledlights.Bluetooth;

import android.content.Context;
import android.util.Log;

import javax.inject.Inject;

import alex_shutov.com.ledlights.Bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.Bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.Bluetooth.di.BtPortModule;
import alex_shutov.com.ledlights.HexGeneral.CellDeployer;
import alex_shutov.com.ledlights.HexGeneral.LogicCell;
import alex_shutov.com.ledlights.HexGeneral.PortAdapterCreator;

/**
 * Created by lodoss on 24/08/16.
 */

public class BtCellDeployer extends CellDeployer{
    private static final String LOG_TAG = CellDeployer.class.getSimpleName();

    // Dagger can only inject public fields
    @Inject
    public BtScanPort btScanPort;
    @Inject
    public BtConnPort btConnPort;

    public BtCellDeployer(Context context){
        super(context);
    }

    @Override
    public PortAdapterCreator createPortCreator() {
        BtPortModule portModule = new BtPortModule();
        PortAdapterCreator creator = DaggerBtPortAdapterCreator.builder()
                .systemModule(getSystemModule())
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

        /* all ports is set, call 'init method from logi cell so
         * it can finish initialization */
        btCell.init();


    }
}
