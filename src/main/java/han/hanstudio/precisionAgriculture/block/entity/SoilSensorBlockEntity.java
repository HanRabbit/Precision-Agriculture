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
        var sw = (net.minecraft.server.world.ServerWorld) world;
        for (net.minecraft.util.math.Direction dir : new net.minecraft.util.math.Direction[]{
                net.minecraft.util.math.Direction.DOWN,
                net.minecraft.util.math.Direction.NORTH,
                net.minecraft.util.math.Direction.SOUTH,
                net.minecraft.util.math.Direction.EAST,
                net.minecraft.util.math.Direction.WEST}) {
            BlockPos target = pos.offset(dir);
            if (world.getBlockState(target).getBlock() instanceof net.minecraft.block.FarmlandBlock)
                return SoilManager.get(sw).getOrCreate(target);
        }
        return null;
    }
}
