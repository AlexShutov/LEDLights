package alex_shutov.com.ledlights;

import android.content.Intent;
import android.hardware.SensorEvent;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import alex_shutov.com.ledlights.sensor.AccelerationReader;
import alex_shutov.com.ledlights.sensor.HardwareAccelerationReader;
import alex_shutov.com.ledlights.sensor.HighPassFilterSensorDecorator;
import alex_shutov.com.ledlights.sensor.Reading;
import alex_shutov.com.ledlights.sensor.SensorReader;
import alex_shutov.com.ledlights.sensor.UnbiasingDecorator;
import alex_shutov.com.ledlights.sensor.filtering.Filter;
import alex_shutov.com.ledlights.sensor.filtering.FirstOrderHighPassFilter;
import alex_shutov.com.ledlights.service.BackgroundService;


/**
 * Created by lodoss on 30/06/16.
 */
public class LEDApplication extends MultiDexApplication {
    private static final String LOG_TAG = LEDApplication.class.getSimpleName();

    private SensorReader sensorReader;

//    CellDeployer btCellDeployer;
//    BtLogicCell cell;

//    void initCell(){
//        // create cell deployer
//        btCellDeployer = new BtCellDeployer(this);
//        // create new logic cell
//        cell = new BtLogicCell();
//        // deploy this cell- create and createObjects ports, connect ports to the cell
//        btCellDeployer.deploy(cell);
//
//        Context context = ((BtLogicCell) cell).getContext();
//        String msg = context == null ? "Context is null" : "Context is not null, DI work";
//        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        startService();
        sensorReader = new AccelerationReader(this);
        sensorReader.setCallback(new SensorReader.SensorReadingCallback() {
            @Override
            public void processSensorReading(Reading reading) {
                Log.i(LOG_TAG, "TS: " + reading.timeInterval + ", (" +
                        reading.values[0] + ", " + reading.values[1] + ", " + reading.values[2] + ").");
            }

            @Override
            public void onSensorAccuracyChanged(int newAccuracy) {

            }

            @Override
            public void onBeforeStartingReadingSensor() {

            }

            @Override
            public void onAfterStoppedReadingSensor() {

            }
        });
        sensorReader.startReadingSensors();
    }

    @Override
    public void onTerminate() {
        stopService();
        sensorReader.stopReadingSensors();
        super.onTerminate();
    }

    private void startService() {
        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);
    }

    private void stopService() {
        Intent intent = new Intent(this, BackgroundService.class);
        stopService(intent);
    }
}
