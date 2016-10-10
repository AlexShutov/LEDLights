package alex_shutov.com.ledlights;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import alex_shutov.com.ledlights.db.Motorcycle;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Alex on 10/6/2016.
 */
public class RealmActivity extends Activity {

    private static final String LOG_TAG = RealmActivity.class.getSimpleName();

    private Button btnCreateObjects;
    private Button btnSaveObjects;
    private Button btnQueryObjects;
    private Button btnRemoveObjects;

    private RealmConfiguration realmConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm_test);
        btnCreateObjects = (Button) findViewById(R.id.art_create_objects);
        btnCreateObjects.setOnClickListener(v -> {
            createObjects();
        });
        btnSaveObjects = (Button) findViewById(R.id.art_save_objects);
        btnSaveObjects.setOnClickListener(v -> {
            saveObjects();
        });
        btnQueryObjects = (Button) findViewById(R.id.art_query_objects);
        btnQueryObjects.setOnClickListener(v -> {
            queryObjects();
        });
        btnRemoveObjects = (Button) findViewById(R.id.art_remove_objects);
        btnRemoveObjects.setOnClickListener(v -> {
            removeObjects();
        });

        realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
        knownMotorcycles = new ArrayList<>();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private List<Motorcycle> knownMotorcycles;

    private void createObjects(){
        // run on background thread
        Observable.defer(() -> Observable.just(""))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(t -> {
                    /*
                    Realm realm = Realm.getDefaultInstance();

                    Motorcycle motorcycle = new Motorcycle();
                    motorcycle.setBrand("Honda");
                    motorcycle.setEngineVolume(600);
                    motorcycle.setModelName("CBR600F4");
                    motorcycle.setSportBike(true);

                    realm.beginTransaction();
                    realm.copyToRealm(motorcycle);
                    realm.commitTransaction();

                    realm.close();
                    */
                    knownMotorcycles.clear();
                    Motorcycle motorcycle = new Motorcycle();
                    motorcycle.setBrand("Honda");
                    motorcycle.setModelName("CBR600F4");
                    motorcycle.setEngineVolume(600);
                    motorcycle.setSportBike(true);
                    knownMotorcycles.addAll(addKnownMotorcycleTemplate(motorcycle));
                    motorcycle.setModelName("CBR600RR");
                    knownMotorcycles.addAll(addKnownMotorcycleTemplate(motorcycle));
                    motorcycle.setModelName("GL1500");
                    motorcycle.setSportBike(false);
                    knownMotorcycles.addAll(addKnownMotorcycleTemplate(motorcycle));

                    motorcycle.setBrand("Yamaha");
                    motorcycle.setModelName("R1");
                    motorcycle.setSportBike(true);
                    knownMotorcycles.addAll(addKnownMotorcycleTemplate(motorcycle));
                    motorcycle.setModelName("R6");
                    knownMotorcycles.addAll(addKnownMotorcycleTemplate(motorcycle));
                });

    }

    private List<Motorcycle> addKnownMotorcycleTemplate(Motorcycle motorcycle){
        ArrayList<Motorcycle> motorcycles = new ArrayList<>();
        for (int i = 0; i < 100; ++i){
            Motorcycle t = new Motorcycle();
            t.setBrand(motorcycle.getBrand());
            t.setEngineVolume(motorcycle.getEngineVolume());
            t.setSportBike(motorcycle.isSportBike());
            t.setModelName(t.getModelName() + " " + (i + 1));
            motorcycles.add(t);
        }
        return motorcycles;
    }

    private void saveObjects(){
        Observable.defer(() -> Observable.just(""))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(t -> {
                    // get default Realm instance
                    Realm realm = Realm.getDefaultInstance();
                    // begin db transaction
                    realm.beginTransaction();
                    realm.copyToRealm(knownMotorcycles);
                    // Tell Realm that transaction is over and we want to save it on disc
                    realm.commitTransaction();
                    // Realm has limited number of instances, release the taken one
                    realm.close();
                });
    }
    private void queryObjects(){
        Observable.defer(() -> Observable.just(""))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(t -> {
                    Realm realm = Realm.getDefaultInstance();
                    queryHonda(realm);


                    realm.close();
                });
    }

    private void queryHonda(Realm realm){
        String brandName = "Honda";
        RealmResults<Motorcycle> hondas =
                realm.where(Motorcycle.class)
                .equalTo("brand", brandName)
                .findAll();
        int numberOfHondas = hondas.size();
        Log.i(LOG_TAG, "Number of Honda motorcycles: " + numberOfHondas);
    }

    private void removeObjects(){
        Observable.defer(() -> Observable.just(""))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(t -> {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.clear(Motorcycle.class);
                        }
                    });
                });
    }

}
