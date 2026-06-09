package han.hanstudio.precisionAgriculture.block.entity;

import han.hanstudio.precisionAgriculture.ModBlockEntities;
import han.hanstudio.precisionAgriculture.machine.Irrigator;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IrrigatorBlockEntity extends BlockEntity {
    private Irrigator machine;

    public IrrigatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.IRRIGATOR, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, IrrigatorBlockEntity be) {
        if (world.isClient()) return;
        if (be.machine == null) be.machine = new Irrigator(pos);
        be.machine.serverTick((ServerWorld) world);
    }
}
