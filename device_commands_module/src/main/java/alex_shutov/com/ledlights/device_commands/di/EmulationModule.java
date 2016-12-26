package alex_shutov.com.ledlights.device_commands.di;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.emulation.ChangeColorEmulator;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.emulation.LightSequenceEmulator;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.DeviceEmulationFrame;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulationExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.interval_sequence.IntervalSequencePlayer;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Alex on 12/25/2016.
 */

/**
 * Creates all objects used in command emulation
 */
@Module
public class EmulationModule {

    /**
     * Emulator for lisghts sequence executor and strobe executor use
     * SequencePlayer for emulating time intervals.
     * @return
     */
    @Provides
    IntervalSequencePlayer provideSequencePlayer() {
        IntervalSequencePlayer player = new IntervalSequencePlayer();
        return player;
    }

    @Provides
    @Singleton
    @Named("LightSequenceEmulator")
    LightSequenceEmulator provideLightSequenceEmulator(IntervalSequencePlayer sequencePlayer) {
        LightSequenceEmulator emulator = new LightSequenceEmulator(sequencePlayer);
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
            @Named("LightSequenceEmulator") LightSequenceEmulator lightSequenceEmulator) {
        List<EmulationExecutor> execs = new ArrayList<>();
        // Create emulators:
        // Change color command:
        ChangeColorEmulator changeColorEmulator = new ChangeColorEmulator();
        execs.add(changeColorEmulator);
        // Add LightSequence Executor
        execs.add(lightSequenceEmulator);
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
