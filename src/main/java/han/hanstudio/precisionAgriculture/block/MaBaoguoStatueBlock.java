package han.hanstudio.precisionAgriculture.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MaBaoguoStatueBlock extends Block {
    private static final Set<BlockPos> POSITIONS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(0, 0, 0, 16, 2, 16),
        Block.createCuboidShape(5, 2, 5, 11, 4, 11)
    );

    public MaBaoguoStatueBlock(Settings settings) { super(settings); }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return SHAPE;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient()) POSITIONS.add(pos.toImmutable());
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        POSITIONS.remove(pos);
        super.onStateReplaced(state, world, pos, moved);
    }

    public static boolean isNearby(World world, BlockPos pos, int range) {
        int r2 = range * range;
        for (BlockPos statue : POSITIONS) {
            if (statue.getSquaredDistance(pos) <= r2) return true;
        }
        return false;
    }
}
