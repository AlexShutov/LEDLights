package alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.interval_sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by Alex on 12/25/2016.
 */

/**
 * Class, allowing to play sequence of time intervals.
 */
public class IntervalSequencePlayer {
    /**
     * Callback for informing keeping calling code up to date about
     * operation progress.
     */
    public interface IntervalSequenceCallback {
        /**
         * SequencPlayer use it for telling that time interval started
         * @param intervalNo index of time interval
         */
        void onIntervalStarted(int intervalNo);

        /**
         * Inform calling code that interval just ended
         * @param intervalNo number of interval
         */
        void onIntevalEnded(int intervalNo);

        /**
         * Time sequence started
         */
        void onSequenceStarted();

        /**
         * Tell called that entire sequence is complete.
         */
        void onSequenceEnded();

        /**
         * Called after sequence is complete, but it should be
         * restarted (was originally scheduled with restart = true)
         */
        void onSequenceRestarted();
    }

    private boolean repeat;
    private List<Long> intervalDurations;
    // index of current time interval
    private int currentIntervalIndex;
    private Subscription timerSubscription;
    private IntervalSequenceCallback callback;


    public void stop() {
        stopIntervalTask();
        currentIntervalIndex = 0;
        intervalDurations = new ArrayList<>();
    }

    public void startSequence(List<Long> intervals, boolean repeat) {
        startSequence(intervals, repeat, false);
    }


    public IntervalSequencePlayer() {
        repeat = false;
        intervalDurations = new ArrayList<>();
    }

    // accessors

    public boolean isInRepeatMode() {
        return repeat;
    }

    public IntervalSequenceCallback getCallback() {
        return callback;
    }

    /**
     * When user change callback - stop all current activities.
     * This is so, because interval sessioin is supposed to be bound to that callback
     * @param callback
     */
    public void setCallback(IntervalSequenceCallback callback) {
        if (isRunning()) {
            stop();
        }
        this.callback = callback;
    }

    // private methods

    /**
     * Check if any time interval is currently running (timer is active)
     * @return
     */
    private boolean isRunning() {
        boolean isRunning =
                null != timerSubscription && !timerSubscription.isUnsubscribed();
        return isRunning;
    }

    /**
     * cancel any active timer task
     */
    private void stopIntervalTask() {
        if (isRunning()) {
            timerSubscription.unsubscribe();
            timerSubscription = null;
        }
    }

    /**
     * Schedule time interval by using concrete implementation
     * @param durationMillis interval duration in milliseconds
     */
    private void startInterval(Long durationMillis) {
        // crate task for scheduling timer for a needed time interval
        Observable<Long> task = Observable.defer(() ->
                Observable.timer(durationMillis, TimeUnit.MILLISECONDS));
        // and activate that task
        timerSubscription = task
                .subscribe(t -> {
            // inform base class of this event so it can schedule
            // next interval
            onIntervalEnded();
        });
    }

    /**
     * This method is called by timer callback when current interval ends.
     * It is called from background thread.
     * Check if sequence ended. If it endede, inform callback of it.
     * Schedule next time interval and inform callback of it otherwise.
     */
    private void onIntervalEnded() {
        // inform caller code that interval have ended
        callback.onIntevalEnded(currentIntervalIndex);
        // check if ended task not the last one
        if (currentIntervalIndex < intervalDurations.size() - 1) {
            // this interval not the last one, select next interval
            long duration = intervalDurations.get(++currentIntervalIndex);
            startInterval(duration);
            callback.onIntervalStarted(currentIntervalIndex);
        } else {
            // this was the last interval in sequence, inform calling code
            // that sequence ended
            callback.onSequenceEnded();
            if (repeat) {
                // tell callback that sequence is about to get restarted
                callback.onSequenceRestarted();
                // schedule another operation, use 'true' argument
                startSequence(intervalDurations, repeat, true);
            }
        }
    }

    /**
     * Start new sequence
     * @param intervals
     * @param repeat
     * @param isRepeating this sequence is scheduled because original sequence has endede and
     *                    should be repeated again. Do not assign any values
     */
    private void startSequence(List<Long> intervals, boolean repeat, boolean isRepeating) {
        // if sequence is repeated, then values will be the same and this sequence
        // is stopped already
        if (!isRepeating) {
            if (intervals.isEmpty()) {
                return;
            }
            // stop active sequence
            if (isRunning()) {
                stop();
            }
            // copy interval durations into new array (the old one might get
            // corrupted or reference to it can cause a  memory leak)
            intervalDurations = new ArrayList<>();
            intervalDurations.addAll(intervals);
            this.repeat = repeat;
        }
        // select first index
        currentIntervalIndex = 0;
        // get length of first interval
        long firstInterval = intervals.get(0);
        // inform listener that sequence begun and first interval started.
        // this is done in background, but it should not cause any time delays
        Observable.defer(() -> Observable.just(firstInterval))
                .subscribeOn(Schedulers.computation())
                .map(t -> {
                    // start first time interval
                    startInterval(t);
                    return t;
                })
                .subscribe(t -> {
                    // inform called that this interval and sequence is
                    // started
                    if (null != callback) {
                        callback.onSequenceStarted();
                        callback.onIntervalStarted(0);
                    }
                });
    }
}
