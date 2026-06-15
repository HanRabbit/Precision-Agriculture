package han.hanstudio.precisionAgriculture.machine;

import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/** Base class for all agriculture machines. */
public abstract class AgricultureMachine {
    protected final BlockPos pos;
    protected int range;

    protected AgricultureMachine(BlockPos pos, int range) {
        this.pos = pos;
        this.range = range;
    }

    public void setRange(int range) { this.range = range; }

    public abstract void serverTick(ServerWorld world);

    protected void forEachSoilInRange(ServerWorld world, SoilConsumer consumer) {
        SoilManager manager = SoilManager.get(world);
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int dy = 1; dy <= 3; dy++) {
                    BlockPos target = pos.add(x, -dy, z);
                    if (world.getBlockState(target).getBlock() instanceof net.minecraft.block.FarmlandBlock) {
                        consumer.accept(target, manager.getOrCreate(target));
                        break;
                    }
                }
            }
        }
    }

    @FunctionalInterface
    protected interface SoilConsumer {
        void accept(BlockPos pos, SoilData soil);
    }
}
