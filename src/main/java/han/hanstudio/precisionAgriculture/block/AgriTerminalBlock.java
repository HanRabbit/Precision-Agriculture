package han.hanstudio.precisionAgriculture.block;

import han.hanstudio.precisionAgriculture.block.entity.AgriTerminalBlockEntity;
import han.hanstudio.precisionAgriculture.farm.FarmStats;
import han.hanstudio.precisionAgriculture.network.OpenAgriTerminalPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AgriTerminalBlock extends Block implements BlockEntityProvider {
    public AgriTerminalBlock(Settings settings) { super(settings); }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AgriTerminalBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;
        if (!(player instanceof ServerPlayerEntity sp)) return ActionResult.PASS;
        if (world.getBlockEntity(pos) instanceof AgriTerminalBlockEntity be) {
            FarmStats stats = be.getStats();
            if (stats == null || stats.totalPlots == 0) {
                sp.sendMessage(Text.literal("§c[农业终端] 暂无农田数据。"), false);
                return ActionResult.SUCCESS;
            }
            ServerPlayNetworking.send(sp, new OpenAgriTerminalPayload(
                    stats.totalPlots, stats.avgMoisture, stats.avgFertility,
                    stats.avgHealth, stats.pestRate, be.getAdvice()));
        }
        return ActionResult.SUCCESS;
    }
}
