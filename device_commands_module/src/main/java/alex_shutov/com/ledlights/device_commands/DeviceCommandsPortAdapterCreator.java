package alex_shutov.com.ledlights.device_commands;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.device_commands.di.CellModule;
import alex_shutov.com.ledlights.device_commands.di.CommPortModule;
import alex_shutov.com.ledlights.device_commands.di.ControlPortModule;
import alex_shutov.com.ledlights.device_commands.di.EmulationModule;
import alex_shutov.com.ledlights.hex_general.PortAdapterCreator;
import dagger.Component;

/**
 * Created by lodoss on 21/12/16.
 */

@Singleton
@Component(modules = {CellModule.class, CommPortModule.class, ControlPortModule.class,
        EmulationModule.class})
public interface DeviceCommandsPortAdapterCreator extends PortAdapterCreator {

    void injectLogicCell(DeviceCommandsLogicCell logicCell);
    void injectCellDeployer(DeviceCommandsCellDeployer deployer);
}
