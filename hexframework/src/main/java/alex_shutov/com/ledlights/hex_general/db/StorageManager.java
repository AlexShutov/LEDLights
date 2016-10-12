package alex_shutov.com.ledlights.hex_general.db;


import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Alex on 10/11/2016.
 */
public abstract class StorageManager {
    protected static final String LOG_TAG = StorageManager.class.getSimpleName();
    /**
     * we can specify name of Realm file on disc, this
     * file will be stored in default directory under
     * filename.realm name.
     */
    private String dbFilename;
    /**
     * Realm is Context- dependent
     */
    private Context context;
    /**
     * configuration of Realm database, created in subclasses.
     */
    private RealmConfiguration dbConfig;

    /**
     * There will be few databases, endependent from each other. We can separate
     * those schemas by using Realm's modules.
     * @return
     */
    protected abstract RealmConfiguration buildDbConfiguration();

    public StorageManager(Context context){
        this.context = context;
        dbFilename = "";
    }

    /**
     * Algorithm of using this class is following:
     * - set name of database file
     * - call 'Init' method
     * - use 'allocate' and 'disposeOf' method for accessing database instance
     */
    public void init() {
        // use 'Factory method' to build database configuration
        dbConfig = buildDbConfiguration();
    }

    public Realm allocateInstance(){
        Realm realm = Realm.getInstance(dbConfig);
        return realm;
    }

    public void disposeOfInstance(Realm realm){
        if (null == realm){
            Log.w(LOG_TAG, "Attempting to close null realm instance");
            return;
        }
        realm.close();
    }

    public String getDbFilename() {
        return dbFilename;
    }

    public void setDbFilename(String dbFilename) {
        this.dbFilename = dbFilename;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
