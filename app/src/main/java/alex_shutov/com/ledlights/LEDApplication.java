package alex_shutov.com.ledlights;

import android.content.Intent;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import alex_shutov.com.ledlights.sensor.AccelerationReader;
import alex_shutov.com.ledlights.sensor.Reading;
import alex_shutov.com.ledlights.sensor.SensorReader;
import alex_shutov.com.ledlights.service.BackgroundService;
import rx.Observable;
import rx.schedulers.Schedulers;


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
        sensorReader = new AccelerationReader(this, false);
        sensorReader.setCallback(new SensorReader.SensorReadingCallback() {
            @Override
            public void processSensorReading(Reading reading) {
                Observable.defer(() -> Observable.just(reading))
                        .subscribeOn(Schedulers.computation())
                        .subscribe(r -> calculateSpeed(r));
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

        speed = new Reading();

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

    private Reading speed;

    private void calculateSpeed(Reading reading) {
        float dt = reading.timeInterval / 1000f;
        float dVX = reading.values[0] * dt;
        float dVY = reading.values[1] * dt;
        float dVZ = reading.values[2] * dt;

        speed.values[0] += dVX;
        speed.values[1] += dVY;
        speed.values[2] += dVZ;

        logReading("Speed: ", speed);
    }

    private void logReading(String tag, Reading reading) {
        Log.i(LOG_TAG, tag + ": " + reading.timeInterval + ", (" +
                reading.values[0] + ", " + reading.values[1] + ", " + reading.values[2] + ").");
    }
}
