package han.hanstudio.precisionAgriculture.machine;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class FertilizerMachine extends AgricultureMachine {
    private static final float FERTILIZE_AMOUNT = 2f;
    private static final int TICK_INTERVAL = 100;
    private int tickCount = 0;
    private final SimpleInventory inventory = new SimpleInventory(1);
    private int fertilizedLast = 0;

    public FertilizerMachine(BlockPos pos, int range) {
        super(pos, range);
    }

    public SimpleInventory getInventory() { return inventory; }
    public int getFertilizedLast() { return fertilizedLast; }

    @Override
    public void serverTick(ServerWorld world) {
        if (++tickCount < TICK_INTERVAL) return;
        tickCount = 0;
        fertilizedLast = 0;
        if (inventory.getStack(0).isEmpty()) return;
        forEachSoilInRange(world, (p, soil) -> {
            if (soil.getFertility() < 60f) {
                soil.setFertility(soil.getFertility() + FERTILIZE_AMOUNT);
                fertilizedLast++;
            }
        });
        if (fertilizedLast > 0) {
            inventory.getStack(0).decrement(1);
            han.hanstudio.precisionAgriculture.soil.SoilManager.get(world).markDirty();
        }
    }
}
