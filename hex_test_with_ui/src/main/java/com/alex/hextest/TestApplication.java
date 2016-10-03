package com.alex.hextest;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Alex on 10/1/2016.
 */
public class TestApplication extends Application {
    private static final String LOG_TAG = TestApplication.class.getSimpleName();
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



    }
}
