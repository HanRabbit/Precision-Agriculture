package han.hanstudio.precisionAgriculture;

import han.hanstudio.precisionAgriculture.crop.CropGrowthHandler;
import han.hanstudio.precisionAgriculture.network.ModNetworking;
import han.hanstudio.precisionAgriculture.pest.PestSystem;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

public class PrecisionAgriculture implements ModInitializer {

    @Override
    public void onInitialize() {
        ModNetworking.registerServerPayloads();
        ModRegistries.register();
        CropGrowthHandler.register();
        registerSoilTracking();
        registerPestTick();
    }

    private void registerSoilTracking() {
        // Create SoilData when a player interacts with farmland
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (world.isClient()) return ActionResult.PASS;
            if (world.getBlockState(hit.getBlockPos()).getBlock() instanceof FarmlandBlock) {
                SoilManager.get((ServerWorld) world).getOrCreate(hit.getBlockPos());
            }
            return ActionResult.PASS;
        });

        // Tick all soil data every world tick
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            SoilManager.get(world).tickAll(world.isRaining());
        });
    }

    private void registerPestTick() {
        // Run pest spread every 100 world ticks
        final int[] tick = {0};
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (++tick[0] % 100 == 0) PestSystem.tick(world);
        });
    }
}
