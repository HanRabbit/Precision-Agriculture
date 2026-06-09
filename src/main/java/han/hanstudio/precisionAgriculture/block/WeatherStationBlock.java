package han.hanstudio.precisionAgriculture.block;

import han.hanstudio.precisionAgriculture.block.entity.WeatherStationBlockEntity;
import han.hanstudio.precisionAgriculture.machine.WeatherStation;
import han.hanstudio.precisionAgriculture.network.OpenWeatherStationPayload;
import han.hanstudio.precisionAgriculture.ModBlockEntities;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WeatherStationBlock extends Block implements BlockEntityProvider {
    public WeatherStationBlock(Settings settings) { super(settings); }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WeatherStationBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.WEATHER_STATION
                ? (w, p, s, be) -> WeatherStationBlockEntity.tick(w, p, s, (WeatherStationBlockEntity) be)
                : null;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;
        if (!(player instanceof ServerPlayerEntity sp)) return ActionResult.PASS;
        if (world.getBlockEntity(pos) instanceof WeatherStationBlockEntity be) {
            WeatherStation ws = be.getStation();
            if (ws == null) {
                sp.sendMessage(Text.literal("§e[气象站] 正在初始化..."), false);
            } else {
                ServerPlayNetworking.send(sp, new OpenWeatherStationPayload(
                        ws.getAmbientTemperature(), ws.getLightLevel(), ws.isRaining()));
            }
        }
        return ActionResult.SUCCESS;
    }
}
