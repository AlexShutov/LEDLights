package com.alex.hextest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.alex.hextest.hex.TestCellDeployer;
import com.alex.hextest.hex.TestLogicCell;

/**
 * Created by Alex on 10/1/2016.
 */
public class TestApplication extends Application {
    private static final String LOG_TAG = TestApplication.class.getSimpleName();

    private TestLogicCell testLogicCell;
    private TestCellDeployer testCellDeployer;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "Creating application instance");
    }

    @Override
    public void onTerminate() {
        Log.i(LOG_TAG, "Deleting application instance");
        super.onTerminate();
    }

    public void createObjects(){
        Log.i(LOG_TAG, "Creating objects in TestLogicCell");

        testLogicCell = new TestLogicCell();
        testCellDeployer = new TestCellDeployer(this);

        testCellDeployer.deploy(testLogicCell);
        // logic cell is deployed now (all objects is initialized, we can call init() method
        testLogicCell.init();

    }


}
