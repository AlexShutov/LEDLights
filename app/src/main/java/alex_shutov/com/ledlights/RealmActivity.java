package alex_shutov.com.ledlights;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import io.realm.Realm;

/**
 * Created by Alex on 10/6/2016.
 */
public class RealmActivity extends Activity {

    private Button btnCreateObjects;
    private Button btnSaveObjects;
    private Button btnQueryObjects;
    private Button btnRemoveObjects;

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

    }
    private void saveObjects(){

    }
    private void queryObjects(){

    }
    private void removeObjects(){

    }

}
