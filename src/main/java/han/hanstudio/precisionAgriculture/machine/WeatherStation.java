package han.hanstudio.precisionAgriculture.machine;

import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class WeatherStation extends AgricultureMachine {
    private static final int APPLY_RADIUS = 16;
    private static final int APPLY_INTERVAL = 40;
    private float ambientTemperature;
    private int lightLevel;
    private boolean raining;
    private int tickCount;

    public WeatherStation(BlockPos pos) { super(pos, 0); }

    @Override
    public void serverTick(ServerWorld world) {
        raining = world.isRaining();
        BlockPos sky = pos.up();
        // Combined light at the block above; falls off at night and underground.
        lightLevel = world.getLightLevel(sky);
        ambientTemperature = world.getBiome(pos).value().getTemperature() * 20f;
        if (raining) ambientTemperature -= 2f;
        ambientTemperature -= (15 - lightLevel) * 0.3f;

        if (++tickCount < APPLY_INTERVAL) return;
        tickCount = 0;
        applyTemperatureToNearbySoil(world);
    }

    private void applyTemperatureToNearbySoil(ServerWorld world) {
        SoilManager mgr = SoilManager.get(world);
        boolean dirty = false;
        for (int dx = -APPLY_RADIUS; dx <= APPLY_RADIUS; dx++) {
            for (int dz = -APPLY_RADIUS; dz <= APPLY_RADIUS; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos p = pos.add(dx, dy, dz);
                    if (!(world.getBlockState(p).getBlock() instanceof FarmlandBlock)) continue;
                    SoilData soil = mgr.getOrCreate(p);
                    float current = soil.getTemperature();
                    soil.setTemperature(current + (ambientTemperature - current) * 0.25f);
                    dirty = true;
                    break;
                }
            }
        }
        if (dirty) mgr.markDirty();
    }

    public float getAmbientTemperature() { return ambientTemperature; }
    public int getLightLevel() { return lightLevel; }
    public boolean isRaining() { return raining; }
}
