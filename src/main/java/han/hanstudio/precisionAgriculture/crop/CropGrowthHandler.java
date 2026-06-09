package han.hanstudio.precisionAgriculture.crop;

import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.CropBlock;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;

public class CropGrowthHandler {
    private static int tickCount = 0;
    private static final float FERTILITY_PER_GROWTH = 2f;
    private static final float FERTILITY_MIN = 10f;
    // ticks without crop before pest reset (20 ticks/s * 60s * 5min = 6000)
    private static final int BARE_TICKS_RESET = 6000;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(CropGrowthHandler::onWorldTick);
    }

    private static void onWorldTick(ServerWorld world) {
        if (++tickCount % 20 != 0) return;
        Random rng = world.random;
        SoilManager mgr = SoilManager.get(world);
        boolean dirty = false;
        for (var entry : mgr.getAll().entrySet()) {
            BlockPos pos = entry.getKey();
            SoilData soil = entry.getValue();
            BlockPos above = pos.up();
            boolean hasCrop = world.getBlockState(above).getBlock() instanceof CropBlock;

            if (!hasCrop) {
                if (soil.getPestType() != null) {
                    soil.setPestType(null);
                    soil.setBareTickCount(0);
                    dirty = true;
                    continue;
                }
                int bare = soil.getBareTickCount() + 20;
                soil.setBareTickCount(bare);
                if (bare >= BARE_TICKS_RESET) soil.setBareTickCount(0);
                continue;
            }

            soil.setBareTickCount(0);
            if (soil.getFertility() < FERTILITY_MIN) continue;

            CropBlock crop = (CropBlock) world.getBlockState(above).getBlock();
            Identifier id = Registries.BLOCK.getId(crop);
            Crop cropData = CropRegistry.findByBlockId(id);
            int light = world.getLightLevel(LightType.SKY, above);
            float multiplier = cropData.calcGrowthMultiplier(soil, light);
            if (rng.nextFloat() < multiplier * 0.05f) {
                crop.applyGrowth(world, above, world.getBlockState(above));
                soil.setFertility(soil.getFertility() - FERTILITY_PER_GROWTH);
                dirty = true;
            }
        }
        if (dirty) mgr.markDirty();
    }
}
