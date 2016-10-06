package alex_shutov.com.ledlights.bluetoothmodule.bluetooth;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di.BtPortModule;
import alex_shutov.com.ledlights.hex_general.PortAdapterCreator;
import alex_shutov.com.ledlights.hex_general.di.SystemModule;
import dagger.Component;

/**
 * Created by lodoss on 24/08/16.
 */

@Singleton
@Component(modules = {SystemModule.class, BtPortModule.class})
public interface BtPortAdapterCreator extends PortAdapterCreator {
    void injectBtCellDeployer(BtCellDeployer cellDeployer);
    void injectBtLogicCell(BtLogicCell logicCell);
}
