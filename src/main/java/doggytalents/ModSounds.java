package doggytalents;

import java.util.function.Function;
import java.util.function.Supplier;

import doggytalents.lib.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, Reference.MOD_ID);

    public static final RegistryObject<SoundEvent> WHISTLE_SHORT = register("whistle_short");
    public static final RegistryObject<SoundEvent> WHISTLE_LONG = register("whistle_long");

    private static RegistryObject<SoundEvent> register(final String name) {
        return register(name, SoundEvent::new);
    }

    private static <T extends SoundEvent> RegistryObject<T> register(final String name, final Function<ResourceLocation, T> factory) {
        return register(name, () -> factory.apply(new ResourceLocation(Reference.MOD_ID, name)));
    }

    private static <T extends SoundEvent> RegistryObject<T> register(final String name, final Supplier<T> sup) {
        return SOUNDS.register(name, sup);
    }
}