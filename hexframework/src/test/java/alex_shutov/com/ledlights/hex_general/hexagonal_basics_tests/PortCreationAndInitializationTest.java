package alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.TestCellDeployer;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.TestLogicCell;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.TestPort;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by lodoss on 04/10/16.
 */
public class PortCreationAndInitializationTest extends HexagonalEntitiesContainer {

    @Before
    public void prepTest(){
        createEntities();

        // creates DI component, creates all entities in LogicCell and CellDeployer
        getCellDeployer().deploy(getLogicCell());
        getLogicCell().init();
    }

    @Test
    public void dummyTest(){
        TestLogicCell logicCell = (TestLogicCell) getLogicCell();
        TestCellDeployer testCellDeployer = (TestCellDeployer) getCellDeployer();

        TestPort testPort = logicCell.getTestPort();
        assertNotNull("TestPort is null - it was not created", testPort);

        testPort.logMessage("123");
        testPort.sendMessage("123");
        testPort.showMessage("123");
    }


    @After
    public void cleanup(){

    }

}
