package alex_shutov.com.ledlights.device_commands.di;

import dagger.Module;

/**
 * Created by lodoss on 21/12/16.
 */

/**
 * Instantiates entites, used by DeviceCommandLogicCell.
 * Each CommPortAdapter has ite own module, which create adapter itself along objects used in it.
 * This module doesn't create adapters.
 */
@Module
public class CellModule {


}
