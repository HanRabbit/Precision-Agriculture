package han.hanstudio.precisionAgriculture.screen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static ScreenHandlerType<FertilizerScreenHandler> FERTILIZER;
    public static ScreenHandlerType<PesticideSprayerScreenHandler> PESTICIDE_SPRAYER;

    public static void register() {
        FERTILIZER = Registry.register(Registries.SCREEN_HANDLER,
            Identifier.of("precision-agriculture", "fertilizer"),
            new ScreenHandlerType<>(FertilizerScreenHandler::new, FeatureFlags.VANILLA_FEATURES));

        PESTICIDE_SPRAYER = Registry.register(Registries.SCREEN_HANDLER,
            Identifier.of("precision-agriculture", "pesticide_sprayer"),
            new ScreenHandlerType<>(PesticideSprayerScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
    }
}
