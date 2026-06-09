package han.hanstudio.precisionAgriculture.block.entity;

import han.hanstudio.precisionAgriculture.ModBlockEntities;
import han.hanstudio.precisionAgriculture.advisor.AdvisorEngine;
import han.hanstudio.precisionAgriculture.farm.FarmStats;
import han.hanstudio.precisionAgriculture.network.SyncAgriTerminalPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class AgriTerminalBlockEntity extends BlockEntity {
    private static final int BROADCAST_INTERVAL = 20;
    private int tickCount = 0;

    public AgriTerminalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AGRI_TERMINAL, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AgriTerminalBlockEntity be) {
        if (world.isClient()) return;
        if (++be.tickCount < BROADCAST_INTERVAL) return;
        be.tickCount = 0;
        ServerWorld sw = (ServerWorld) world;
        FarmStats stats = FarmStats.compute(sw, pos);
        List<String> advice = AdvisorEngine.analyze(sw, pos);
        SyncAgriTerminalPayload payload = new SyncAgriTerminalPayload(
                pos, stats.totalPlots, stats.avgMoisture, stats.avgFertility,
                stats.avgHealth, stats.pestRate, advice);
        for (ServerPlayerEntity p : sw.getPlayers()) {
            if (p.getBlockPos().getSquaredDistance(pos) <= 32 * 32)
                ServerPlayNetworking.send(p, payload);
        }
    }
}
