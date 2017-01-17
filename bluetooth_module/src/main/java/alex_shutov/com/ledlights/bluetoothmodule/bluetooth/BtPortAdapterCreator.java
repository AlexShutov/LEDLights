package alex_shutov.com.ledlights.bluetoothmodule.bluetooth;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtAlgorithmicModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtCellModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtCommModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtConnectorModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtScannerModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtStorageModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtPresenterModule;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.SelectAnotherDeviceStrategy;
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
        BtStorageModule.class,
        BtCommModule.class,
        BtAlgorithmicModule.class,
        BtPresenterModule.class})
public interface BtPortAdapterCreator extends PortAdapterCreator {
    void injectBtCellDeployer(BtCellDeployer cellDeployer);
    void injectBtLogicCell(BtLogicCell logicCell);
    void injectBtLogicCellFacade(BtLogicCellFacade facade);
    void injectSelectAnotherDeviceStrategy(SelectAnotherDeviceStrategy strategy);
}
