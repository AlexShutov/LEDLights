package alex_shutov.com.ledlights.device_commands.main_logic.commands.save_to_ee;

import alex_shutov.com.ledlights.device_commands.main_logic.Command;

/**
 * Created by lodoss on 23/12/16.
 */

public class SaveToEECommand extends Command {

    private Command foregroundCommand;
    private Command backgroundCommand;

    // index of memory cell (ranging from 0 to 7)
    private int cellIndex;
    // load command from memory or save to memory
    boolean isLoadCommand;
    // true if user want to erase cell
    boolean eraseCell;


    @Override
    public int getCommandCode() {
        return 3;
    }

    /**
     * Saving commands to flash memoty is a single run background command.
     * @return
     */
    @Override
    public boolean isForegroundCommand() {
        return false;
    }

    // accessors

    public Command getForegroundCommand() {
        return foregroundCommand;
    }

    public void setForegroundCommand(Command foregroundCommand) {
        this.foregroundCommand = foregroundCommand;
    }

    public Command getBackgroundCommand() {
        return backgroundCommand;
    }

    public void setBackgroundCommand(Command backgroundCommand) {
        this.backgroundCommand = backgroundCommand;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public void setCellIndex(int cellIndex) {
        this.cellIndex = cellIndex;
    }

    public boolean isLoadCommand() {
        return isLoadCommand;
    }

    public void setLoadCommand(boolean loadCommand) {
        isLoadCommand = loadCommand;
    }

    public boolean isEraseCell() {
        return eraseCell;
    }

    public void setEraseCell(boolean eraseCell) {
        this.eraseCell = eraseCell;
    }
}
