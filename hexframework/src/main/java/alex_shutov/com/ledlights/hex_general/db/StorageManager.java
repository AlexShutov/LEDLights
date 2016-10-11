package alex_shutov.com.ledlights.hex_general.db;


import io.realm.RealmConfiguration;

/**
 * Created by Alex on 10/11/2016.
 */
public abstract class StorageManager {
    /**
     * we can specify name of Realm file on disc, this
     * file will be stored in default directory under
     * filename.realm name.
     */
    private String dbFilename;
    /**
     * configuration of Realm database, created in subclasses.
     */
    private RealmConfiguration dbConfig;

    /**
     * There will be few databases, endependent from each other. We can separate
     * those schemas by using Realm's modules
     * @return
     */
    protected abstract  RealmConfiguration createDbConfiguration();

    public StorageManager(){
        dbFilename = "";
    }




    public String getDbFilename() {
        return dbFilename;
    }

    public void setDbFilename(String dbFilename) {
        this.dbFilename = dbFilename;
    }
}
