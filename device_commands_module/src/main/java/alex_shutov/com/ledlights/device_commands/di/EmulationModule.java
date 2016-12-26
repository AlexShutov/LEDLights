package alex_shutov.com.ledlights.device_commands.di;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.emulation.ChangeColorEmulator;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.emulation.LightSequenceEmulator;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.emulation.StrobeSequenceEmulator;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.DeviceEmulationFrame;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulationExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.interval_sequence.IntervalSequencePlayer;
import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Alex on 12/25/2016.
 */

/**
 * Creates all objects used in command emulation
 */
@Module
public class EmulationModule {

    @Provides
    @Singleton
    @Named("EmulatedDeviceScheduler")
    Scheduler provideSchedulerForEmulatedDeviceUpdate() {
        return AndroidSchedulers.mainThread();
    }

    /**
     * Emulator for light sequence executor and strobe executor use
     * SequencePlayer for emulating time intervals.
     * @return
     */
    @Provides
    IntervalSequencePlayer provideSequencePlayer() {
        IntervalSequencePlayer player = new IntervalSequencePlayer();
        return player;
    }

    /**
     * Construct emulator for sequence of colors.
     * @param sequencePlayer
     * @return
     */
    @Provides
    @Singleton
    @Named("LightSequenceEmulator")
    LightSequenceEmulator provideLightSequenceEmulator(
            IntervalSequencePlayer sequencePlayer,
            @Named("EmulatedDeviceScheduler") Scheduler deviceScheduler ) {
        LightSequenceEmulator emulator = new LightSequenceEmulator(sequencePlayer);
        emulator.setUiThreadScheduler(deviceScheduler);
        return emulator;
    }

    /**
     * Construct emulator for sequence of strobe flashes
     * @param sequencePlayer
     * @return
     */
    @Provides
    @Singleton
    @Named("StrobeSequenceEmulator")
    StrobeSequenceEmulator provideStrobeSequenceEmulator(
            IntervalSequencePlayer sequencePlayer,
            @Named("EmulatedDeviceScheduler") Scheduler deviceScheduler ) {
        StrobeSequenceEmulator emulator = new StrobeSequenceEmulator(sequencePlayer);
        emulator.setUiThreadScheduler(deviceScheduler);
        return emulator;
    }

    /**
     * Create and all executors, emulating real device. Those are used by emulator
     * @return
     */
    @Provides
    @Singleton
    @Named("EmulationExecutors")
    List<EmulationExecutor> createEmulationExecutors(
            @Named("LightSequenceEmulator")  LightSequenceEmulator lightSequenceEmulator,
            @Named("StrobeSequenceEmulator") StrobeSequenceEmulator strobeSequenceEmulator) {
        List<EmulationExecutor> execs = new ArrayList<>();
        // Create emulators:
        // Change color command:
        ChangeColorEmulator changeColorEmulator = new ChangeColorEmulator();
        execs.add(changeColorEmulator);
        // Add LightSequence Executor
        execs.add(lightSequenceEmulator);
        // Add emulator for strobe sequence
        execs.add(strobeSequenceEmulator);
        return execs;
    }

    /**
     * Create command emulator, which will be customized in logic cell later on.
     * TODO: it is just empty composite executor because emulation isn't ready yet
     * @return
     */
    @Provides
    @Singleton
    @Named("CommandEmulator")
    DeviceEmulationFrame provideCommandEmulator(
            @Named("EmulationExecutors") List<EmulationExecutor> execs) {
        DeviceEmulationFrame emulator = new DeviceEmulationFrame();
        for (EmulationExecutor e : execs) {
            emulator.addExecutor(e);
        }
        return emulator;
    }
}
