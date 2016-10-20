package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Named;
import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPortLogListener;
import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 12/10/16.
 */
@Module
public class BtCommModule {

    /**
     * Create external listener stub, printing warning messages if app not connected to
     * external port.
     * @return
     */
    @Provides
    @Singleton
    @Named("dummy_comm_listener")
    BtCommPortListener provideBtCommPortLogListener(){
        BtCommPortListener listener = new BtCommPortLogListener();
        return listener;
    }



}
