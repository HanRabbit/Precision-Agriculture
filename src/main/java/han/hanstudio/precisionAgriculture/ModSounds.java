package han.hanstudio.precisionAgriculture;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent FERTILIZER_USE = register("fertilizer_use");

    public static void register() {}

    private static SoundEvent register(String name) {
        Identifier id = Identifier.of("precision-agriculture", name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
}
