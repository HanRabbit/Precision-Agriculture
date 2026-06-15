package han.hanstudio.precisionAgriculture.block;

import han.hanstudio.precisionAgriculture.ModBlockEntities;
import han.hanstudio.precisionAgriculture.block.entity.PesticideSprayerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PesticideSprayerBlock extends Block implements BlockEntityProvider {
    public PesticideSprayerBlock(Settings settings) { super(settings); }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PesticideSprayerBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;
        if (!(player instanceof ServerPlayerEntity sp)) return ActionResult.SUCCESS;
        if (world.getBlockEntity(pos) instanceof PesticideSprayerBlockEntity be)
            be.sendOpenPayload(sp);
        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.PESTICIDE_SPRAYER
                ? (w, p, s, be) -> PesticideSprayerBlockEntity.tick(w, p, s, (PesticideSprayerBlockEntity) be)
                : null;
    }
}
