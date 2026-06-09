package han.hanstudio.precisionAgriculture.block.entity;

import han.hanstudio.precisionAgriculture.ModBlockEntities;
import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SoilSensorBlockEntity extends BlockEntity {
    public SoilSensorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOIL_SENSOR, pos, state); // populated in ModRegistries.register()
    }

    public SoilData getSoilBelow() {
        if (world == null || world.isClient()) return null;
        return SoilManager.get((net.minecraft.server.world.ServerWorld) world).getOrCreate(pos.down());
    }
}
