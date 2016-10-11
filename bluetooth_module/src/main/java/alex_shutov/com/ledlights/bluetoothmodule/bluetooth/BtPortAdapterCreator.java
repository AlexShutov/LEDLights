package alex_shutov.com.ledlights.bluetoothmodule.bluetooth;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtCellModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtConnectorModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtScannerModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtStorageManagerModule;
import alex_shutov.com.ledlights.hex_general.PortAdapterCreator;
import alex_shutov.com.ledlights.hex_general.di.SystemModule;
import dagger.Component;

/**
 * Created by lodoss on 24/08/16.
 */

@Singleton
@Component(modules = {SystemModule.class,
        BtCellModule.class,
        BtConnectorModule.class,
        BtScannerModule.class,
        BtStorageManagerModule.class})
public interface BtPortAdapterCreator extends PortAdapterCreator {
    void injectBtCellDeployer(BtCellDeployer cellDeployer);
    void injectBtLogicCell(BtLogicCell logicCell);
}
