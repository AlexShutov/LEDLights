package alex_shutov.com.ledlights.bluetoothmodule.bluetooth;

import android.content.Context;
import android.util.Log;

import javax.inject.Inject;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.hex.BtStorageAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtAlgorithmicModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtCellModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtCommModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtConnectorModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtPresenterModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtScannerModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtStorageModule;
import alex_shutov.com.ledlights.hex_general.CellDeployer;
import alex_shutov.com.ledlights.hex_general.LogicCell;
import alex_shutov.com.ledlights.hex_general.PortAdapterCreator;
import alex_shutov.com.ledlights.hex_general.di.SystemModule;

/**
 * Created by lodoss on 24/08/16.
 */

public class BtCellDeployer extends CellDeployer {

    private static final String LOG_TAG = CellDeployer.class.getSimpleName();
    private Context context;
    // Dagger can only inject public fields
    @Inject
    public BtScanAdapter btScanAdapter;
    @Inject
    public BtConnAdapter btConnAdapter;
    @Inject
    public BtStorageAdapter btStorageAdapter;
    @Inject
    public BtCommAdapter btCommAdapter;

    public BtCellDeployer(Context context){
        this.context = context;
    }

    /**
     * Build DI component, which will create all ports and other objects.
     * @return
     */
    @Override
    public PortAdapterCreator createPortCreator() {
        // create di modules
        BtConnectorModule connectorModule = new BtConnectorModule();
        BtScannerModule scannerModule = new BtScannerModule();
        BtCellModule cellModule = new BtCellModule();
        SystemModule systemModule = new SystemModule(context);
        // create database, storing history of bluetooth devices
        BtStorageModule storageModule = new BtStorageModule();
        BtCommModule commModule = new BtCommModule();
        BtAlgorithmicModule algorithmicModule = new BtAlgorithmicModule();
        BtPresenterModule presenterModule = new BtPresenterModule();
        PortAdapterCreator creator = DaggerBtPortAdapterCreator.builder()
                .systemModule(systemModule)
                .btCellModule(cellModule)
                .btConnectorModule(connectorModule)
                .btScannerModule(scannerModule)
                .btStorageModule(storageModule)
                .btCommModule(commModule)
                .btAlgorithmicModule(algorithmicModule)
                .btPresenterModule(presenterModule)
                .build();
        return creator;
    }

    @Override
    public void connectPorts(LogicCell logicCell) {
        if (null == btScanAdapter){
            Log.e(LOG_TAG, "DI did not work , something is broken");
            return;
        }
        BtLogicCell btCell = (BtLogicCell) logicCell;
        /** set port instance, we didn't do that by DI, because logic cell
         * must not know about its ports
         */
        btCell.setBtScanAdapter(btScanAdapter);
        btCell.setBtConnAdapter(btConnAdapter);
        btCell.setBtStorageAdapter(btStorageAdapter);
        btCell.setBtCommAdapter(btCommAdapter);
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
