package alex_shutov.com.ledlights.sensor.gps;

/**
 * Created by lodoss on 13/01/17.
 */

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import alex_shutov.com.ledlights.sensor.Reading;
import alex_shutov.com.ledlights.sensor.SensorReader;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * This is an implementation of reader, which track use movement by GPS using old
 * LocationManager (there is another version for FusedLocationAPIs.
 * This class doesn't check if GPS is enabled - it is assumed to be enabled by default and
 * should be checked by code, using this class first. It is so, because both
 * LocationManager and version using FusedLocationApi prompt to enable GPS in the same manner.
 */
public class LocationManagerReader extends SensorReader implements LocationListener {
    // update location at least every second
    private static final long MINIMAL_UPDATE_TIME = 1000;
    // or at east when user move to 10 meters.
    private static final long MINIMAL_UPDATE_DISTANCE = 10;

    private LocationManager locationManager;


    public LocationManagerReader(Context context) {
        super(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

    }

    /**
     * Register this reader as LocationManager to listen for GPS updates with defined
     * minimal update time and accuracy.
     */
    @Override
    protected void startPollingHardwareSensor() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MINIMAL_UPDATE_TIME, MINIMAL_UPDATE_DISTANCE, this);
    }

    /**
     * Unsubscribe from location updates.
     */
    @Override
    protected void stopPollingHardwareSensor() {
        locationManager.removeUpdates(this);
    }

    /**
     * Inherited from LocationListener
     */

    /**
     * Process new location in a background
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        Observable.defer(() -> Observable.just(location))
                .subscribeOn(Schedulers.computation())
                .subscribe(loc -> processLocationUpdate(loc));
    }

    /**
     * We're not interested in monitoring changes in gps state, need location updates only
     * @param provider
     * @param status
     * @param extras
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    /**
     * We need a high accuracy, that is why use single GPS provider, network is not used -
     * don't track changes in provider state
     *
     * @param provider
     */
    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    /**
     * get current time in nanoseconds and save latitude and longitude as values of reading.
     *  When we have reading, give it to base class for further processing
     * @param location
     */
    private void processLocationUpdate(Location location)  {
        long timeStamp = System.nanoTime();
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        Reading reading = new Reading();
        reading.timestamp = timeStamp;
        reading.values[0] = lat;
        reading.values[1] = lon;
        getReadingPipe().onNext(reading);
    }
}
