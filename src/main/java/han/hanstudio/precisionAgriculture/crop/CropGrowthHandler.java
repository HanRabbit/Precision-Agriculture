package han.hanstudio.precisionAgriculture.crop;

import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.CropBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class CropGrowthHandler {
    private static int tickCount = 0;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(CropGrowthHandler::onWorldTick);
    }

    private static void onWorldTick(ServerWorld world) {
        if (++tickCount % 20 != 0) return;
        Random rng = world.random;
        SoilManager.get(world).getAll().forEach((pos, soil) -> {
            BlockPos above = pos.up();
            if (!(world.getBlockState(above).getBlock() instanceof CropBlock crop)) return;
            String name = world.getBlockState(above).getBlock().toString();
            String key = name.contains("corn") ? "corn" : name.contains("rice") ? "rice" : "wheat";
            Crop cropData = CropRegistry.get(key);
            if (cropData == null) cropData = CropRegistry.get("wheat");
            float multiplier = cropData.calcGrowthMultiplier(soil);
            if (rng.nextFloat() < multiplier * 0.05f) {
                crop.applyGrowth(world, above, world.getBlockState(above));
            }
        });
    }
}
