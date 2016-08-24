package alex_shutov.com.ledlights.Bluetooth;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.HexGeneral.PortAdapterCreator;
import alex_shutov.com.ledlights.HexGeneral.di.SystemModule;
import dagger.Component;

/**
 * Created by lodoss on 24/08/16.
 */

@Singleton
@Component(modules = {SystemModule.class})
public interface BtPortAdapterCreator extends PortAdapterCreator {

}
