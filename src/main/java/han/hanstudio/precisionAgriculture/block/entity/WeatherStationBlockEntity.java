package han.hanstudio.precisionAgriculture.block.entity;

import han.hanstudio.precisionAgriculture.ModBlockEntities;
import han.hanstudio.precisionAgriculture.machine.WeatherStation;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WeatherStationBlockEntity extends BlockEntity {
    private WeatherStation machine;

    public WeatherStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WEATHER_STATION, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, WeatherStationBlockEntity be) {
        if (world.isClient()) return;
        if (be.machine == null) be.machine = new WeatherStation(pos);
        be.machine.serverTick((ServerWorld) world);
    }

    public WeatherStation getStation() { return machine; }
}
