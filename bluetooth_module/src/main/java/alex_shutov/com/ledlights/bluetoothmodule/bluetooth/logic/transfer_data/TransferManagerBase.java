package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.transfer_data;

/**
 * Created by lodoss on 19/12/16.
 */

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.BtAlgorithm;

/**
 * Base class for TransferManager, which can be activated and deactivated and hold reference
 * to feedback interface also.  App can have few data data transfer managers and may want to switch
 * between them. By doing so, it have to deactivate old one first and then activate anew.
 */
public abstract class TransferManagerBase extends BtAlgorithm implements TransferManager {

    private TransferManagerFeedback feedback;

    // set feedback, which is used for informing app of some progress and received data
    public void setFeedback(TransferManagerFeedback feedback) {
        this.feedback = feedback;
    }

    public TransferManagerFeedback getFeedback() {
        return feedback;
    }

}
