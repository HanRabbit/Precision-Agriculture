package han.hanstudio.precisionAgriculture.machine;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class Irrigator extends AgricultureMachine {
    private static final float WATER_PER_TICK = 1f;
    private static final int TICK_INTERVAL = 40;
    private int tickCount = 0;

    public Irrigator(BlockPos pos, int range) { super(pos, range); }

    public void setRange(int range) { this.range = range; }

    @Override
    public void serverTick(ServerWorld world) {
        if (++tickCount < TICK_INTERVAL) return;
        tickCount = 0;
        forEachSoilInRange(world, (p, soil) -> {
            if (soil.getMoisture() < 80f) soil.setMoisture(soil.getMoisture() + WATER_PER_TICK);
        });
        han.hanstudio.precisionAgriculture.soil.SoilManager.get(world).markDirty();
    }
}
