package alex_shutov.com.ledlights;

import android.content.Intent;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import alex_shutov.com.ledlights.sensor.AccelerationReader;
import alex_shutov.com.ledlights.sensor.Reading;
import alex_shutov.com.ledlights.sensor.SensorReader;
import alex_shutov.com.ledlights.sensor.gps.GpsVelocityCalculator;
import alex_shutov.com.ledlights.sensor.gps.LocationManagerReader;
import alex_shutov.com.ledlights.service.BackgroundService;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


/**
 * Created by lodoss on 30/06/16.
 */
public class LEDApplication extends MultiDexApplication {

    private PublishSubject<Double> speedSource = PublishSubject.create();

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
        sensorReader = new LocationManagerReader(this);
        //sensorReader = new AccelerationReader(this, false);
        GpsVelocityCalculator velocityCalculator = new GpsVelocityCalculator(this);
        velocityCalculator.setDecoree(sensorReader);
        sensorReader = velocityCalculator;
        velocityCalculator.setCallback(new SensorReader.SensorReadingCallback() {
            @Override
            public void processSensorReading(Reading reading) {
                Observable.defer(() -> Observable.just(reading))
                        .subscribeOn(Schedulers.computation())
                        .subscribe(r -> logSpeed(r));
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

//        speed = new Reading();

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

    private void logSpeed(Reading speed) {
        // show time in seconds
        int interval = (int)( speed.timeInterval / 1000d);
        float s = (float) speed.values[0];
        Log.i(LOG_TAG, "Average speed for time: " + interval + " is " + s);
        speedSource.onNext(new Double(s));
    }

    public Observable<Double> getSpeedSource() {
        return speedSource.asObservable()
                .observeOn(AndroidSchedulers.mainThread());
    }
}
