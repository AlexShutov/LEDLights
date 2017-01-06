package alex_shutov.com.ledlights.sensors;

/**
 * Created by lodoss on 06/01/17.
 */

/**
 * Class, representing value from some sensor.
 * Some reading are in Decart's coordinate system (x, y, z), another (acceleration, speed),
 * rotation, on the other hand, in Eulers (angles). So, call those as general variables axes
 * (x1, x3, x4)
 */
public class SensorReading {
    // readings along three axes
    private double x1Val;
    private double x2Val;
    private double x3Val;
    // capture time
    private long timeStamp;
    // kind of sensor this reading come from
    private ReadingType readingType;

    public double getX1Val() {
        return x1Val;
    }

    public void setX1Val(double x1Val) {
        this.x1Val = x1Val;
    }

    public double getX2Val() {
        return x2Val;
    }

    public void setX2Val(double x2Val) {
        this.x2Val = x2Val;
    }

    public double getX3Val() {
        return x3Val;
    }

    public void setX3Val(double x3Val) {
        this.x3Val = x3Val;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ReadingType getReadingType() {
        return readingType;
    }

    public void setReadingType(ReadingType readingType) {
        this.readingType = readingType;
    }
}
