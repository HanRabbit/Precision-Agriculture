package han.hanstudio.precisionAgriculture.block.entity;

import han.hanstudio.precisionAgriculture.ModBlockEntities;
import han.hanstudio.precisionAgriculture.machine.FertilizerMachine;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FertilizerBlockEntity extends BlockEntity {
    private FertilizerMachine machine;

    public FertilizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FERTILIZER, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, FertilizerBlockEntity be) {
        if (world.isClient()) return;
        if (be.machine == null) be.machine = new FertilizerMachine(pos);
        be.machine.serverTick((ServerWorld) world);
    }
}
