package alex_shutov.com.ledlights.device_commands.main_logic.commands.save_to_ee.serialization;

import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.DataHeader;

/**
 * Created by lodoss on 23/12/16.
 */

public class SaveToEEDataHeader implements DataHeader {

    private int cellIndex;
    private boolean isLoadCommand;
    private boolean eraseCell;
    private boolean hasBackgroundCommand;

    @Override
    public int getHeaderSize() {
        return 4;
    }

    @Override
    public void writeToResult(byte[] result, int offset) {
        result[offset + 0] = (byte) cellIndex;
        result[offset + 1] = (byte) (isLoadCommand ? 1 : 0);
        result[offset + 2] = (byte) (eraseCell ? 1 : 0);
        result[offset + 3] = (byte) (hasBackgroundCommand ? 1 : 0);
    }

    // accessors

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

    public boolean isHasBackgroundCommand() {
        return hasBackgroundCommand;
    }

    public void setHasBackgroundCommand(boolean hasBackgroundCommand) {
        this.hasBackgroundCommand = hasBackgroundCommand;
    }

}
