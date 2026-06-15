package han.hanstudio.precisionAgriculture.block;

import han.hanstudio.precisionAgriculture.block.entity.FertilizerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import han.hanstudio.precisionAgriculture.ModBlockEntities;

public class FertilizerBlock extends Block implements BlockEntityProvider {
    public FertilizerBlock(Settings settings) { super(settings); }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FertilizerBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.FERTILIZER
                ? (w, p, s, be) -> FertilizerBlockEntity.tick(w, p, s, (FertilizerBlockEntity) be)
                : null;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;
        if (world.getBlockEntity(pos) instanceof FertilizerBlockEntity be) {
            be.sendOpenPayload((ServerPlayerEntity) player);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (world.getBlockEntity(pos) instanceof FertilizerBlockEntity be) {
            be.dropContents();
        }
        super.onStateReplaced(state, world, pos, moved);
    }
}
