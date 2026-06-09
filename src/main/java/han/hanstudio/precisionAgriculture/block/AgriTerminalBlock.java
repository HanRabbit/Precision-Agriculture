package han.hanstudio.precisionAgriculture.block;

import han.hanstudio.precisionAgriculture.ModBlockEntities;
import han.hanstudio.precisionAgriculture.advisor.AdvisorEngine;
import han.hanstudio.precisionAgriculture.block.entity.AgriTerminalBlockEntity;
import han.hanstudio.precisionAgriculture.farm.FarmStats;
import han.hanstudio.precisionAgriculture.network.OpenAgriTerminalPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

public class AgriTerminalBlock extends Block implements BlockEntityProvider {
    public AgriTerminalBlock(Settings settings) { super(settings); }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AgriTerminalBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;
        if (!(player instanceof ServerPlayerEntity sp)) return ActionResult.SUCCESS;
        if (world.getBlockEntity(pos) instanceof AgriTerminalBlockEntity be) {
            FarmStats stats = FarmStats.compute((ServerWorld) world, pos);
            ServerPlayNetworking.send(sp, new OpenAgriTerminalPayload(
                    pos, stats.totalPlots, stats.avgMoisture, stats.avgFertility,
                    stats.avgHealth, stats.pestRate, AdvisorEngine.analyze((ServerWorld) world, pos)));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.AGRI_TERMINAL
                ? (w, p, s, be) -> AgriTerminalBlockEntity.tick(w, p, s, (AgriTerminalBlockEntity) be)
                : null;
    }
}
