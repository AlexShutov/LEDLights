package alex_shutov.com.ledlights.device_commands.di;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import alex_shutov.com.ledlights.device_commands.main_logic.CommandExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.serialization.LightsSequenceSerializer;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CommandSerializer;
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
    CompositeSerializer provideSerializationStore(
                        @Named("Serializers") List<CommandSerializer> serializers) {
        CompositeSerializer store = new CompositeSerializer();
        // serializer use first fitting serializer
        store.setMode(CompositeMode.Single);
        store.clearAll();
        for (CommandExecutor e : serializers) {
            store.addExecutor(e);
        }
        return store;
    }

    @Provides
    @Singleton
    @Named("Serializers")
    List<CommandSerializer> createAllSerializers() {
        List<CommandSerializer> serializers = new ArrayList<>();
        // add serializer for 'Change color' command
        ChangeColorSerializer changeColorSerializer = new ChangeColorSerializer();
        serializers.add(changeColorSerializer);
        // add serializer for 'Lights sequence' command
        LightsSequenceSerializer lightsSequenceSerializer = new LightsSequenceSerializer();
        serializers.add(lightsSequenceSerializer);

        return serializers;
    }

}
