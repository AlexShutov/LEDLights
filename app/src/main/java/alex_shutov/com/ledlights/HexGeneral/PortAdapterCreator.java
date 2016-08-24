package alex_shutov.com.ledlights.HexGeneral;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.HexGeneral.di.SystemModule;
import dagger.Component;

/**
 * Created by lodoss on 24/08/16.
 */

@Singleton
@Component(modules = {SystemModule.class})
public interface PortAdapterCreator {



    // will be overriden by Dagger2 in derived class
    void injectLogicCell(LogicCell cell);

}
