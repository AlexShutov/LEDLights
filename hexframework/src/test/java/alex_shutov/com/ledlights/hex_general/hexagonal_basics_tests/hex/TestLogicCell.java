package alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex;
import android.util.Log;


import javax.inject.Inject;

import alex_shutov.com.ledlights.hex_general.LogicCell;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.di.TestPortCreator;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.test_logic.TestObjectA;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.test_logic.TestObjectBSingleton;

/**
 * Created by lodoss on 03/10/16.
 */
public class TestLogicCell extends LogicCell {

    private static final String LOG_TAG = TestLogicCell.class.getSimpleName();

    @Inject
    public TestPortAdapter testPortAdapter;
    @Inject
    public TestObjectA testObjectA;
    @Inject
    public TestObjectBSingleton testObjectBSingleton;




    public TestPort getTestPort(){
        Log.i(LOG_TAG, "getTestPort()");
        return testPortAdapter;
    }


    @Override
    public void init() {
        Log.i(LOG_TAG, "init() method is called");


    }


    @Override
    protected void injectThisCell() {
        Log.i(LOG_TAG, "Injecting logi cell TestLogicCell ");
        TestPortCreator portCreator = (TestPortCreator) getAdaperCreator();
        portCreator.injectTestLogicCell(this);
    }
}
