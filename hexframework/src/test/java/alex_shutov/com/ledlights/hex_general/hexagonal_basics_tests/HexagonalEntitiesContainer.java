package alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests;

import android.renderscript.Script;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import alex_shutov.com.ledlights.hex_general.CellDeployer;
import alex_shutov.com.ledlights.hex_general.LogicCell;
import alex_shutov.com.ledlights.hex_general.PortAdapterCreator;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.TestCellDeployer;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.TestLogicCell;

/**
 * Created by lodoss on 04/10/16.
 */
public class HexagonalEntitiesContainer {

    private LogicCell logicCell;
    private CellDeployer cellDeployer;
    private PortAdapterCreator portAdapterCreator;

    /**
     * Create LogicCell and CellDeployer
     */
    protected void createEntities(){
        logicCell = new TestLogicCell();
        cellDeployer = new TestCellDeployer();
    }


    public LogicCell getLogicCell() {
        return logicCell;
    }

    public void setLogicCell(LogicCell logicCell) {
        this.logicCell = logicCell;
    }

    public CellDeployer getCellDeployer() {
        return cellDeployer;
    }

    public void setCellDeployer(CellDeployer cellDeployer) {
        this.cellDeployer = cellDeployer;
    }

    public PortAdapterCreator getPortAdapterCreator() {
        return portAdapterCreator;
    }

    public void setPortAdapterCreator(PortAdapterCreator portAdapterCreator) {
        this.portAdapterCreator = portAdapterCreator;
    }
}
