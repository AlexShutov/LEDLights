package alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex;

import android.content.Context;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import alex_shutov.com.ledlights.hex_general.CellDeployer;
import alex_shutov.com.ledlights.hex_general.LogicCell;
import alex_shutov.com.ledlights.hex_general.PortAdapterCreator;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.di.DaggerTestPortCreator;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.di.TestLogicModule;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.di.TestPortCreator;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.test_logic.TestObjectA;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.test_logic.TestObjectBSingleton;

/**
 * Created by lodoss on 03/10/16.
 */
public class TestCellDeployer extends CellDeployer {
    private static final String LOG_TAG = TestCellDeployer.class.getSimpleName();


    @Inject
    TestObjectA objA;

    @Inject
    @Singleton
    TestObjectBSingleton objB;

    private TestLogicModule testLogicModule;

    public TestCellDeployer(){
    }

    /**
     * In this method we have to create Dagger2 component, which will be
     * instantiating all objects inside this logic cell TestLogicCell
     * @return
     */
    @Override
    protected PortAdapterCreator createPortCreator() {
        // port creator is dagger component. create modules, used by it.
        createModules();
        TestPortCreator portCreator = DaggerTestPortCreator.builder()
                .testLogicModule(testLogicModule)
                .build();
        return portCreator;
    }

    private void createModules(){
        testLogicModule = new TestLogicModule();
    }

    /**
     * At this point all Port objects is created, we can initialize ports and then connect those
     * @param logicCell
     */
    @Override
    public void connectPorts(LogicCell logicCell) {
        System.out.println(LOG_TAG + " connecting ports in connectPorts()");
        TestLogicCell testLogicCell = (TestLogicCell) logicCell;
        // initialize 'testPort' and other ports inside a cell
        TestPort testPort =  testLogicCell.getTestPort();

        if (objA != null && objB != null){
            System.out.println(LOG_TAG + " Injected context is not null, objects inside TestCellDeployer were" +
                    "created during deployment process");
        } else {
            System.err.println(LOG_TAG + " Objects were not injected during deployment, something is broken");
        }
    }

    @Override
    protected void injectCellDeployer(PortAdapterCreator injector) {
        TestPortCreator testPortCreator = (TestPortCreator) injector;
        testPortCreator.injectTestCellDeployed(this);
    }

    public TestObjectA getObjA() {
        return objA;
    }

    public TestObjectBSingleton getObjB() {
        return objB;
    }
}
