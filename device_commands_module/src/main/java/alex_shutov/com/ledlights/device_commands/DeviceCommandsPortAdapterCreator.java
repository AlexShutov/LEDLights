package alex_shutov.com.ledlights.device_commands;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.device_commands.di.CellModule;
import alex_shutov.com.ledlights.hex_general.PortAdapterCreator;
import dagger.Component;

/**
 * Created by lodoss on 21/12/16.
 */

@Singleton
@Component(modules = {CellModule.class})
public interface DeviceCommandsPortAdapterCreator extends PortAdapterCreator {

    void injectLogicCell(DeviceCommandsLogicCell logicCell);
    void injectCellDeployer(DeviceCommandsCellDeployer deployer);
}
