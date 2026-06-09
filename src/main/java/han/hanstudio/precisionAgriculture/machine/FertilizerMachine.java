package han.hanstudio.precisionAgriculture.machine;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class FertilizerMachine extends AgricultureMachine {
    private static final float FERTILIZE_AMOUNT = 2f;
    private static final int TICK_INTERVAL = 100;
    private int tickCount = 0;

    public FertilizerMachine(BlockPos pos) { super(pos, 3); }

    @Override
    public void serverTick(ServerWorld world) {
        if (++tickCount < TICK_INTERVAL) return;
        tickCount = 0;
        forEachSoilInRange(world, (p, soil) -> {
            if (soil.getFertility() < 60f) {
                soil.setFertility(soil.getFertility() + FERTILIZE_AMOUNT);
            }
        });
        han.hanstudio.precisionAgriculture.soil.SoilManager.get(world).markDirty();
    }
}
