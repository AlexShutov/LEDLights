package alex_shutov.com.ledlights;

import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import alex_shutov.com.ledlights.service.BackgroundService;


/**
 * Created by lodoss on 30/06/16.
 */
public class LEDApplication extends MultiDexApplication {
    private static final String LOG_TAG = LEDApplication.class.getSimpleName();

//    CellDeployer btCellDeployer;
//    BtLogicCell cell;

//    void initCell(){
//        // create cell deployer
//        btCellDeployer = new BtCellDeployer(this);
//        // create new logic cell
//        cell = new BtLogicCell();
//        // deploy this cell- create and createObjects ports, connect ports to the cell
//        btCellDeployer.deploy(cell);
//
//        Context context = ((BtLogicCell) cell).getContext();
//        String msg = context == null ? "Context is null" : "Context is not null, DI work";
//        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        startService();
    }

    @Override
    public void onTerminate() {
        stopService();
        super.onTerminate();
    }

    private void startService() {
        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);
    }

    private void stopService() {
        Intent intent = new Intent(this, BackgroundService.class);
        stopService(intent);
    }
}
