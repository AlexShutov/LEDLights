package alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;

/**
 * Created by lodoss on 22/12/16.
 */

public class ChangeColorCommand extends Command {
    // Color of LED
    private int color;

    /**
     * Command for changinf color is onder index 0 on device
     * @return
     */
    @Override
    public int getCommandCode() {
        return 0;
    }

    /**
     * Command for changing LED color is a foreground command
     * @return
     */
    @Override
    public boolean isForegroundCommand() {
        return true;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
