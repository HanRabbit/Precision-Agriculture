package han.hanstudio.precisionAgriculture.block;

import han.hanstudio.precisionAgriculture.ModBlockEntities;
import han.hanstudio.precisionAgriculture.block.entity.HarvesterBlockEntity;
import han.hanstudio.precisionAgriculture.network.OpenHarvesterPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

public class HarvesterBlock extends Block implements BlockEntityProvider {
    public HarvesterBlock(Settings settings) { super(settings); }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HarvesterBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;
        if (!(player instanceof ServerPlayerEntity sp)) return ActionResult.SUCCESS;
        if (world.getBlockEntity(pos) instanceof HarvesterBlockEntity be)
            ServerPlayNetworking.send(sp, new OpenHarvesterPayload(pos, be.getRange(), be.getHarvestedLast()));
        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.HARVESTER
                ? (w, p, s, be) -> HarvesterBlockEntity.tick(w, p, s, (HarvesterBlockEntity) be)
                : null;
    }
}
