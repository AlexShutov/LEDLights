package alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests;

/**
 * Created by lodoss on 04/10/16.
 */


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Run tests, checking if dependency injection work with hexagonal framework. There was an
 * issue - we have to use 'Fabric method' in order to create objects in concrete CellDeployer
 * and LogicCell implementations.
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({
        HexagonalDeploymentTest.class,
        PortCreationAndInitializationTest.class
})

public class SuitHexagonalDI {

}
