package han.hanstudio.precisionAgriculture.block.entity;

import han.hanstudio.precisionAgriculture.ModBlockEntities;
import han.hanstudio.precisionAgriculture.advisor.AdvisorEngine;
import han.hanstudio.precisionAgriculture.farm.FarmStats;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class AgriTerminalBlockEntity extends BlockEntity {
    public AgriTerminalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AGRI_TERMINAL, pos, state);
    }

    public FarmStats getStats() {
        if (world == null || world.isClient()) return null;
        return FarmStats.compute((ServerWorld) world);
    }

    public List<String> getAdvice() {
        if (world == null || world.isClient()) return List.of();
        return AdvisorEngine.analyze((ServerWorld) world);
    }
}
