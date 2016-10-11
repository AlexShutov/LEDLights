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

                });

    }


    private void saveObjects(){
        Observable.defer(() -> Observable.just(""))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(t -> {

                });
    }
    private void queryObjects(){
        Observable.defer(() -> Observable.just(""))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(t -> {

                });
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

                        }
                    });
                });
    }

}
