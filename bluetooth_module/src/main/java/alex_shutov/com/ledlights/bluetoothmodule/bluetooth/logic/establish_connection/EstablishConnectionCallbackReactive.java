package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Alex on 11/6/2016.
 */

/**
 * Transforms callback into set of pipelines, emitting events.
 */
public class EstablishConnectionCallbackReactive implements ConnectionManagerCallback {

    public static class CallbackSubscriptionManager {
        public Subscription successSubscription;
        public Subscription failureSubscription;
        public Subscription unsupportedOperationSubscription;

        public CallbackSubscriptionManager(){
            successSubscription = null;
            failureSubscription = null;
            unsupportedOperationSubscription = null;
        }

        public void unsubscribe(){
            if (null != successSubscription && !successSubscription.isUnsubscribed()){
                successSubscription.unsubscribe();
                successSubscription = null;
            }
            if (null != failureSubscription && !failureSubscription.isUnsubscribed()){
                failureSubscription.unsubscribe();
                failureSubscription = null;
            }
            if (null != unsupportedOperationSubscription &&
                    !unsupportedOperationSubscription.isUnsubscribed()){
                unsupportedOperationSubscription.unsubscribe();
                unsupportedOperationSubscription = null;
            }
        }
    }

    private PublishSubject<BtDevice> successPipe = PublishSubject.create();
    private PublishSubject<Boolean> failurePipe = PublishSubject.create();
    private PublishSubject<Boolean> unsupportedOperationPipe = PublishSubject.create();


    @Override
    public void onConnectionEstablished(BtDevice connectedDevice) {
        successPipe.onNext(connectedDevice);
    }

    @Override
    public void onAttemptFailed() {
        failurePipe.onNext(true);
    }

    @Override
    public void onUnsupportedOperation() {
        unsupportedOperationPipe.onNext(true);
    }

    public Observable<BtDevice> getConnectedSource(){
        return successPipe.asObservable().subscribeOn(Schedulers.computation());
    }

    public Observable<Boolean> getFailureSource(){
        return failurePipe.asObservable().subscribeOn(Schedulers.computation());
    }

    public Observable<Boolean> getUnsupportedOperationSource(){
        return unsupportedOperationPipe.asObservable().observeOn(Schedulers.computation());
    }
}
