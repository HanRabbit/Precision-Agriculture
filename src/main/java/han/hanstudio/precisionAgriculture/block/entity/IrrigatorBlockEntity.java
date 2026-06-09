package han.hanstudio.precisionAgriculture.block.entity;

import han.hanstudio.precisionAgriculture.ModBlockEntities;
import han.hanstudio.precisionAgriculture.machine.Irrigator;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IrrigatorBlockEntity extends BlockEntity {
    public static final Map<BlockPos, Integer> rangeMap = new ConcurrentHashMap<>();
    public static final Set<BlockPos> activePositions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private int range = 3;
    private Irrigator machine;

    public IrrigatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.IRRIGATOR, pos, state);
    }

    public int getRange() { return range; }

    public void setRange(int r) {
        range = Math.max(1, Math.min(5, r));
        rangeMap.put(getPos().toImmutable(), range);
        if (machine != null) machine.setRange(range);
    }

    public static void tick(World world, BlockPos pos, BlockState state, IrrigatorBlockEntity be) {
        if (world.isClient()) return;
        activePositions.add(pos.toImmutable());
        rangeMap.put(pos.toImmutable(), be.range);
        if (be.machine == null) be.machine = new Irrigator(pos, be.range);
        be.machine.serverTick((ServerWorld) world);
    }

    @Override
    public void markRemoved() {
        activePositions.remove(getPos());
        rangeMap.remove(getPos());
        super.markRemoved();
    }
}
