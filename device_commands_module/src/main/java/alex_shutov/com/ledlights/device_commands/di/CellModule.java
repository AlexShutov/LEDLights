package alex_shutov.com.ledlights.device_commands.di;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import alex_shutov.com.ledlights.device_commands.main_logic.AnotherThreadDecorator;
import alex_shutov.com.ledlights.device_commands.main_logic.CommandExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.CompositeExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.emulation.ChangeColorEmulator;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.serialization.LightsSequenceSerializer;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.save_to_ee.serialization.SaveToEESerializer;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.serialization.StrobeSequenceSerializer;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.DeviceEmulationFrame;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulationExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.interval_sequence.IntervalSequencePlayer;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CompositeSerializer;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.change_color.serialization.ChangeColorSerializer;
import dagger.Module;
import dagger.Provides;

import static alex_shutov.com.ledlights.device_commands.main_logic.CompositeExecutor.*;

/**
 * Created by lodoss on 21/12/16.
 */

/**
 * Instantiates entites, used by DeviceCommandLogicCell.
 * Each CommPortAdapter has ite own module, which create adapter itself along objects used in it.
 * This module doesn't create adapters.
 */
@Module
public class CellModule {

    @Provides
    @Singleton
    @Named("CommandSerializationStore")
    CompositeSerializer provideSerializationStore() {
        CompositeSerializer store = new CompositeSerializer();
        // serializer use first fitting serializer
        store.setMode(CompositeMode.Single);
        store.clearAll();

        // add serializer for 'Change color' command
        ChangeColorSerializer changeColorSerializer = new ChangeColorSerializer();
        store.addExecutor(changeColorSerializer);
        // add serializer for 'Lights sequence' command
        LightsSequenceSerializer lightsSequenceSerializer = new LightsSequenceSerializer();
        store.addExecutor(lightsSequenceSerializer);
        // add serializer for strobe flash sequence
        StrobeSequenceSerializer strobeSequenceSerializer = new StrobeSequenceSerializer();
        store.addExecutor(strobeSequenceSerializer);
        // add serializer for saving command to eeprom
        SaveToEESerializer saveToEESerializer = new SaveToEESerializer(store);
        store.addExecutor(saveToEESerializer);
        return store;
    }


    /**
     * Create top-level executor, to which this logic cell will be redirecting all commands.
     * Creation of this entity is moved into DI module, because, by doing so, it is easier
     * to add some additional logic, say, logging.
     * Add another executors to this composite and put it to decorator for background execution
     * @return
     */
    @Provides
    @Singleton
    @Named("CommandCellExecutor")
    CommandExecutor provideTopLevelExecutor(
            @Named("CommandSerializationStore") CompositeSerializer deviceExec,
            @Named("CommandEmulator") DeviceEmulationFrame emulator) {
        CompositeExecutor executor = new CompositeExecutor();
        // handle all incoming commands on another thread.
        executor.addExecutor(deviceExec);
        executor.addExecutor(emulator);
        // put that composite executor into decorator for processing commands
        // in background
        AnotherThreadDecorator bgDecorator = new AnotherThreadDecorator();
        bgDecorator.setUseAnotherThread(true);
        bgDecorator.setDecoree(executor);
        return bgDecorator;
    }

}
