package alex_shutov.com.ledlights;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import alex_shutov.com.ledlights.db.Motorcycle;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Alex on 10/6/2016.
 */
public class RealmActivity extends Activity {

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


    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void createObjects(){
        // run on background thread
        Observable.defer(() -> Observable.just(""))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(t -> {
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
                });

    }
    private void saveObjects(){

    }
    private void queryObjects(){

    }
    private void removeObjects(){

    }

}
