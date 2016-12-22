package alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.model;

/**
 * Created by lodoss on 22/12/16.
 */

/**
 * Device store color as three bytes for red, green and blue
 */
public class Color {
    private int red;
    private int green;
    private int blue;



    /**
     * create color from 'int' by using system Color class
     * @param color
     */
    public static Color fromSystemColor(int color) {
        Color c = new Color();
        c.setColor(color);
        return c;
    }

    /**
     * Get color components from 'int' by using system Color class and set those to
     * this object.
     * @param color
     */
    public void setColor(int color) {
        int red = android.graphics.Color.red(color);
        setRed(red);
        int green = android.graphics.Color.green(color);
        setGreen(green);
        int blue = android.graphics.Color.blue(color);
        setBlue(blue);
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }
}
