package alex_shutov.com.ledlights.db;

import io.realm.RealmObject;

/**
 * Created by lodoss on 07/10/16.
 */
public class Motorcycle extends RealmObject{

    private String brand;
    private String modelName;
    private Integer engineVolume;
    private boolean isSportBike;


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getEngineVolume() {
        return engineVolume;
    }

    public void setEngineVolume(Integer engineVolume) {
        this.engineVolume = engineVolume;
    }

    public boolean isSportBike() {
        return isSportBike;
    }

    public void setSportBike(boolean sportBike) {
        isSportBike = sportBike;
    }
}
