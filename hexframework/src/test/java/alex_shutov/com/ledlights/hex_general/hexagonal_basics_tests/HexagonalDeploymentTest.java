package alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.TestCellDeployer;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.TestLogicCell;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.test_logic.TestObjectA;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.test_logic.TestObjectBSingleton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by lodoss on 04/10/16.
 */
public class HexagonalDeploymentTest extends HexagonalEntitiesContainer {

    @Before
    public void initObjects(){
        createEntities();
    }

    @Test
    public void testDeploymentDI(){
        TestLogicCell logicCell = (TestLogicCell) getLogicCell();
        TestCellDeployer testCellDeployer = (TestCellDeployer) getCellDeployer();

        // creates DI component, creates all entities in LogicCell and CellDeployer
        getCellDeployer().deploy(getLogicCell());
        getLogicCell().init();

        assertEquals("Is values the same? ", 1, 1);

        // test if DI created objects in CellDeployer derived class
        TestObjectA objA = testCellDeployer.getObjA();
        assertNotNull("dummy object A in CellDeployer is null, di is not working", objA);

        TestObjectBSingleton objB = testCellDeployer.getObjB();
        assertNotNull("dummy object B in CellDeployer is null, di is not working");

        // test if DI created objects in LogicCell derived class
        objA = logicCell.getTestObjectA();
        assertNotNull("dummy object A in LogicCell is null, di is not working", objA);


    }


}
