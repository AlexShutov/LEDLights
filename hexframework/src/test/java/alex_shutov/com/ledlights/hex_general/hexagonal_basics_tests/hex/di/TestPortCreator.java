package alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.di;


import javax.inject.Singleton;

import alex_shutov.com.ledlights.hex_general.PortAdapterCreator;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.TestCellDeployer;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.TestLogicCell;
import dagger.Component;

/**
 * Created by lodoss on 03/10/16.
 */

@Singleton
@Component(modules = { TestLogicModule.class })
public interface TestPortCreator extends PortAdapterCreator {
    void injectTestLogicCell(TestLogicCell testLogicCell);
    void injectTestCellDeployed(TestCellDeployer testCellDeployer);

}
