package han.hanstudio.precisionAgriculture.machine;

import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/** Base class for all agriculture machines. */
public abstract class AgricultureMachine {
    protected final BlockPos pos;
    protected final int range;

    protected AgricultureMachine(BlockPos pos, int range) {
        this.pos = pos;
        this.range = range;
    }

    public abstract void serverTick(ServerWorld world);

    protected void forEachSoilInRange(ServerWorld world, SoilConsumer consumer) {
        SoilManager manager = SoilManager.get(world);
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                BlockPos target = pos.add(x, -1, z);
                SoilData soil = manager.get(target);
                if (soil != null) consumer.accept(target, soil);
            }
        }
    }

    @FunctionalInterface
    protected interface SoilConsumer {
        void accept(BlockPos pos, SoilData soil);
    }
}
